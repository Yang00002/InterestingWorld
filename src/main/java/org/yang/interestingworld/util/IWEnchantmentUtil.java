package org.yang.interestingworld.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.enchant.resource.EnchantData;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.yang.interestingworld.enchant.resource.ToolGroup.getAllowEnchantOfItemStack;


public class IWEnchantmentUtil
{

	private static int countItemStackEnchantCost(ItemStack stack)
	{
		ItemEnchantmentsComponent component = stack.getEnchantments();
		ItemEnchantmentsComponent defaultComponent = stack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
				ItemEnchantmentsComponent.DEFAULT);
		Set<RegistryEntry<Enchantment>> appliedSet = new HashSet<>();
		int cost = 0;
		for (var enchantEntry : component.getEnchantmentEntries())
		{
			int lvl = enchantEntry.getIntValue();
			var registryEntry = enchantEntry.getKey();
			lvl -= defaultComponent.getLevel(registryEntry);
			var enchantData = EnchantData.getEnchantmentDataFromRegistryEntry(registryEntry);
			if (enchantData == null || lvl <= 0) continue;
			if (lvl > enchantData.getMaxRandomLevel()) lvl = enchantData.getMaxRandomLevel();
			int addPunish = 0;
			float mulPunish = 1.0f;
			var confictSet = enchantData.getConflicts();
			for (var i : confictSet.entrySet())
			{
				if (appliedSet.contains(i.getKey()))
				{
					var entry = i.getValue();
					addPunish += entry.addPunish;
					mulPunish *= entry.mulPunish;
				}
			}
			cost += addPunish + (int) (mulPunish * enchantData.getLevelCost(lvl));
			appliedSet.add(registryEntry);
		}
		return cost;
	}


	public static ItemStack getEnchantRuneItemStack(ItemEnchantmentsComponent component, int level)
	{
		ItemStack stack = IWItems.ENCHANTMENT_RUNE.getDefaultStack();
		stack.set(DataComponentTypes.STORED_ENCHANTMENTS, component);
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(level));
		return stack;
	}

	public static int calculatNextEnchantLevel(int currentLevel, int applyLevel, int maxLevel)
	{
		if (currentLevel >= maxLevel) return maxLevel;
		if (currentLevel > applyLevel) return currentLevel;
		else if (currentLevel < applyLevel) return Math.min(applyLevel, maxLevel);
		else return currentLevel + 1;
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
		if (cost < 1) return -1;
		setRealEnchant(toolStack, builder.build());
		return cost;
	}

	public static int getWorldLevelOfXpCost(int cost)
	{
		if (cost >= 910)
		{
			if (cost >= 2045)
			{
				if (cost >= 4020) return 8;
				if (cost >= 2920) return 7;
				return 6;
			}
			if (cost >= 1395) return 5;
			return 4;
		}
		if (cost >= 315)
		{
			if (cost >= 550) return 3;
			return 2;
		}
		if (cost >= 160) return 1;
		return 0;
	}

	//									   1    2    3    4    5     6     7     8
	private static final int[] xp_costs = {160, 315, 550, 910, 1395, 2045, 2920, 4020, 5345};

	public static int getMaxAllowXpCostOfWorldLevel(int level)
	{
		return xp_costs[level] - 1;
	}

	public static int getRandomXpCostOfWorldLevel(int level, Random random)
	{
		if (level > 0) return random.nextInt(xp_costs[level - 1], xp_costs[level]);
		return random.nextInt(0, xp_costs[0]);
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

	public static void setRealEnchant(ItemStack stack, ItemEnchantmentsComponent component)
	{
		stack.set(DataComponentTypes.ENCHANTMENTS, component);
		var flag = EnergyToolDataFlag.copyFromItemStack(stack);
		int cost = countItemStackEnchantCost(stack);
		if (cost < 1)
		{
			stack.remove(IWComponents.ENCHANT_VALUE);
			flag.removeHaveRealEnchantment();
		}
		else
		{
			stack.set(IWComponents.ENCHANT_VALUE, cost);
			flag.setHaveRealEnchantment();
		}
		flag.updateLevelFromItemStack(stack);
		stack.set(IWComponents.DATA_FLAGS, flag);
	}

	public static void setRealEnchantValue(ItemStack stack, int value)
	{
		stack.set(IWComponents.ENCHANT_VALUE, value);
		var flag = EnergyToolDataFlag.copyFromItemStack(stack);
		flag.setHaveRealEnchantment();
		flag.updateLevelFromItemStack(stack);
		stack.set(IWComponents.DATA_FLAGS, flag);
	}

	public static void clearRealEnchantValue(ItemStack stack)
	{
		stack.remove(IWComponents.ENCHANT_VALUE);
		var flag = EnergyToolDataFlag.copyFromItemStack(stack);
		flag.removeHaveRealEnchantment();
		flag.updateLevelFromItemStack(stack);
		stack.set(IWComponents.DATA_FLAGS, flag);
	}

	public static void setDefaultEnchant(ItemStack stack, ItemEnchantmentsComponent component)
	{
		var ori = stack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
		var n = stack.getEnchantments();
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(n);
		for (var i : component.getEnchantmentEntries())
		{
			var entry = i.getKey();
			builder.set(entry, i.getIntValue() - ori.getLevel(entry) + n.getLevel(entry));
		}
		stack.set(IWComponents.DEFAULT_ENCHANTMENTS, component);
		setRealEnchant(stack, builder.build());
		var flag = EnergyToolDataFlag.copyFromItemStack(stack);
		flag.setHaveDefaultEnchantment();
		stack.set(IWComponents.DATA_FLAGS, flag);
	}
}