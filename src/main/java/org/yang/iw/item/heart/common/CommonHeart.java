package org.yang.iw.item.heart.common;

import net.minecraft.item.ItemStack;
import org.yang.iw.item.heart.AbstractHeart;

import java.util.function.Function;

public abstract class CommonHeart extends AbstractHeart
{


	public CommonHeart(Settings settings, Function<Integer, Integer> enchantAbility)
	{
		super(settings, enchantAbility, 8);
	}

	public boolean supportAbility()
	{
		return true;
	}

	public ItemStack boostStackByLevel(int lvl, ItemStack stack)
	{
		return stack;
	}

	@Override
	public ItemStack getDefaultStack(int materialLevel)
	{
		return boostStackByLevel(materialLevel, super.getDefaultStack(materialLevel));
	}
}
