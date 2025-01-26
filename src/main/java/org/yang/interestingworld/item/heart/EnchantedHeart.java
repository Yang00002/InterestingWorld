package org.yang.interestingworld.item.heart;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.item.heart.base.BaseHeart;
import org.yang.interestingworld.persistentdata.IWPersistentData;
import org.yang.interestingworld.util.Server;
import org.yang.interestingworld.util.style.Color;

import java.util.List;

import static org.yang.interestingworld.util.IWEnchantmentUtil.getWorldLevelOfXpCost;

public class EnchantedHeart extends Item
{
	public EnchantedHeart(Settings settings)
	{
		super(settings.maxCount(1));
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		int level = stack.getOrDefault(IWComponents.ENCHANT_VALUE, 0);
		IWPersistentData data = Server.getPersistentData();
		if (data != null && level <= data.worldEnergyLevel) tooltip.add(
				Text.translatable("tooltip.rune.needlevel").withColor(Color.GREEN_RGB)
						.append(Text.literal(String.valueOf(level)).withColor(Color.getLevelColor(level))));
		else tooltip.add(Text.translatable("tooltip.rune.needlevel").withColor(Color.RED_RGB)
				.append(Text.literal(String.valueOf(level)).withColor(Color.getLevelColor(level))));
	}

	public ItemStack stackOf(ItemEnchantmentsComponent component, int cost, BaseHeart from)
	{
		ItemStack it = new ItemStack(this);
		it.set(DataComponentTypes.STORED_ENCHANTMENTS, component);
		it.set(IWComponents.ENCHANT_VALUE, getWorldLevelOfXpCost(cost));
		it.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(from.getModelIndex()));
		return it;
	}

	public ItemStack getDefaultStack()
	{
		ItemStack it = new ItemStack(this);
		it.set(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT);
		return it;
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.getLevelColor(
				stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT).value() +
				1));
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return !stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty();
	}

}
