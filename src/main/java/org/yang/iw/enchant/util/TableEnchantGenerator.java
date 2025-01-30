package org.yang.iw.enchant.util;


import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.apache.commons.lang3.mutable.MutableInt;
import org.yang.iw.enchant.IWEnchantments;
import org.yang.iw.enchant.resource.TableEnchantGroup;
import org.yang.iw.item.heart.base.BaseHeart;
import org.yang.iw.util.Rand;
import org.yang.iw.util.Server;

import java.util.LinkedList;
import java.util.List;

import static org.yang.iw.enchant.resource.ToolGroup.getRandomTableEnchantGroupOfItemStack;
import static org.yang.iw.util.IWEnchantmentUtil.getMaxAllowXpCostOfWorldLevel;

/***
 * 一个 Generator 仅能生成一次附魔
 */
public class TableEnchantGenerator
{
	public static int getGeneratorEntryCount(int plentiLevel, int capCount, Rand random)
	{
		capCount--;
		int genLevel = Math.abs(random.nextTriangleInt(-capCount, capCount)) + 1;
		if (plentiLevel == 0 || capCount <= genLevel) return genLevel;
		return random.nextTriangleInt(0, Math.min(capCount - genLevel, plentiLevel)) + genLevel;
	}

	public static TableEnchantEntry getRandomEntry(List<TableEnchantEntry> list, int maxWeight, Rand random)
	{
		int l = random.nextEvenInt(1, maxWeight);
		var it = list.iterator();
		while (it.hasNext())
		{
			var e = it.next();
			if (e.remove)
			{
				it.remove();
				continue;
			}
			int w = e.weight;
			if (w < l) l -= w;
			else return e;
		}
		return null;
	}

	//使用 MutableInt, 输入拥有, 输出使用数量 crystalCount -> 22 位 附魔id 10 位消耗水晶数
	public static ItemEnchantmentsComponent generateFromItemStack(ItemStack toolStack, ItemStack heartStack, int seed,
																  MutableInt crystalCount, MutableInt lapisCount,
																  MutableInt costAchieve)
	{
		Rand random = new Rand(seed + Registries.ITEM.getRawId(toolStack.getItem())); //rd
		//获得附魔组
		TableEnchantGroup enchantGroup = getRandomTableEnchantGroupOfItemStack(toolStack, random); //rd
		if (enchantGroup == null) return null;
		int enchantGroupSize = enchantGroup.getEnchantCount();
		if (enchantGroupSize < 1) return null;
		//附魔池
		List<TableEnchantEntry> enchantmentPool = new LinkedList<>();
		ItemEnchantmentsComponent runeComponent = heartStack.getEnchantments();
		//是否均衡
		boolean balanced = runeComponent.getLevel(IWEnchantments.BALANCE_entry.get()) > 0;
		boolean firstUp = true;
		//是否强化
		boolean boost = runeComponent.getLevel(IWEnchantments.RUNE_BOOST_entry.get()) > 0;
		//获得最大附魔数
		int maxEnchantCount = getGeneratorEntryCount(runeComponent.getLevel(IWEnchantments.LUCKY_entry.get()),
				enchantGroupSize, random); //rd
		//获得最大尝试次数
		int maxTryAmount = crystalCount.getValue() + 1 + runeComponent.getLevel(IWEnchantments.PLENTIFUL_entry.get());
		int efficiency = runeComponent.getLevel(IWEnchantments.ENERGY_EFFICIENCY_entry.get());
		//获得最大消耗
		Item heartItem = heartStack.getItem();
		int worldGate = Server.getPersistentData().worldEnergyLevel;
		if (heartItem instanceof BaseHeart baseHeart) worldGate = Math.min(worldGate, baseHeart.getMaxSupportLevel());
		int maxCost = (200 * (1 + lapisCount.getValue()) * (efficiency + 1));
		int cap = getMaxAllowXpCostOfWorldLevel(worldGate);
		if (maxCost > cap) maxCost = cap;
		int maxWeight = 0;
		for (var i : enchantGroup.getEnchants())
		{
			var g = TableEnchantEntry.of(i, maxCost, worldGate);
			if (g != null)
			{
				enchantmentPool.add(g);
				maxWeight += g.weight;
			}
		}
		if (maxWeight < 1) return null;
		int leftCost = maxCost;
		int tryamount = 0;
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		while (maxWeight > 0 && tryamount < maxTryAmount)
		{
			var entry = getRandomEntry(enchantmentPool, maxWeight, random);
			if (entry == null) break;
			if (boost && !entry.isBoosted())
			{
				if (entry.canAddLevelIfBoosted(leftCost))
				{
					entry.boost();
					boost = false;
				}
				else
				{
					if (entry.currentLevel > 0) builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
			}
			int maxLevel = entry.maxLevelWithCost(leftCost);
			if (maxLevel <= entry.currentLevel)
			{
				if (entry.currentLevel > 0) builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
				continue;
			}
			if (!entry.choosen)
			{
				if (maxEnchantCount > 0)
				{
					entry.choosen = true;
					maxEnchantCount--;
					var ed = entry.enchantData;
					for (TableEnchantEntry n : enchantmentPool)
					{
						if (ed.tableConflictWith(n.enchantData.getRegistryEntry()))
						{
							n.remove = true;
							maxWeight -= n.weight;
						}
					}
				}
				else
				{
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
			}
			byte getLevel = (byte) random.nextTriangleInt(entry.currentLevel + 1, maxLevel);
			leftCost -= entry.toLevel(getLevel);
			tryamount++;
			if (!entry.canAddLevel(leftCost))
			{
				if (entry.currentLevel > 0) builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
			}
			else
			{
				if (balanced)
				{
					if (enchantmentPool.size() > 1)
					{
						int weight = Math.max(1, (maxWeight - entry.weight) / (enchantmentPool.size() - 1));
						int changed = weight - entry.weight;
						entry.weight = weight;
						maxWeight += changed;
					}
				}
				else if (firstUp)
				{
					int weightUp = Math.abs(random.nextTriangleInt(-maxWeight, maxWeight));
					entry.weight += weightUp;
					maxWeight += weightUp;
					firstUp = false;
				}
			}
		}
		for (var i : enchantmentPool)
			if (i.currentLevel > 0) builder.set(i.enchantData.getRegistryEntry(), i.currentLevel);
		costAchieve.setValue(maxCost - leftCost);
		lapisCount.setValue(Math.max((maxCost - leftCost) / ((efficiency + 1) * 200) - 1, 0));
		var ret = builder.build();
		int n = ret.getSize();
		crystalCount.setValue(Math.max(tryamount - 1, 0) + (random.nextEvenInt(1, n) << 10));
		return ret;
	}
}
