package roidrole.thaumicinfo.mixins.dioptra_aura;

import hellfall.visualores.database.thaumcraft.AuraFluxPosition;
import hellfall.visualores.database.thaumcraft.TCDimensionCache;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TCDimensionCache.class)
public interface TCDimensionCacheAccessor {
	@Accessor(remap = false)
	Object2ObjectMap<ChunkPos, AuraFluxPosition> getChunks();
}
