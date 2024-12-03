package org.yang.interestingworld.util.enchantment;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWItems;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.resource.enchant.RuneEnchantData;

import java.util.HashSet;
import java.util.Set;

import static org.yang.interestingworld.resource.enchant.EnchantGroupData.getEnchantGroupOfItemStack;

public class RuneEnchantment
{
	public static final int HAVE_DEFAULT_ENCHANTMENT_BYTE = 1;
	public static final int HAVE_REAL_ENCHANTMENT_BYTE = 2;

	public static boolean onlyHaveDefaultEnchantment(ItemStack stack)
	{
		return haveDefaultEnchantment(stack) && !haveRealEnchantment(stack);
	}

	public static boolean haveDefaultEnchantment(ItemStack stack)
	{
		int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		return (data & HAVE_DEFAULT_ENCHANTMENT_BYTE) != 0;
	}

	public static boolean haveRealEnchantment(ItemStack stack)
	{
		int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		return (data & HAVE_REAL_ENCHANTMENT_BYTE) != 0;
	}

	public static void setFlagOfRealEnchant(ItemStack stack)
	{
		int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		stack.set(IWComponents.DATA_FLAGS, (data & ~HAVE_DEFAULT_ENCHANTMENT_BYTE) | HAVE_REAL_ENCHANTMENT_BYTE);
	}

	public static void clearFlagOfRealEnchant(ItemStack stack)
	{
		int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		if (stack.contains(IWComponents.DEFAULT_ENCHANTMENTS))
			stack.set(IWComponents.DATA_FLAGS, (data & ~HAVE_REAL_ENCHANTMENT_BYTE) | HAVE_DEFAULT_ENCHANTMENT_BYTE);
		else stack.set(IWComponents.DATA_FLAGS, data & ~HAVE_REAL_ENCHANTMENT_BYTE & ~HAVE_DEFAULT_ENCHANTMENT_BYTE);
	}

