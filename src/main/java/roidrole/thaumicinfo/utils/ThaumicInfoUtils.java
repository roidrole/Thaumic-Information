package roidrole.thaumicinfo.utils;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;

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

	public static ResearchEntry getEntry(String name){
		if(name == null){
			return null;
		}
		String processedName;
		int indexOfAt = name.indexOf('@');
		if(indexOfAt == -1){
			processedName = name;
		} else {
			processedName = name.substring(0, indexOfAt);
		}
		ResearchEntry research;
		for(ResearchCategory category : ResearchCategories.researchCategories.values()){
			research = category.research.get(processedName);
			if(research != null){
				return research;
			}
		}
		return null;
	}
}
