package org.yang.iw.item.heart;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.iw.IWMain;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.component.AbilityComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.persistentdata.IWPersistentData;
import org.yang.iw.util.style.Color;

import java.util.List;
import java.util.function.Function;

public class AbilityHeart extends Item implements HideAbilityTooltip
{

	public final Identifier identifier;
	public final int nameColorRGB;
	private final Function<Integer, BoostFunctionMap> uniqueBoost;

	public boolean supportAbility(AbstractAbility ability)
	{
		return ability.include(this);
	}

	public AbilityHeart(Settings settings, int nameColorRGB, Identifier identifier, Function<Integer,
			BoostFunctionMap> uniqueBoost)
	{
		super(settings.maxCount(1));
		this.nameColorRGB = nameColorRGB;
		this.identifier = identifier;
		this.uniqueBoost = uniqueBoost;
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return !stack.getOrDefault(IWComponents.ABILITY, AbilityComponent.DEFAULT).isEmpty();
	}

	public boolean setAbility(ItemStack stack, AbstractAbility ability)
	{
		if (!ability.isEmpty() && supportAbility(ability))
		{
			stack.set(IWComponents.ABILITY, new AbilityComponent(ability));
			return true;
		}
		return false;
	}

	public void removeAbility(ItemStack stack)
	{
		stack.remove(IWComponents.ABILITY);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		var component = stack.getOrDefault(IWComponents.ABILITY, AbilityComponent.DEFAULT);
		if (component.isEmpty())
			tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_CAN_TAKE_ABILITY).withColor(Color.GRAY_RGB));
		else
		{
			var ability = component.ability();
			int takeLvl = ability.level();
			IWPersistentData data = IWMain.getPersistentData();
			if (data != null && takeLvl <= data.worldEnergyLevel) tooltip.add(
					Text.translatable(TranslationPool.TOOLTIP_HEART_ABILITY_LEVEL).withColor(Color.GRAY_RGB)
							.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
			else tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_ABILITY_LEVEL).withColor(Color.RED_RGB)
					.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
			tooltip.add(Text.empty());
			tooltip.add(ability.getTitleText());
			tooltip.add(ability.getTip());
		}
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(nameColorRGB);
	}

	@Override
	public BoostFunctionMap interestingWorld$uniqueBoostFor(ItemStack stack)
	{
		var component = stack.interestingWorld$getAbility();
		return component.isEmpty() ? null : uniqueBoost.apply(component.ability().level());
	}
}
