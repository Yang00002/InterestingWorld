package org.yang.interestingworld.rune.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.rune.IWRuneAbility;

import java.util.List;

import static org.yang.interestingworld.IWUtil.EnergyTool.extractAutomicEnergy;

public class SweepingAbility extends IWRuneAbility
{
	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("sweeping_ability_detail").withColor(getColor()));
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("sweeping_ability_title");
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		Item it = stack.getItem();
		if (it instanceof EnergyToolItem)
		{
			return !((EnergyToolItem) it).canSweep(stack);
		}
		return false;
	}

	@Override
	public boolean canSweeping(ItemStack stack, PlayerEntity entity)
	{
		if (entity.getWorld().isClient) return false;
		return extractAutomicEnergy(stack, entity, 1);
	}
}
