package org.yang.iw.upgrade.sweeping;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.yang.iw.component.ToolFlagComponent;
import org.yang.iw.upgrade.AbstractUpgrade;
import org.yang.iw.util.style.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SweepingUpgrade extends AbstractUpgrade
{
	private static final Map<Item, Integer> ingredientMap;

	static
	{
		Map<Item, Integer> map = new HashMap<>();
		map.put(Items.STICK, 16);
		map.put(Items.IRON_INGOT, 4);
		ingredientMap = Collections.unmodifiableMap(map);
	}

	public SweepingUpgrade(String identifier)
	{
		super(identifier);
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return !ToolFlagComponent.fromItemStack(stack).canSweep();
	}


	@Override
	protected void applyUpgradeContent(ItemStack toolStack)
	{
		ToolFlagComponent.builder(toolStack).setCanSweep().dump(toolStack);
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

}
