package roidrole.thaumicroid.mixins.dioptra_aura;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import roidrole.thaumicroid.Tags;
import roidrole.thaumicroid.visualores.PacketAuraToClient;
import roidrole.thaumicroid.visualores.PacketHandler;
import roidrole.thaumicroid.visualores.TileDioptraAddition;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.devices.TileDioptra;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.*;

@Mixin(
	value = TileDioptra.class,
	remap = false
)
public abstract class TileDioptraMixin extends TileThaumcraft implements TileDioptraAddition {
	@Unique
	Collection<UUID> thaumicroid_playersToSync;

	@Override
	public Collection<UUID> thaumicroid_getPlayersToSync() {
		return thaumicroid_playersToSync;
	}

	@Unique
	private static final String NBT_KEY = Tags.MOD_ID + "_playerlist";

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void initPlayerList(CallbackInfo ci){
		this.thaumicroid_playersToSync = new TreeSet<>();
	}

	@Inject(
		//uodate()
		method = "func_73660_a",
		at = @At("TAIL")
	)
	@SuppressWarnings("all")
	private void sendToPlayers(CallbackInfo ci){
		if(this.world.getWorldTime() % 64 != 1){
			return;
		}
		if(!(this.world instanceof WorldServer)){
			return;
		}
		WorldServer world = (WorldServer) this.world;
		List<EntityPlayerMP> playersToSend = new ArrayList<>(thaumicroid_playersToSync.size());
		for(UUID uuid: thaumicroid_playersToSync){
			EntityPlayerMP player = world.getMinecraftServer().getPlayerList().getPlayerByUUID(uuid);
			if(player == null){
				continue;
			}
			playersToSend.add(player);
		}
		if(playersToSend.isEmpty()){
			return;
		}
		PacketAuraToClient packet = new PacketAuraToClient((this.pos.getX() >> 4) - 6, (this.pos.getZ() >> 4) - 6);
		for(EntityPlayerMP player : playersToSend){
			PacketHandler.INSTANCE.sendTo(
				packet,
				player
			);
		}
	}

	@Inject(
		method = "writeSyncNBT",
		at = @At("HEAD")
	)
	public void writePlayerList(NBTTagCompound nbt, CallbackInfoReturnable<NBTTagCompound> cir) {
		//NBTTagLongArray is private and unsupported. It's this or mixins.
		ByteBuffer byteBuffer = ByteBuffer.allocate(thaumicroid_playersToSync.size() * 2 * Long.BYTES);
		LongBuffer longBuffer = byteBuffer.asLongBuffer();
		for(UUID uuid : this.thaumicroid_playersToSync){
			longBuffer.put(uuid.getMostSignificantBits());
			longBuffer.put(uuid.getLeastSignificantBits());
		}
		nbt.setTag(NBT_KEY, new NBTTagByteArray(byteBuffer.array()));
	}

	@Inject(
		method = "readSyncNBT",
		at = @At("HEAD")
	)
	public void readPlayerList(NBTTagCompound nbt, CallbackInfo ci) {
		//NBTTagLongArray is private and unsupported. It's this or mixins.
		ByteBuffer byteBuffer = ByteBuffer.wrap(((NBTTagByteArray)nbt.getTag(NBT_KEY)).getByteArray());
		LongBuffer longBuffer = byteBuffer.asLongBuffer();
		while(longBuffer.hasRemaining()){
			this.thaumicroid_playersToSync.add(new UUID(longBuffer.get(), longBuffer.get()));
		}
	}

}
