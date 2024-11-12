package org.yang.interestingworld.rune;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.persistentdata.IWPersistentData;

import java.util.List;

import static org.yang.interestingworld.IWUtil.TextStyle.GRAY_RGB;

public class IWRuneAbility extends IWAbstractRuneAbility
{
	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】")
				.setStyle(IWUtil.TextStyle.getBoldTextStyle(getColor())));
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return true;
	}

	@Override
	public void appendBanedToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】")
				.setStyle(IWUtil.TextStyle.getBoldTextStyle(GRAY_RGB)));
		tooltip.add(getTitleText().withColor(GRAY_RGB));
	}

	@Override
	public boolean canWork()
	{
		IWPersistentData data = IWUtil.Server.getPersistentData();
		if (data != null && level() <= data.worldEnergyLevel) return true;
		return false;
	}

}
