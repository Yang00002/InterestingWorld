package org.yang.iw.rune_upgrade.sweeping;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.rune_upgrade.AbstractRuneUpgrade;
import org.yang.iw.util.constants.AttributeModifierIds;
import org.yang.iw.util.style.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Sweeping2Upgrade extends AbstractRuneUpgrade
{
	public static final float SWEEP_RATIO = 0.1f;

	private static final Map<Item, Integer> ingredientMap;

	static
	{
		Map<Item, Integer> map = new HashMap<>();
		map.put(Items.STICK, 16);
		map.put(Items.IRON_INGOT, 16);
		ingredientMap = Collections.unmodifiableMap(map);
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return !EnergyToolDataFlag.fromItemStack(stack).canSweep();
	}


	@Override
	protected void applyUpgradeContent(ItemStack toolStack)
	{
		EnergyToolDataFlag.builder(toolStack).setCanSweep().dump(toolStack);
		var at = toolStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		at = at.with(EntityAttributes.SWEEPING_DAMAGE_RATIO,
				new EntityAttributeModifier(AttributeModifierIds.SWEEPING_DAMAGE_RATIO_ADD_MAIN_HAND_UPGRADE,
						SWEEP_RATIO, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
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
		return 1;
	}

	@Override
	public int getColor()
	{
		return Color.WHITE_RGB;
	}

	@Override
	public String id()
	{
		return "sweeping2";
	}

}
