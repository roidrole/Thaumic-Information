package roidrole.thaumicroid.mixins.aspect_cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.thaumicroid.CacheManager;
import roidrole.thaumicroid.ThaumicRoid;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.config.ConfigAspects;

@Mixin(ConfigAspects.class)
public abstract class ReadCaches {
	@Inject(
		method = "postInit",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void thaumicroid_readCaches(CallbackInfo ci){
		if(CacheManager.canRunCaches()){
			ThaumicRoid.LOGGER.info("Loading caches");
			CacheManager.parseAspectCache();
			CacheManager.parseEntityCache();
			ThaumicRoid.LOGGER.info("Loaded {} aspect entries from cache", CommonInternals.objectTags.size());
			ci.cancel();
		}
	}
}
