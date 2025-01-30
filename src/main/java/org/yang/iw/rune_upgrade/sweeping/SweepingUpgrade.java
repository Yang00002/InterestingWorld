package org.yang.iw.rune_upgrade.sweeping;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.yang.iw.IWComponents;
import org.yang.iw.rune_upgrade.AbstractRuneUpgrade;
import org.yang.iw.util.style.Color;
import org.yang.iw.util.toolflag.EnergyToolDataFlag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SweepingUpgrade extends AbstractRuneUpgrade
{
	private static final Map<Item, Integer> ingredientMap;

	static
	{
		Map<Item, Integer> map = new HashMap<>();
		map.put(Items.STICK, 16);
		map.put(Items.IRON_INGOT, 4);
		ingredientMap = Collections.unmodifiableMap(map);
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return !EnergyToolDataFlag.getFromItemStack(stack).canSweep();
	}


	@Override
	protected void applyUpgradeContent(ItemStack toolStack)
	{
		toolStack.set(IWComponents.TOOL_FLAG, EnergyToolDataFlag.copyFromItemStack(toolStack).setCanSweep());
	}

	@Override
	public Map<Item, Integer> getIngredients()
	{
		return ingredientMap;
	}

	@Override
	public int level()
	{
		return 0;
	}

	@Override
	public int getColor()
	{
		return Color.WHITE_RGB;
	}

	@Override
	public String id()
	{
		return "sweeping";
	}

}
