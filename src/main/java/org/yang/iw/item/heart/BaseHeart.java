package org.yang.iw.item.heart;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.iw.component.HeartDataFlag;
import org.yang.iw.item.IWItems;

import java.util.function.Function;

public class BaseHeart extends AbstractHeart
{
	private final int nameRGB;

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		var type = HeartDataFlag.fromItemStack(stack).getTypeTaking();
		return type != HeartDataFlag.HeartTypeTaking.NULL;
	}

	@Override
	public int getNameColorRGB()
	{
		return nameRGB;
	}

	public BaseHeart(Settings settings, Function<Integer, Integer> enchantAbility, int lvl, int color)
	{
		super(settings.component(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT),
				enchantAbility, lvl);
		nameRGB = color;
	}

	public static Function<Integer, Integer> handleEnchantAbility(Function<Integer, Integer> origin, int lvl)
	{
		int n = origin.apply(lvl);
		return i -> n;
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
			case 7, 8, 9, 10 ->
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
		if (HeartDataFlag.fromItemStack(stack).getTypeTaking() == HeartDataFlag.HeartTypeTaking.ENCHANT)
			return Text.translatable("item.iw.enchantedheart").withColor(getNameColorRGB());
		return Text.translatable(getTranslationKey()).withColor(getNameColorRGB());
	}
}
