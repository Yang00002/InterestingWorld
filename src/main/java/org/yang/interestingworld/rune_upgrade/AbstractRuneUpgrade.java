package org.yang.interestingworld.rune_upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

import java.util.List;
import java.util.Map;

public class AbstractRuneUpgrade
{
	public short index = 0;
	public short runeIndex = 0;

	public int getColor()
	{
		return Color.WHITE_RGB;
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

	public AbstractRuneUpgrade getRuneIndexParent()
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
