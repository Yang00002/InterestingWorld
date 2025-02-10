package org.yang.iw.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.component.IWComponents;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.util.style.Color;


public class IWRuneAbilityUtil
{

	public static void setAbility(ItemStack stack, AbstractRuneAbility ability)
	{
		AbstractRuneAbility origin = getAbility(stack);
		if (origin.index != ability.index)
		{
			Item item = stack.getItem();
			stack.set(IWComponents.ABILITY_INDEX, ability.index);
			stack.set(IWComponents.ABILITY_COLOR_RGB, ability.getColor());
			if (item instanceof EnergyToolItem)
			{
				EnergyToolDataFlag.builder(stack).updateLevelFromItemStack(stack).dump(stack);
				origin.onRemoveAbility(stack);
				ability.onSetAbility(stack);
			}
		}
	}

	public static void setAbility(ItemStack stack, short index)
	{
		AbstractRuneAbility origin = getAbility(stack);
		AbstractRuneAbility ability = getAbility(index);
		if (origin.index != index)
		{
			Item item = stack.getItem();
			stack.set(IWComponents.ABILITY_INDEX, index);
			stack.set(IWComponents.ABILITY_COLOR_RGB, ability.getColor());
			if (item instanceof EnergyToolItem)
			{
				EnergyToolDataFlag.builder(stack).updateLevelFromItemStack(stack).dump(stack);
				origin.onRemoveAbility(stack);
				ability.onSetAbility(stack);
			}
		}
	}

	public static AbstractRuneAbility getAbility(ItemStack stack)
	{
		return IWRuneAbilities.getAbilityofIndex(stack.getOrDefault(IWComponents.ABILITY_INDEX, (short) 0));
	}

	public static int getColor(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.ABILITY_COLOR_RGB, Color.WHITE_RGB);
	}

	public static int getColor(short index)
	{
		return getAbility(index).getColor();
	}


	public static AbstractRuneAbility getAbility(short index)
	{
		return IWRuneAbilities.getAbilityofIndex(index);
	}

	public static void removeToolAbility(ItemStack stack)
	{
		AbstractRuneAbility ability = getAbility(stack);
		stack.remove(IWComponents.ABILITY_INDEX);
		//stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
		stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilities.DEFAULT_ABILITY.getColor());
		EnergyToolDataFlag.builder(stack).updateLevelFromItemStack(stack).dump(stack);
		ability.onRemoveAbility(stack);
	}

	private static void removeToolAbility(ItemStack stack, AbstractRuneAbility ability)
	{
		stack.remove(IWComponents.ABILITY_INDEX);
		//stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
		stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilities.DEFAULT_ABILITY.getColor());
		EnergyToolDataFlag.builder(stack).updateLevelFromItemStack(stack).dump(stack);
		ability.onRemoveAbility(stack);
	}
}