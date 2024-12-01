package org.yang.interestingworld.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWResources;
import org.yang.interestingworld.IWTags;
import org.yang.interestingworld.IWUtil;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.yang.interestingworld.IWResources.EnchantmentData.EnchantmentData;
import static org.yang.interestingworld.IWResources.ToolEnchantmentType.ToolEnchantmentType;

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


	public static int getEnchantmentLevel(World world, ItemStack stack, RegistryKey<Enchantment> key)
	{
		return stack.getEnchantments().getLevel(IWUtil.Registry.getEnchantmentEntry(world, key));
	}

	public static boolean isConflict(Collection<RegistryEntry<Enchantment>> enchantmentCollection,
									 RegistryEntry<Enchantment> target)
	{
		var tv = target.value();
		for (var i : enchantmentCollection)
		{
			var v = i.value();
			if (v == tv) continue;
			if (v.exclusiveSet().contains(target)) return true;
		}
		return false;
	}

	public static int calculateTargetEnchantLevel(int defaultLevel, int nowLevel, int applyLevel,
												  IWResources.EnchantmentData.EnchantmentValue data)
	{
		int cap = data.getMaxAllowLevel() - defaultLevel;
		int now = nowLevel - defaultLevel;
		if (now > applyLevel) return nowLevel;
		if (now >= cap) return nowLevel;
		if (now == applyLevel) return nowLevel + 1;
		return Math.min(applyLevel, cap) + defaultLevel;
	}

	public static int calculateAddEnchantCost(int defaultLevel, int nowLevel, int applyLevel,
											  IWResources.EnchantmentData.EnchantmentValue data)
	{
		int cap = data.getMaxAllowLevel() - defaultLevel;
		int now = nowLevel - defaultLevel;
		if (now > applyLevel) return 0;
		if (now >= cap) return 0;
		if (now == applyLevel) return data.getLevelCost(now + 1) - data.getLevelCost(now);
		return data.getLevelCost(Math.min(applyLevel, cap)) - data.getLevelCost(now);
	}

	public static int canEnchantTo(ItemEnchantmentsComponent enchantmentsComponent, ItemStack stack, World world)
	{
		//寻找 stack 对应的物品类
		for (var tag : IWTags.EnergyToolTypeTags.getAll())
		{
			if (stack.isIn(tag))
			{
				//该类是否有附魔
				if (!ToolEnchantmentType.containsKey(tag)) return -1;
				//该类附魔
				Set<RegistryKey<Enchantment>> enchantmentSet = ToolEnchantmentType.get(tag);
				int cost = 0;
				Set<RegistryEntry<Enchantment>> applyedEnchantmentSet = new LinkedHashSet<>(
						stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
								.getEnchantments());
				var wrapper = IWUtil.Registry.getEnchantmentWrapper(world);
				for (var enchantmentKey : enchantmentSet)
				{
					//附魔是否被加载
					if (!EnchantmentData.containsKey(enchantmentKey))
					{
						enchantmentSet.remove(enchantmentKey);
						continue;
					}
					var optionalEnchantmentEntry = wrapper.getOptional(enchantmentKey);
					//附魔是否存在
					if (optionalEnchantmentEntry.isEmpty())
					{
						enchantmentSet.remove(enchantmentKey);
					}
					else
					{
						var enchantmentEntry = optionalEnchantmentEntry.get();
						//附魔在符文上
						int applyLevel = enchantmentsComponent.getLevel(enchantmentEntry);
						if (applyLevel == 0) continue;
						var data = EnchantmentData.get(enchantmentKey);
						//默认附魔未达到最大等级
						int deflevel = stack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
								ItemEnchantmentsComponent.DEFAULT).getLevel(enchantmentEntry);
						int nowlevel = stack.getEnchantments().getLevel(enchantmentEntry);
						int costEc = calculateAddEnchantCost(deflevel, nowlevel, applyLevel, data);
						if (!isConflict(applyedEnchantmentSet, enchantmentEntry)) cost += costEc;
						else if (costEc != 0 && data.allowConflict()) cost += costEc + data.getConflictCostPunishment();
					}
				}
				if (cost != 0) return cost;
			}
		}
		return -1;
	}

	public static void applyEnchant(ItemEnchantmentsComponent enchantmentsComponent, ItemStack toolStack, World world)
	{
		//寻找 stack 对应的物品类
		for (var tag : IWTags.EnergyToolTypeTags.getAll())
		{
			if (toolStack.isIn(tag))
			{
				//该类是否有附魔
				if (!ToolEnchantmentType.containsKey(tag)) return;
				//该类附魔
				Set<RegistryKey<Enchantment>> enchantmentSet = ToolEnchantmentType.get(tag);
				ItemEnchantmentsComponent before = toolStack.getOrDefault(DataComponentTypes.ENCHANTMENTS,
						ItemEnchantmentsComponent.DEFAULT);
				ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(before);
				var wrapper = IWUtil.Registry.getEnchantmentWrapper(world);
				for (var enchantmentKey : enchantmentSet)
				{
					//附魔是否被加载
					if (!EnchantmentData.containsKey(enchantmentKey))
					{
						enchantmentSet.remove(enchantmentKey);
						continue;
					}
					var optionalEnchantmentEntry = wrapper.getOptional(enchantmentKey);
					//附魔是否存在
					if (optionalEnchantmentEntry.isEmpty())
					{
						enchantmentSet.remove(enchantmentKey);
					}
					else
					{
						var enchantmentEntry = optionalEnchantmentEntry.get();
						//附魔在符文上
						int applyLevel = enchantmentsComponent.getLevel(enchantmentEntry);
						if (applyLevel == 0) continue;
						var data = EnchantmentData.get(enchantmentKey);
						//默认附魔未达到最大等级
						int deflevel = toolStack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
								ItemEnchantmentsComponent.DEFAULT).getLevel(enchantmentEntry);
						int nowlevel = toolStack.getEnchantments().getLevel(enchantmentEntry);
						int targetLevel = calculateTargetEnchantLevel(deflevel, nowlevel, applyLevel, data);
						if (isConflict(builder.getEnchantments(), enchantmentEntry) && !data.allowConflict()) continue;
						builder.set(enchantmentEntry, targetLevel);
					}
				}
				toolStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
				setFlagOfRealEnchant(toolStack);
			}
		}
	}
}