package org.yang.interestingworld.util.enchantment;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWItems;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.enchant.EnchantData;

import java.util.HashSet;
import java.util.Set;

import static org.yang.interestingworld.enchant.ToolGroup.getAllowEnchantOfItemStack;
import static org.yang.interestingworld.util.EnergyTool.setFlagOfRealEnchant;


public class RuneEnchantment
{

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

	public static int calculatNextEnchantLevel(int currentLevel, int applyLevel, int maxLevel)
	{
		if (currentLevel >= maxLevel) return maxLevel;
		if (currentLevel > applyLevel) return currentLevel;
		else if (currentLevel < applyLevel) return Math.min(applyLevel, maxLevel);
		else return currentLevel + 1;
	}

	public static int canEnchantTo(ItemEnchantmentsComponent enchantmentsComponent, ItemStack stack)
	{
		//该类是否有附魔
		Set<EnchantData> enchantmentSet = getAllowEnchantOfItemStack(stack);
		if (enchantmentSet == null) return -1;
		int cost = 0;
		Set<RegistryEntry<Enchantment>> applyedEnchantmentSet = new HashSet<>();
		ItemEnchantmentsComponent haveEnchantments = stack.getEnchantments();
		ItemEnchantmentsComponent defaultEnchantments = stack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
				ItemEnchantmentsComponent.DEFAULT);
		for (var i : haveEnchantments.getEnchantmentEntries())
		{
			var k = i.getKey();
			if (i.getIntValue() > defaultEnchantments.getLevel(k)) applyedEnchantmentSet.add(k);
		}
		for (var targetEnchantmentEntryAndLevel : enchantmentsComponent.getEnchantmentEntries())
		{
			var targetEnchantmentEntry = targetEnchantmentEntryAndLevel.getKey();
			var targetEnchantmentData = EnchantData.getEnchantmentDataFromRegistryEntry(targetEnchantmentEntry);
			if (targetEnchantmentData == null) continue;
			if (!enchantmentSet.contains(targetEnchantmentData)) continue;
			int defaultLevel = defaultEnchantments.getLevel(targetEnchantmentEntry);
			int maxLevel = targetEnchantmentData.getMaxLevel() - defaultLevel;
			int currentLevel = haveEnchantments.getLevel(targetEnchantmentEntry) - defaultLevel;
			int applyLevel = calculatNextEnchantLevel(currentLevel, targetEnchantmentEntryAndLevel.getIntValue(),
					maxLevel);
			if (applyLevel <= currentLevel) continue;
			if (targetEnchantmentData.tableConflictWith(applyedEnchantmentSet)) continue;
			cost += targetEnchantmentData.getAddLevelCost(currentLevel, applyLevel);
		}
		if (cost != 0) return cost;
		return -1;
	}

	public static int applyEnchant(ItemEnchantmentsComponent enchantmentsComponent, ItemStack toolStack)
	{
		int cost = 0;
		//该类是否有附魔
		Set<EnchantData> enchantmentSet = getAllowEnchantOfItemStack(toolStack);
		if (enchantmentSet == null) return -1;
		Set<RegistryEntry<Enchantment>> applyedEnchantmentSet = new HashSet<>();
		ItemEnchantmentsComponent haveEnchantments = toolStack.getEnchantments();
		ItemEnchantmentsComponent defaultEnchantments = toolStack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
				ItemEnchantmentsComponent.DEFAULT);
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(haveEnchantments);
		for (var i : haveEnchantments.getEnchantmentEntries())
		{
			var k = i.getKey();
			if (i.getIntValue() > defaultEnchantments.getLevel(k)) applyedEnchantmentSet.add(k);
		}

		for (var targetEnchantmentEntryAndLevel : enchantmentsComponent.getEnchantmentEntries())
		{
			var targetEnchantmentEntry = targetEnchantmentEntryAndLevel.getKey();
			var targetEnchantmentData = EnchantData.getEnchantmentDataFromRegistryEntry(targetEnchantmentEntry);
			if (targetEnchantmentData == null) continue;
			if (!enchantmentSet.contains(targetEnchantmentData)) continue;
			int defaultLevel = defaultEnchantments.getLevel(targetEnchantmentEntry);
			int maxLevel = targetEnchantmentData.getMaxLevel() - defaultLevel;
			int currentLevel = haveEnchantments.getLevel(targetEnchantmentEntry) - defaultLevel;
			int applyLevel = calculatNextEnchantLevel(currentLevel, targetEnchantmentEntryAndLevel.getIntValue(),
					maxLevel);
			if (applyLevel <= currentLevel) continue;
			if (targetEnchantmentData.tableConflictWith(applyedEnchantmentSet)) continue;
			builder.set(targetEnchantmentEntry, applyLevel + defaultLevel);
			cost += targetEnchantmentData.getAddLevelCost(currentLevel, applyLevel);
		}
		toolStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
		setFlagOfRealEnchant(toolStack, toolStack.getOrDefault(IWComponents.ENCHANT_VALUE, 0) + cost);
		if (cost > 0) return cost;
		return -1;
	}

	public static int getWorldLevelOfXpCost(int cost)
	{
		if (cost >= 2045)
		{
			if (cost >= 4020)
			{
				if (cost >= 10820) return 10;
				if (cost >= 5345) return 9;
				return 8;
			}
			if (cost >= 2920) return 7;
			return 6;
		}
		if (cost >= 550)
		{
			if (cost >= 1395) return 5;
			if (cost >= 910) return 4;
			return 3;
		}
		if (cost >= 315) return 2;
		if (cost >= 160) return 1;
		return 0;
	}

	private static final int[] xp_costs = {159, 314, 549, 909, 1394, 2044, 2919, 4019, 5344, 18019};

	public static int getXpCostOfWorldLevel(int level)
	{
		return xp_costs[Math.min(level, 9)];
	}

	public static int getExperienceFromLevel(int level, float frac)
	{
		int base;
		if (level < 17)
		{
			base = (level + 6) * level;
			if (level < 16) return base + (int) ((2 * level + 7) * frac);
			else return base + (int) ((5 * level - 38) * frac);
		}
		else if (level < 32)
		{
			base = (int) ((2.5f * level - 40.5f) * level) + 360;
			if (level < 31) return base + (int) ((5 * level - 38) * frac);
			else return base + (int) ((9 * level - 158) * frac);
		}
		else
		{
			base = (int) ((4.5f * level - 162.5f) * level) + 2220;
			return base + (int) ((9 * level - 158) * frac);
		}
	}

	public static Text getRawName(RegistryEntry<Enchantment> enchantment, int level)
	{
		MutableText mutableText = enchantment.value().description().copy();
		if (level != 1 || enchantment.value().getMaxLevel() != 1)
		{
			mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + level));
		}
		return mutableText;
	}

}