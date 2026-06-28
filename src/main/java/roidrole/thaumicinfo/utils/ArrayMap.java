package roidrole.thaumicinfo.utils;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

//A partial Int2ObjectMap implementation
public class ArrayMap<T> {
	T[] data;

	@SuppressWarnings("unchecked")
	public ArrayMap(){
		this.data = (T[])new Object[16];
	}

	public int size() {
		return data.length;
	}

	public boolean containsKey(int key) {
		return key < size();
	}

	public T computeIfAbsent(int key, Supplier<T> supplier){
		T out = get(key);
		if(out != null){
			return out;
		}
		data[key] = supplier.get();
		return data[key];
	}

	public T get(int key) {
		if(!containsKey(key)){
			grow(key);
		}
		return data[key];
	}

	public void forEach(BiConsumer<Integer, T> consumer){
		for (int i = data.length - 1; i != 0; i--) {
			if(data[i] != null){
				consumer.accept(i, data[i]);
			}
		}
	}

	private void grow(int minCapacity) {
		// overflow-conscious code
		int oldCapacity = data.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 1) {
			newCapacity = minCapacity + 1;
		}
		// minCapacity is usually close to size, so this is a win:
		data = Arrays.copyOf(data, newCapacity);
	}
}