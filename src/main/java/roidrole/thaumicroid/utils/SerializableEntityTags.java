package roidrole.thaumicroid.utils;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

//The ObjectInputStream needs a no-arg constructor, and mixins cannot add this constructor, so I need an extending class.
public class SerializableEntityTags extends ThaumcraftApi.EntityTags implements Externalizable {
	public SerializableEntityTags() {
		super(null, null);
	}

	public SerializableEntityTags(String entityName, AspectList aspects, ThaumcraftApi.EntityTagsNBT[] nbts) {
		super(entityName, aspects, nbts);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.entityName);
		out.writeObject(this.aspects);
		if(this.nbts == null){
			out.write(0);
		} else {
			out.write(this.nbts.length);
			for (ThaumcraftApi.EntityTagsNBT currentTag : this.nbts) {
				out.writeObject(currentTag.name);
				out.writeObject(currentTag.value);
			}
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.entityName = (String) in.readObject();
		this.aspects = (AspectList) in.readObject();
		this.nbts = new ThaumcraftApi.EntityTagsNBT[in.read()];
		for (int i = 0; i < this.nbts.length; i++) {
			this.nbts[i] = new ThaumcraftApi.EntityTagsNBT((String)in.readObject(), in.readObject());
		}
	}
}
