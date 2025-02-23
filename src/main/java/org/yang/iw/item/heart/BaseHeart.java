package org.yang.iw.item.heart;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.yang.iw.component.BoostableComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.style.Color;

import java.util.List;

import static org.yang.iw.util.style.Color.getLevelColor;

public class BaseHeart extends Item
{
	public final int nameColorRGB;
	public final int materialLevel;
	public final int enchantAbility;

	public BaseHeart(Settings settings, int materialLevel, int boostTime, int enchantAbility, int nameColorRGB)
	{
		super(settings.component(IWComponents.BOOSTABLE, BoostableComponent.of(boostTime))
				.enchantable(Math.max(enchantAbility, 1)).maxCount(1));
		this.nameColorRGB = nameColorRGB;
		this.materialLevel = Math.clamp(materialLevel, 0, 10);
		this.enchantAbility = Math.max(enchantAbility, 1);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(nameColorRGB);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_CONTAINER_LEVEL).withColor(Color.GRAY_RGB)
				.append(Text.literal(String.valueOf(materialLevel)).withColor(getLevelColor(materialLevel))));
		int count = stack.getOrDefault(IWComponents.BOOSTABLE, BoostableComponent.DEFAULT).remainBoostTime();
		if (count < 1)
		{
			tooltip.add(Text.translatable(TranslationPool.TOOLTIP_BOOSTTIME_OUT).withColor(Color.GRAY_RGB));
			tooltip.add(Text.empty());
		}
		else
		{
			tooltip.add(Text.translatable(TranslationPool.TOOLTIP_REMAIN_BOOSTTIME).withColor(Color.GRAY_RGB)
					.append(Text.literal(String.valueOf(count))
							.withColor(getLevelColor(Math.min(materialLevel, count)))));
			if (stack.interestingWorld$getBoosts().isEmpty())
			{
				if (stack.getEnchantments().isEmpty())
				{
					tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_CAN_ATTACH_BOOST)
							.withColor(Color.GRAY_RGB));
				}
				else
				{
					tooltip.add(
							Text.translatable(TranslationPool.TOOLTIP_HEART_ENCHANT_CONVERT).withColor(Color.GRAY_RGB));
					tooltip.add(Text.empty());
				}
			}
			else
			{
				tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_BOOST).withColor(Color.GRAY_RGB));
				tooltip.add(Text.empty());
			}
		}
	}
}
