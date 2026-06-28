package roidrole.thaumicinfo.mixins.dioptra_aura;

import hellfall.visualores.database.thaumcraft.TCClientCache;
import hellfall.visualores.database.thaumcraft.TCDimensionCache;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TCClientCache.class)
public interface TCClientCacheAccessor {
	@Accessor(remap = false)
	Int2ObjectMap<TCDimensionCache> getCache();
}
