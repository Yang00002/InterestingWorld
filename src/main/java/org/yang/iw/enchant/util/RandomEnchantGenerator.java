package org.yang.iw.enchant.util;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import org.yang.iw.enchant.resource.ConflictGroup;
import org.yang.iw.enchant.resource.RandomEnchantGroup;
import org.yang.iw.item.IWItems;
import org.yang.iw.item.heart.AbstractHeart;
import org.yang.iw.item.heart.base.BaseHeart;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.yang.iw.util.IWEnchantmentUtil.getRandomXpCostOfWorldLevel;

public class RandomEnchantGenerator
{
	public static ItemStack generate(int seed, int level, AbstractHeart container)
	{
		if (container instanceof BaseHeart baseHeart) level = Math.min(level, baseHeart.getMaxSupportLevel());
		Random random = new Random(seed); //rd
		//获得附魔组
		int enchantGroupWeight = random.nextInt(1, RandomEnchantGroup.getMaxWeight() + 1);
		RandomEnchantGroup enchantGroup = null;
		for (var g : RandomEnchantGroup.getEnchantGroups())
		{
			if (g.getWeight() >= enchantGroupWeight)
			{
				enchantGroup = g;
				break;
			}
			enchantGroupWeight -= g.getWeight();
		}
		if (enchantGroup == null) return IWItems.HEART.getDefaultStack();
		int enchantGroupSize = enchantGroup.getEnchants().size();
		if (enchantGroupSize < 1) return IWItems.HEART.getDefaultStack();
		//附魔池
		List<RandomEnchantEntry> enchantmentPool = new LinkedList<>();
		//获得最大附魔数
		int maxEnchantCount = Math.min((level >> 1) + 1, enchantGroupSize);
		int dif = random.nextInt(-1, 1) + random.nextInt(0, 2);
		maxEnchantCount += dif;
		if (maxEnchantCount < 1) maxEnchantCount = 1;
		//获得最大消耗
		int maxCost = getRandomXpCostOfWorldLevel(level, random);
		int maxWeight = 0;
		for (var i : enchantGroup.getEnchants())
		{
			var g = RandomEnchantEntry.of(i, maxCost, level);
			if (g != null)
			{
				enchantmentPool.add(g);
				maxWeight += g.weight;
			}
		}
		if (maxWeight < 1) return IWItems.HEART.getDefaultStack();
		int leftCost = maxCost;
		boolean firstUp = true;
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		do
		{
			RandomEnchantEntry entry = null;
			{
				int entry_w = random.nextInt(1, maxWeight + 1);
				var it = enchantmentPool.iterator();
				while (it.hasNext())
				{
					var e = it.next();
					if (e.remove) it.remove();
					else
					{
						if (e.weight >= entry_w)
						{
							entry = e;
							break;
						}
						entry_w -= e.weight;
					}
				}
				if (entry == null) break;
			}
			var enchantDate = entry.enchantData;
			int maxLevel;
			if (entry.currentLevel == 0)
			{
				int punishedCost;
				if ((leftCost <= entry.addPunish) || (enchantDate.getLevelCost(1) >
													  (punishedCost = (int) (entry.mulPunishForOther *
																			 (leftCost - entry.addPunish)))))
				{
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
				maxLevel = enchantDate.maxLevelWithCost((byte) 1, enchantDate.getMaxRandomLevel(), punishedCost);
			}
			else
			{
				if (entry.currentLevel >= enchantDate.getMaxRandomLevel())
				{
					builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
				maxLevel = enchantDate.maxLevelWithCost(entry.currentLevel, enchantDate.getMaxRandomLevel(),
						(int) (entry.mulPunishForOther * leftCost));
				if (maxLevel <= entry.currentLevel)
				{
					builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
			}
			if (!entry.chosen)
			{
				if (maxEnchantCount > 0)
				{
					entry.chosen = true;
					maxEnchantCount--;
					var conf = enchantDate.getConflicts();
					for (RandomEnchantEntry n : enchantmentPool)
					{
						if (!n.remove && !n.chosen)
						{
							var nEntry = n.enchantData.getRegistryEntry();
							if (conf.containsKey(nEntry))
							{
								var d = conf.get(nEntry);
								if (d == ConflictGroup.fatalGroup)
								{
									n.remove = true;
									maxWeight -= n.weight;
								}
								else
								{
									n.addPunish += d.addPunish;
									n.mulPunishForOther /= d.mulPunish;
									n.mulPunishForSelf *= d.mulPunish;
									int punishDecrease = Math.min(d.weightPunish, n.weight - 1);
									n.weight -= punishDecrease;
									maxWeight -= punishDecrease;
								}
							}
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
			byte getLevel = (byte) (
					((random.nextInt(entry.currentLevel, maxLevel) + random.nextInt(entry.currentLevel, maxLevel)) >>
					 1) + 1);
			int cost = entry.currentLevel < 1 ? (entry.addPunish +
												 (int) (enchantDate.getLevelCost(getLevel) * entry.mulPunishForSelf))
											  : (int) ((enchantDate.getLevelCost(getLevel) -
														enchantDate.getLevelCost(entry.currentLevel)) *
													   entry.mulPunishForSelf);
			leftCost -= cost;
			entry.currentLevel = getLevel;
			if (entry.currentLevel >= enchantDate.getMaxRandomLevel())
			{
				builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
				continue;
			}
			if ((enchantDate.getAddLevelCost(entry.currentLevel, entry.currentLevel + 1)) >
				(int) (entry.mulPunishForOther * leftCost))
			{
				builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
				continue;
			}
			if (firstUp)
			{
				int weightUp = Math.abs(
						random.nextInt(-maxWeight, 1) + random.nextInt(-(maxWeight >> 1), maxWeight + 1));
				if (weightUp > 0)
				{
					entry.weight += weightUp;
					maxWeight += weightUp;
				}
				firstUp = false;
			}
		} while (maxWeight > 0);
		for (var i : enchantmentPool)
			if (i.currentLevel > 0) builder.set(i.enchantData.getRegistryEntry(), i.currentLevel);
		var ec = builder.build();
		if (ec.getSize() < 1) return IWItems.HEART.getDefaultStack();
		var ret = container.getDefaultStack(level);
		container.setEnchant(ret, builder.build(), maxCost - leftCost);
		return ret;
	}
}
