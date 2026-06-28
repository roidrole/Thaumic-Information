package roidrole.thaumicinfo.visualores;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import roidrole.thaumicinfo.Tags;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

	public static void preInit() {
		INSTANCE.registerMessage(PacketAuraToClient.Handler.class, PacketAuraToClient.class, 0, Side.CLIENT);
	}
}