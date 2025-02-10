package org.yang.iw.rune_upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.style.Color;
import org.yang.iw.util.style.TextStyle;

import java.util.List;
import java.util.Map;

public abstract class AbstractRuneUpgrade
{
	private final MutableText tip = Text.translatable("upgrade.tip." + id()).withColor(getColor());
	private final MutableText title = Text.translatable("upgrade.title." + id());

	public abstract int getColor();

	public abstract String id();

	public MutableText getTitleText()
	{
		return title;
	}

	public void appendToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(title).append("】").setStyle(TextStyle.BOLD_STYLE).withColor(getColor()));
		tooltip.add(tip);
		appendIngredientToolTip(tooltip);
	}

	public abstract boolean canApplyTo(ItemStack stack);

	public void applyUpgrade(ItemStack toolStack)
	{
		applyUpgradeContent(toolStack);
		toolStack.set(IWComponents.UPGRADE_TEXT, getTitleText().withColor(getColor()));
		EnergyToolDataFlag.builder(toolStack).setUpgradeLevel(level()).updateLevelFromItemStack(toolStack)
				.dump(toolStack);
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
			tooltip.add(Text.translatable(TranslationPool.TOOLTIP_UPGRADE_NEED_BEGIN).withColor(Color.GRAY_RGB));
			for (var i : ig.entrySet())
			{
				var item = i.getKey();
				var count = i.getValue();
				tooltip.add(Text.translatable(item.getTranslationKey()).append(" " + count).withColor(Color.GRAY_RGB));
			}
		}
	}
}
