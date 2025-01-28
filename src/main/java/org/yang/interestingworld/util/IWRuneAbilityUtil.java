package org.yang.interestingworld.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.rune_ability.AbstractRuneAbility;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;


public class IWRuneAbilityUtil
{
	static CustomModelDataComponent getModelComponent(int id)
	{
		return new CustomModelDataComponent(id);
	}

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
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.toolIndex));
				stack.set(IWComponents.TOOL_FLAG,
						EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
				origin.onRemoveAbility(stack);
				ability.onSetAbility(stack);
			}
			else stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.runeIndex));
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
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.toolIndex));
				stack.set(IWComponents.TOOL_FLAG,
						EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
				origin.onRemoveAbility(stack);
				ability.onSetAbility(stack);
			}
			else stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.runeIndex));
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
		stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
		stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilities.DEFAULT_ABILITY.getColor());
		stack.set(IWComponents.TOOL_FLAG,
				EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
		ability.onRemoveAbility(stack);
	}

	public static ItemStack getEmptyAbilityRune()
	{
		return IWItems.EMPTY_RUNE.getDefaultStack();
	}

	private static void removeToolAbility(ItemStack stack, AbstractRuneAbility ability)
	{
		stack.remove(IWComponents.ABILITY_INDEX);
		stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
		stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilities.DEFAULT_ABILITY.getColor());
		stack.set(IWComponents.TOOL_FLAG,
				EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
		ability.onRemoveAbility(stack);
	}
}