package org.yang.interestingworld.rune;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.util.EnergyToolDataFlag;

import java.util.List;

import static org.yang.interestingworld.IWUtil.TextStyle.GRAY_RGB;

public class IWRuneUpgrade extends IWAbstractRuneUpgrade
{
	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(IWUtil.TextStyle.BOLD_STYLE)
				.withColor(getColor()));
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return true;
	}

	public void appendIngredientToolTip(List<Text> tooltip)
	{
		var ig = getIngredients();
		if (ig != null)
		{
			tooltip.add(Text.empty());
			tooltip.add(Text.translatable("upgrade.need.txet").withColor(GRAY_RGB));
			for (var i : ig.entrySet())
			{
				var item = i.getKey();
				var count = i.getValue();
				tooltip.add(Text.translatable(item.getTranslationKey()).append(" " + count).withColor(GRAY_RGB));
			}
		}
	}

	@Override
	public void appendBanedToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(IWUtil.TextStyle.BOLD_STYLE)
				.withColor(GRAY_RGB));
		tooltip.add(Text.translatable("banedupgradedetail").withColor(GRAY_RGB));
	}

	@Override
	public void applyUpgrade(ItemStack toolStack)
	{
		toolStack.set(IWComponents.UPGRADE_TEXT, getTitleText());
		toolStack.set(IWComponents.DATA_FLAGS, EnergyToolDataFlag.copyFromItemStack(toolStack).setUpgradeLevel(level())
				.updateLevelFromItemStack(toolStack));
	}
}
