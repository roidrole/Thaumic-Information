package roidrole.thaumicroid.mixins.aspect_cache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(AspectList.class)
public abstract class SerializableAspectList implements Externalizable {
	@Shadow(remap = false)
	public LinkedHashMap<Aspect, Integer> aspects;

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.write(this.aspects.size());
		for (Map.Entry<Aspect, Integer> entry : this.aspects.entrySet()) {
			out.writeObject(entry.getKey().getTag());
			out.write(entry.getValue());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int limit = in.read();
		this.aspects = new LinkedHashMap<>((int)(1.5 * limit));
		for (int i = 0; i < limit; i++) {
			this.aspects.put(
				Aspect.getAspect((String)in.readObject()),
				in.read()
			);
		}
	}
}
