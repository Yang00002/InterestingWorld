package org.yang.interestingworld.rune;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static org.yang.interestingworld.IWUtil.TextStyle.WHITE_RGB;

public class IWAbstractRuneUpgrade
{
	public short index = 0;
	public short runeIndex = 0;

	public int getColor()
	{
		return WHITE_RGB;
	}

	public MutableText getTitleText()
	{
		return Text.empty();
	}

	public void appendToolTip(List<Text> tooltip)
	{
	}

	public void appendBanedToolTip(List<Text> tooltip)
	{
	}


	public boolean canApplyTo(ItemStack stack)
	{
		return false;
	}

	public IWAbstractRuneUpgrade getRuneIndexParent()
	{
		return null;
	}

	public void applyUpgrade(ItemStack toolStack)
	{

	}

	public Map<Item, Integer> getIngredients()
	{
		return null;
	}

	public int level()
	{
		return 0;
	}
}
