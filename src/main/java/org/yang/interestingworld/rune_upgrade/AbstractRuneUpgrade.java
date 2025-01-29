package org.yang.interestingworld.rune_upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.style.TextStyle;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.List;
import java.util.Map;

public abstract class AbstractRuneUpgrade
{
	public abstract int getColor();

	public abstract String getIdentifierString();

	public abstract MutableText getTitleText();

	public void appendToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(TextStyle.BOLD_STYLE)
				.withColor(getColor()));
		appendExplanation(tooltip);
		appendIngredientToolTip(tooltip);
	}

	public abstract void appendExplanation(List<Text> tooltip);

	public abstract boolean canApplyTo(ItemStack stack);

	public void applyUpgrade(ItemStack toolStack)
	{
		applyUpgradeContent(toolStack);
		toolStack.set(IWComponents.UPGRADE_TEXT, getTitleText().withColor(getColor()));
		toolStack.set(IWComponents.TOOL_FLAG, EnergyToolDataFlag.copyFromItemStack(toolStack).setUpgradeLevel(level())
				.updateLevelFromItemStack(toolStack));
	}

	protected abstract void applyUpgradeContent(ItemStack toolStack);

	public abstract Map<Item, Integer> getIngredients();

	public abstract int level();

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
}