	public static ItemStack getEnchantRuneItemStack(ItemEnchantmentsComponent component, int level)
	{
		ItemStack stack = IWItems.ENCHANTMENT_RUNE.getDefaultStack();
		stack.set(DataComponentTypes.STORED_ENCHANTMENTS, component);
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(level));
		return stack;
	}

	public static int getEnchantmentLevel(World world, ItemStack stack, RegistryKey<Enchantment> key)
	{
		return stack.getEnchantments().getLevel(IWUtil.Registry.getEnchantmentEntry(world, key));
	}

	public static int calculateAddEnchantCost(int defaultLevel, int nowLevel, int applyLevel, RuneEnchantData data)
	{
		int cap = data.getMaxAllowLevel() - defaultLevel;
		int now = nowLevel - defaultLevel;
		if (now > applyLevel) return 0;
		if (now >= cap) return 0;
		if (now == applyLevel) return data.getLevelCost(now + 1) - data.getLevelCost(now);
		return data.getLevelCost(Math.min(applyLevel, cap)) - data.getLevelCost(now);
	}

	public static int calculateAddEnchantLevel(int defaultLevel, int nowLevel, int applyLevel, RuneEnchantData data)
	{
		int cap = data.getMaxAllowLevel() - defaultLevel;
		int now = nowLevel - defaultLevel;
		if (now > applyLevel) return 0;
		if (now >= cap) return 0;
		if (now == applyLevel) return 1;
		return Math.min(applyLevel, cap) - now;
	}

	public static int canEnchantTo(ItemEnchantmentsComponent enchantmentsComponent, ItemStack stack, World world)
	{
		Set<RuneEnchantData> enchantmentSet = getEnchantGroupOfItemStack(stack);
		if (enchantmentSet == null)
		{
			return -1;
		}
		//该类是否有附魔
		int cost = 0;
		Set<RegistryEntry<Enchantment>> applyedEnchantmentSet = new HashSet<>(
				stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
						.getEnchantments());
		ItemEnchantmentsComponent haveEnchantments = stack.getEnchantments();
		ItemEnchantmentsComponent defaultEnchantments = stack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
				ItemEnchantmentsComponent.DEFAULT);
		var wrapper = IWUtil.Registry.getEnchantmentWrapper(world);
		continueLabel:
		for (var enchantmentData : enchantmentSet)
		{
			//附魔是否被加载
			var optionalEnchantmentEntry = wrapper.getOptional(enchantmentData.getRegistryKey());
			//附魔是否存在
			if (optionalEnchantmentEntry.isPresent())
			{
				var enchantmentEntry = optionalEnchantmentEntry.get();
				//附魔在符文上
				int applyLevel = enchantmentsComponent.getLevel(enchantmentEntry);
				if (applyLevel == 0) continue;
				//默认附魔未达到最大等级
				int deflevel = defaultEnchantments.getLevel(enchantmentEntry);
				int nowlevel = haveEnchantments.getLevel(enchantmentEntry);
				int costEc = calculateAddEnchantCost(deflevel, nowlevel, applyLevel, enchantmentData);
				if (costEc < 1) continue;
				int cA = 0;
				float cM = 1.0f;
				// A B 冲突, 则带有 A 的工具附魔 A B 和附魔 B A 效果不应该不同
				if (!applyedEnchantmentSet.contains(enchantmentEntry))
				{
					for (var cs : enchantmentData.getConflictGroups())
					{
						if (cs.getAllowConflict())
						{
							for (var ec : cs.getEnchantSet())
							{
								if (ec != enchantmentData && applyedEnchantmentSet.contains(ec.getRegistryEntry()))
								{
									cA += cs.getCostAddPunish();
									cM += cs.getCostMultiplierPunish();
								}
							}
						}
						else
						{
							for (var ec : cs.getEnchantSet())
							{
								if (ec != enchantmentData && applyedEnchantmentSet.contains(ec.getRegistryEntry()))
								{
									continue continueLabel;
								}
							}
						}
					}
					applyedEnchantmentSet.add(enchantmentData.getRegistryEntry());
				}
				cost += (int) (costEc * cM) + cA;
			}
		}
		if (cost != 0) return cost;
		return -1;
	}

	public static void applyEnchant(ItemEnchantmentsComponent enchantmentsComponent, ItemStack toolStack, World world)
	{
		Set<RuneEnchantData> enchantmentSet = getEnchantGroupOfItemStack(toolStack);
		if (enchantmentSet == null) return;
		Set<RegistryEntry<Enchantment>> applyedEnchantmentSet = new HashSet<>(
				toolStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
						.getEnchantments());
		ItemEnchantmentsComponent haveEnchantments = toolStack.getEnchantments();
		ItemEnchantmentsComponent defaultEnchantments = toolStack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
				ItemEnchantmentsComponent.DEFAULT);
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(haveEnchantments);
		var wrapper = IWUtil.Registry.getEnchantmentWrapper(world);
		continueLabel:
		for (var enchantmentData : enchantmentSet)
		{
			//附魔是否被加载
			var optionalEnchantmentEntry = wrapper.getOptional(enchantmentData.getRegistryKey());
			//附魔是否存在
			if (optionalEnchantmentEntry.isPresent())
			{
				var enchantmentEntry = optionalEnchantmentEntry.get();
				//附魔在符文上
				int applyLevel = enchantmentsComponent.getLevel(enchantmentEntry);
				if (applyLevel == 0) continue;
				//默认附魔未达到最大等级
				int deflevel = defaultEnchantments.getLevel(enchantmentEntry);
				int nowlevel = haveEnchantments.getLevel(enchantmentEntry);
				int al = calculateAddEnchantLevel(deflevel, nowlevel, applyLevel, enchantmentData);
				if (al < 1) continue;
				// A B 冲突, 则带有 A 的工具附魔 A B 和附魔 B A 效果不应该不同
				if (!applyedEnchantmentSet.contains(enchantmentEntry))
				{
					for (var cs : enchantmentData.getConflictGroups())
					{
						if (!cs.getAllowConflict())
						{
							for (var ec : cs.getEnchantSet())
							{
								if (ec != enchantmentData && applyedEnchantmentSet.contains(ec.getRegistryEntry()))
								{
									continue continueLabel;
								}
							}
						}
					}
					applyedEnchantmentSet.add(enchantmentData.getRegistryEntry());
				}
				builder.set(enchantmentEntry, al);
			}
		}
		toolStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
		setFlagOfRealEnchant(toolStack);
	}

	public static int getRuneLevelOfCost(int cost)
	{
		return cost / 1000;
	}
}