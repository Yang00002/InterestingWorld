package org.yang.interestingworld.item.upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.yang.interestingworld.persistentdata.IWPersistentData;
import org.yang.interestingworld.rune_upgrade.AbstractRuneUpgrade;
import org.yang.interestingworld.util.Server;
import org.yang.interestingworld.util.style.Color;

import java.util.List;

public class UpgradeTemplate extends Item
{

	private final AbstractRuneUpgrade upgrade;

	@Override
	public Text getName(ItemStack stack)
	{
		return upgrade.getTitleText().append(Text.translatable("abilityrune.suffix")).withColor(upgrade.getColor());
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		int takeLvl = upgrade.level();
		IWPersistentData data = Server.getPersistentData();
		if (data != null && takeLvl <= data.worldEnergyLevel) tooltip.add(
				Text.translatable("tooltip.upgradeTemplate.level").withColor(Color.GRAY_RGB)
						.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
		else tooltip.add(Text.translatable("tooltip.upgradeTemplate.level").withColor(Color.RED_RGB)
				.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
		upgrade.appendToolTip(tooltip);
	}

	public UpgradeTemplate(Item.Settings settings, AbstractRuneUpgrade abstractRuneUpgrade)
	{
		super(settings.maxCount(1));
		upgrade = abstractRuneUpgrade;
	}

	public int getLevel()
	{
		return upgrade.level();
	}

	public AbstractRuneUpgrade getUpgrade()
	{
		return upgrade;
	}
}
