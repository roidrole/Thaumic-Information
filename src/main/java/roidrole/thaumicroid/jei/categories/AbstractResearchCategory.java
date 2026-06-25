package roidrole.thaumicroid.jei.categories;

import mezz.jei.api.recipe.IRecipeCategory;
import roidrole.thaumicroid.Tags;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractResearchCategory<T extends HasResearch> implements IRecipeCategory<T> {

	public static ArrayList<AbstractResearchCategory<?>> categories;

	public Collection<T> recipes;

	public abstract void populateRecipes();

	public AbstractResearchCategory() {
		categories.add(this);
	}

	@Override
	public final String getModName() {
		return Tags.MOD_NAME;
	}
}
