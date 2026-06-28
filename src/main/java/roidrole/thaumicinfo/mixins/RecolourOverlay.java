package roidrole.thaumicinfo.mixins;

import hellfall.visualores.database.thaumcraft.AuraFluxPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import roidrole.thaumicinfo.ThaumicInformationConfig;

@Mixin(AuraFluxPosition.class)
public abstract class RecolourOverlay {

	@Shadow(remap = false)
	public float flux;

	@ModifyVariable(
		method = "<init>",
		name = "alpha",
		at = @At(
			value = "STORE",
			target = "alpha"
		),
		remap = false
	)
	int configurableAlphaBorder(int alpha){
		return Math.min(
			ThaumicInformationConfig.visualOresConfig.overlay.max_value_border,
			(int) Math.floor(flux * ThaumicInformationConfig.visualOresConfig.overlay.multiplier_border)
		);
	}

	@ModifyVariable(
		method = "<init>",
		name = "midAlpha",
		at = @At(
			value = "STORE",
			target = "midAlpha"
		),
		remap = false
	)
	int configurableAlphaCenter(int midAlpha){
		return Math.min(
			ThaumicInformationConfig.visualOresConfig.overlay.max_value_center,
			(int)Math.floor(flux * ThaumicInformationConfig.visualOresConfig.overlay.multiplier_center)
		);
	}


	@Redirect(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/awt/Color;HSBtoRGB(FFF)I"
		),
		remap = false
	)
	int configurableConstantColor(float hue, float saturation, float brightness) {
		return ThaumicInformationConfig.visualOresConfig.overlay.color;
	}

}
