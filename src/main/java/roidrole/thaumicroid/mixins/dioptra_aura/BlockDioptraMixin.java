package roidrole.thaumicroid.mixins.dioptra_aura;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import roidrole.thaumicroid.visualores.TileDioptraAddition;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.devices.BlockDioptra;
import thaumcraft.common.tiles.devices.TileDioptra;

@Mixin(BlockDioptra.class)
public abstract class BlockDioptraMixin extends BlockTCDevice {
	private BlockDioptraMixin(Material mat, Class tc, String name) {
		super(mat, tc, name);
	}

	@Inject(
		//onBlockActivated
		method = "func_180639_a",
		at = @At("TAIL"),
		remap = false
	)
	@SuppressWarnings("all")
	private void addPlayerToList(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir){
		if(world.isRemote){
			return;
		}

		TileDioptra dioptra = (TileDioptra) world.getTileEntity(pos);
		((TileDioptraAddition) dioptra).thaumicroid_getPlayersToSync().add(player.getUniqueID());
	}
}
