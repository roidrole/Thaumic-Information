package roidrole.thaumicinfo.utils;

import thaumcraft.api.aspects.AspectList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ThaumicInfoUtils {
	public static List<AspectList> splitAspectList(AspectList orginal){
		return orginal.aspects.entrySet().stream()
			// `-entry.getValue()` because natural order is ascending, and we want descending
			.sorted(Comparator.comparingInt(entry -> -entry.getValue()))
			.map(entry -> new AspectList().add(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
	}

	//Helper method to work with JEI
	public static <T> List<List<T>> nestedSingletonList(T element){
		return Collections.singletonList(Collections.singletonList(element));
	}
}
