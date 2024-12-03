package org.yang.interestingworld.util.enchantment;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.yang.interestingworld.resource.enchant.RuneEnchantData;

public class EnchantEntryWithLevelAndCost implements runeEnchantEntry, canModifyCostByConflict
{
	RuneEnchantData data;
	int maxLevel;
	int currentLevel;

	int addPunish = 0;

	float multiPunish = 1;

	@Nullable
	public static EnchantEntryWithLevelAndCost fromItemEnchantmentsComponent(RegistryEntry<Enchantment> entry,
																			 ItemEnchantmentsComponent component,
																			 ItemEnchantmentsComponent defaultComponent)
	{
		var data = RuneEnchantData.fromRegistryEntry(entry);
		if (data == null) return null;
		int level = component.getLevel(entry);
		int deflevel = defaultComponent.getLevel(entry);
		if (deflevel >= level) return null;
		EnchantEntryWithLevelAndCost ret = new EnchantEntryWithLevelAndCost();
		ret.data = data;
		ret.currentLevel = 0;
		ret.maxLevel = level - deflevel;
		return ret;
	}

	@Override
	public RuneEnchantData getData()
	{
		return data;
	}

	@Override
	public int maxLevel()
	{
		return maxLevel;
	}

	@Override
	public boolean canAddLevel(int cost)
	{
		if (currentLevel >= maxLevel) return false;
		return (data.getLevelCost(currentLevel + 1) - data.getLevelCost(currentLevel)) <= cost;
	}

	@Override
	public int getWeight()
	{
		return data.getWeight();
	}

	@Override
	public int maxCanAddLevel(int cost)
	{
		return data.costAchieveLevel(cost + data.getLevelCost(currentLevel), currentLevel, maxLevel) - currentLevel;
	}

	@Override
	public int addLevel(int level)
	{
		int cu = Math.min(currentLevel + level, maxLevel);
		int ret = data.getLevelCost(cu) - data.getLevelCost(currentLevel);
		currentLevel = cu;
		return ret;
	}

	@Override
	public int getCost()
	{
		return data.getLevelCost(currentLevel);
	}

	@Override
	public int currentLevel()
	{
		return currentLevel;
	}

	@Override
	public void addPunishment(int add, float mul)
	{
		addPunish += add;
		multiPunish += mul;
	}

	@Override
	public int getPunishedCost()
	{
		return (int) (data.getLevelCost(currentLevel) * multiPunish) + addPunish;
	}
}
