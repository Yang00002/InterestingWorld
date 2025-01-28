package org.yang.interestingworld.rune_upgrade.sweeping;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.rune_upgrade.RuneUpgrade;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SweepingUpgrade extends RuneUpgrade
{
	private static final Map<Item, Integer> ingredientMap;

	static
	{
		Map<Item, Integer> map = new HashMap<>();
		map.put(Items.STICK, 16);
		map.put(Items.IRON_INGOT, 1);
		ingredientMap = Collections.unmodifiableMap(map);
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return !EnergyToolDataFlag.getFromItemStack(stack).canSweep();
	}

	@Override
	public void applyUpgrade(ItemStack toolStack)
	{
		super.applyUpgrade(toolStack);
		toolStack.set(IWComponents.TOOL_FLAG, EnergyToolDataFlag.copyFromItemStack(toolStack).setCanSweep());
	}

	@Override
	public Map<Item, Integer> getIngredients()
	{
		return ingredientMap;
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("sweeping_upgrade_title");
	}

	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("sweeping_upgrade_detail").withColor(getColor()));
		appendIngredientToolTip(tooltip);
	}

}
