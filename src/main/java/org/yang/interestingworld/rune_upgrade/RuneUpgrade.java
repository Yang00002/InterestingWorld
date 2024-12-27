package org.yang.interestingworld.rune_upgrade;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.style.TextStyle;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.List;

public class RuneUpgrade extends AbstractRuneUpgrade
{
	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(TextStyle.BOLD_STYLE)
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
			tooltip.add(Text.translatable("upgrade.need.txet").withColor(Color.GRAY_RGB));
			for (var i : ig.entrySet())
			{
				var item = i.getKey();
				var count = i.getValue();
				tooltip.add(Text.translatable(item.getTranslationKey()).append(" " + count).withColor(Color.GRAY_RGB));
			}
		}
	}

	@Override
	public void appendBanedToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(TextStyle.BOLD_STYLE)
				.withColor(Color.GRAY_RGB));
		tooltip.add(Text.translatable("banedupgradedetail").withColor(Color.GRAY_RGB));
	}

	@Override
	public void applyUpgrade(ItemStack toolStack)
	{
		toolStack.set(IWComponents.UPGRADE_TEXT, getTitleText().setStyle(TextStyle.BOLD_STYLE).withColor(getColor()));
		toolStack.set(IWComponents.DATA_FLAGS, EnergyToolDataFlag.copyFromItemStack(toolStack).setUpgradeLevel(level())
				.updateLevelFromItemStack(toolStack));
	}
}
