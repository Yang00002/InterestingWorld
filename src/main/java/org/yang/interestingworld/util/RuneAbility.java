package org.yang.interestingworld.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWItems;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;


public class RuneAbility
{
	static CustomModelDataComponent getModelComponent(int id)
	{
		return new CustomModelDataComponent(id);
	}

	public static void setAbility(ItemStack stack, IWAbstractRuneAbility ability)
	{
		IWAbstractRuneAbility origin = getAbility(stack);
		if (origin.index != ability.index)
		{
			Item item = stack.getItem();
			stack.set(IWComponents.ABILITY_INDEX, ability.index);
			stack.set(IWComponents.ABILITY_COLOR_RGB, ability.getColor());
			if (item instanceof EnergyToolItem)
			{
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.toolIndex));
				stack.set(IWComponents.DATA_FLAGS,
						EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
				origin.onRemoveAbility(stack);
				ability.onSetAbility(stack);
			}
			else stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.runeIndex));
		}
	}

	public static void setAbility(ItemStack stack, short index)
	{
		IWAbstractRuneAbility origin = getAbility(stack);
		IWAbstractRuneAbility ability = getAbility(index);
		if (origin.index != index)
		{
			Item item = stack.getItem();
			stack.set(IWComponents.ABILITY_INDEX, index);
			stack.set(IWComponents.ABILITY_COLOR_RGB, ability.getColor());
			if (item instanceof EnergyToolItem)
			{
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.toolIndex));
				stack.set(IWComponents.DATA_FLAGS,
						EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
				origin.onRemoveAbility(stack);
				ability.onSetAbility(stack);
			}
			else stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.runeIndex));
		}
	}

	public static IWAbstractRuneAbility getAbility(ItemStack stack)
	{
		return IWRuneAbilitys.getAbilityofIndex(stack.getOrDefault(IWComponents.ABILITY_INDEX, (short) 0));
	}

	public static int getColor(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.ABILITY_COLOR_RGB, IWUtil.TextStyle.WHITE_RGB);
	}

	public static int getColor(short index)
	{
		return getAbility(index).getColor();
	}


	public static IWAbstractRuneAbility getAbility(short index)
	{
		return IWRuneAbilitys.getAbilityofIndex(index);
	}

	public static void removeToolAbility(ItemStack stack)
	{
		IWAbstractRuneAbility ability = getAbility(stack);
		stack.remove(IWComponents.ABILITY_INDEX);
		stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
		stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilitys.DEFAULT_ABILITY.getColor());
		stack.set(IWComponents.DATA_FLAGS,
				EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
		ability.onRemoveAbility(stack);
	}

	public static ItemStack getEmptyAbilityRune()
	{
		return IWItems.EMPTY_RUNE.getDefaultStack();
	}

	private static void removeToolAbility(ItemStack stack, IWAbstractRuneAbility ability)
	{
		stack.remove(IWComponents.ABILITY_INDEX);
		stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
		stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilitys.DEFAULT_ABILITY.getColor());
		stack.set(IWComponents.DATA_FLAGS,
				EnergyToolDataFlag.copyFromItemStack(stack).updateLevelFromItemStack(stack));
		ability.onRemoveAbility(stack);
	}
}