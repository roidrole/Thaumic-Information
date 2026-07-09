package roidrole.thaumicinfo.visualores;

import hellfall.visualores.database.thaumcraft.AuraFluxPosition;
import hellfall.visualores.database.thaumcraft.TCClientCache;
import hellfall.visualores.database.thaumcraft.TCDimensionCache;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import roidrole.thaumicinfo.mixins.dioptra_aura.TCClientCacheAccessor;
import roidrole.thaumicinfo.mixins.dioptra_aura.TCDimensionCacheAccessor;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.world.aura.AuraWorld;

import java.util.Map;

public class PacketAuraToClient implements IMessage {
	int startX;
	int startZ;
	private static int range;
	private static int rangeSq;
	ByteBuf payload;

	public static void setRadius(int range){
		PacketAuraToClient.range = 2 * range + 1;
		PacketAuraToClient.rangeSq = PacketAuraToClient.range * PacketAuraToClient.range;
	}

	static {
		setRadius(ThaumicInformationConfig.visualOresConfig.dioptra.radius);
	}

	public PacketAuraToClient() {}

	public PacketAuraToClient(int startX, int startZ){
		this.startX = startX;
		this.startZ = startZ;
		AuraWorld auraWorld = AuraHandler.getAuraWorld(0);
		this.payload = Unpooled.buffer(rangeSq * (Short.BYTES + Float.BYTES + Float.BYTES));
		for (int x = startX; x < startX + range; x++){
			for (int z = startZ; z < startZ + range; z++){
				AuraChunk auraChunk = auraWorld.getAuraChunkAt(x, z);
				if(auraChunk == null){
					payload.writeShort(-1);
				} else {
					payload.writeShort(auraChunk.getBase());
					payload.writeFloat(auraChunk.getVis());
					payload.writeFloat(auraChunk.getFlux());
				}
			}
		}
	}

	public void toBytes(ByteBuf dos) {
		dos.writeInt(startX);
		dos.writeInt(startZ);
		dos.writeBytes(payload, 0, payload.readableBytes());
	}

	public void fromBytes(ByteBuf dat) {
		this.startX = dat.readInt();
		this.startZ = dat.readInt();
		//Need to copy the data provided because its reference count is reflectively set to 0
		this.payload = dat.readBytes(dat.readableBytes());
	}

	public static class Handler implements IMessageHandler<PacketAuraToClient, IMessage> {
		public IMessage onMessage(final PacketAuraToClient message, MessageContext ctx) {
			//Doing the AuraFluxPosition recreation off-thread
			AuraFluxPosition[] contents = new AuraFluxPosition[rangeSq];
			for (int i = 0; i < rangeSq; i++) {
				short base = message.payload.readShort();
				if(base == -1){
					contents[i] = null;
					continue;
				}
				int posX = message.startX + (i / range);
				int posZ = message.startZ + (i % range);
				contents[i] = new AuraFluxPosition(base, message.payload.readFloat(), message.payload.readFloat(), posX, posZ);
			}

			//Scheduling because hashMap access off-thread is unsafe
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Int2ObjectMap<TCDimensionCache> tcCache = ((TCClientCacheAccessor)TCClientCache.instance).getCache();
				TCDimensionCache dimCache = tcCache.computeIfAbsent(0, key -> new TCDimensionCache());
				Map<ChunkPos, AuraFluxPosition> chunkCache = ((TCDimensionCacheAccessor)dimCache).getChunks();

				for (AuraFluxPosition pos : contents){
					if(pos == null){
						continue;
					}
					chunkCache.put(new ChunkPos(pos.x, pos.z), pos);
				}
			});
			return null;
		}
	}
}