package org.yang.iw.rune_upgrade.sweeping;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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

import static org.yang.iw.util.constants.AttributeModifierIds.SWEEPING_DAMAGE_RATIO_ADD_MAIN_HAND_UPGRADE;

public class Sweeping3Upgrade extends AbstractRuneUpgrade
{

	public static final float SWEEP_RATIO = 0.5f;

	private static final Map<Item, Integer> ingredientMap;

	static
	{
		Map<Item, Integer> map = new HashMap<>();
		map.put(Items.STICK, 16);
		map.put(Items.IRON_INGOT, 8);
		map.put(Items.DIAMOND, 1);
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
		var at = toolStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		at = at.with(EntityAttributes.SWEEPING_DAMAGE_RATIO,
				new EntityAttributeModifier(SWEEPING_DAMAGE_RATIO_ADD_MAIN_HAND_UPGRADE, SWEEP_RATIO,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		toolStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, at);
	}

	@Override
	public Map<Item, Integer> getIngredients()
	{
		return ingredientMap;
	}

	@Override
	public int level()
	{
		return 3;
	}

	@Override
	public int getColor()
	{
		return Color.DiamondColorRGB;
	}

	@Override
	public String id()
	{
		return "sweeping3";
	}


}
