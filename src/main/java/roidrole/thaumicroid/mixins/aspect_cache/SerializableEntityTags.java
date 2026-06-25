package roidrole.thaumicroid.mixins.aspect_cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.io.ObjectStreamException;
import java.io.Serializable;

@Mixin(ThaumcraftApi.EntityTags.class)
public abstract class SerializableEntityTags implements Serializable {
	@Shadow(remap = false)
	public String entityName;

	@Shadow(remap = false)
	public AspectList aspects;

	@Shadow(remap = false)
	public ThaumcraftApi.EntityTagsNBT[] nbts;

	//Because there is no no-arg constructor
	private Object writeReplace() throws ObjectStreamException {
		return new roidrole.thaumicroid.utils.SerializableEntityTags(this.entityName, this.aspects, this.nbts);
	}
}
