package org.yang.interestingworld.item.rune;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.yang.interestingworld.persistentdata.IWPersistentData;
import org.yang.interestingworld.util.Server;

import java.util.List;

public class RuneItem extends Item
{
	public static final int ABILITY = 1;

	public int getLevel(ItemStack stack)
	{
		return 0;
	}


	public void commonAppendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		int level = getLevel(stack);
		IWPersistentData data = Server.getPersistentData();
		if (data != null && level <= data.worldEnergyLevel)
			tooltip.add(Text.translatable("tooltip.rune.needlevel").append(": " + level).formatted(Formatting.GREEN));
		else tooltip.add(Text.translatable("tooltip.rune.needlevel").append(": " + level).formatted(Formatting.RED));
	}

	public RuneItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getEnchantability()
	{
		return 15;
	}
}
