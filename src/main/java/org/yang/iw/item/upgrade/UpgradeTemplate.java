package org.yang.iw.item.upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.yang.iw.IWMain;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.persistentdata.IWPersistentData;
import org.yang.iw.upgrade.AbstractUpgrade;
import org.yang.iw.util.style.Color;

import java.util.List;

public class UpgradeTemplate extends Item implements HideUpgradeTooltip
{

	private final AbstractUpgrade upgrade;

	@Override
	public Text getName(ItemStack stack)
	{
		return super.getName(stack).copy().withColor(upgrade.getColor());
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		int takeLvl = upgrade.level();
		IWPersistentData data = IWMain.getPersistentData();
		if (data != null && takeLvl <= data.worldEnergyLevel) tooltip.add(
				Text.translatable(TranslationPool.TOOLTIP_UPGRADE_TEMPLATE_LEVEL_N).withColor(Color.GRAY_RGB)
						.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
		else tooltip.add(Text.translatable(TranslationPool.TOOLTIP_UPGRADE_TEMPLATE_LEVEL_N).withColor(Color.RED_RGB)
				.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
		tooltip.add(Text.empty());
		tooltip.add(upgrade.getTitleText());
		tooltip.add(upgrade.getTip());
	}

	public UpgradeTemplate(Item.Settings settings, AbstractUpgrade abstractRuneUpgrade)
	{
		super(settings.maxCount(1));
		upgrade = abstractRuneUpgrade;
	}

	public int getLevel()
	{
		return upgrade.level();
	}

	public AbstractUpgrade getUpgrade()
	{
		return upgrade;
	}
}
