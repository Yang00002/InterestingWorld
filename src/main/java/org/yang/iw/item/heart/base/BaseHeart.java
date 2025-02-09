package org.yang.iw.item.heart.base;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.iw.item.IWItems;
import org.yang.iw.item.heart.AbstractHeart;
import org.yang.iw.util.IWUtil;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.heartflag.HeartFlagOnlyCheckable;

import java.util.function.Function;

public abstract class BaseHeart extends AbstractHeart
{
	@Override
	public boolean hasGlint(ItemStack stack)
	{
		var type = HeartDataFlag.getFromItemStack(stack).getTypeTaking();
		return type != HeartFlagOnlyCheckable.HeartTypeTaking.NULL;
	}

	private static final CustomModelDataComponent ENCHANTED_MODEL_INDEX = IWUtil.getModelComponent(1);

	public int getMaxSupportLevel()
	{
		return HeartDataFlag.getFromItem(this).getMaterialLevel();
	}

	public BaseHeart(Settings settings, Function<Integer, Integer> enchantAbility, int lvl)
	{
		super(settings.component(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT),
				enchantAbility, lvl);
	}

	@Override
	public boolean setEnchant(ItemStack stack, ItemEnchantmentsComponent component, int enchantCost)
	{
		if (super.setEnchant(stack, component, enchantCost))
		{
			stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, ENCHANTED_MODEL_INDEX);
			return true;
		}
		return false;
	}

	@Override
	public boolean dumpPreEnchant(ItemStack stack, ItemEnchantmentsComponent component, int enchantCost)
	{
		if (super.dumpPreEnchant(stack, component, enchantCost))
		{
			stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, ENCHANTED_MODEL_INDEX);
			return true;
		}
		return false;
	}

	@Override
	public void removeEnchant(ItemStack stack)
	{
		super.removeEnchant(stack);
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT);
	}

	@Override
	public boolean supportAbility()
	{
		return false;
	}

	public static BaseHeart baseHeartSupportLevel(int lvl)
	{
		switch (lvl)
		{
			case 2 ->
			{
				return IWItems.IRON_HEART;
			}
			case 3 ->
			{
				return IWItems.GOLD_HEART;
			}
			case 4 ->
			{
				return IWItems.DIAMOND_HEART;
			}
			case 5 ->
			{
				return IWItems.NETHERITE_HEART;
			}
			case 6 ->
			{
				return IWItems.ENDERITE_HEART;
			}
			case 7, 8 ->
			{
				return IWItems.VOIDALLOY_HEART;
			}
			default ->
			{
				return IWItems.COPPER_HEART;
			}
		}
	}

	@Override
	public ItemStack getDefaultStack(int materialLevel)
	{
		return getDefaultStack();
	}

	@Override
	public Text getName(ItemStack stack)
	{
		if (HeartDataFlag.getFromItemStack(stack).getTypeTaking() == HeartFlagOnlyCheckable.HeartTypeTaking.ENCHANT)
			return Text.translatable("item.iw.enchantedheart").withColor(getNameColorRGB());
		return Text.translatable(getTranslationKey()).withColor(getNameColorRGB());
	}
}
