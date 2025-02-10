package org.yang.iw.item.heart;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.yang.iw.component.HeartDataFlag;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.persistentdata.IWPersistentData;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.util.Server;
import org.yang.iw.util.style.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.yang.iw.util.IWEnchantmentUtil.getWorldLevelOfXpCost;
import static org.yang.iw.util.IWRuneAbilityUtil.getAbility;

public abstract class AbstractHeart extends Item
{

	public final Function<Integer, Integer> enchantAbility;

	public final int getEnchantAbility(int level)
	{
		return supportEnchant() ? enchantAbility.apply(level) : 0;
	}

	@Override
	public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack,
												  ItemStack newStack)
	{
		return !ItemStack.areItemsEqual(oldStack, newStack);
	}

	private static final List<Short> abilitiesSupport = new ArrayList<>();

	public List<Short> getAbilitiesSupport()
	{
		return abilitiesSupport;
	}

	public enum SupportAbilityResult
	{
		SUPPORT, LOW_LEVEL, NO_ABILITY
	}

	public SupportAbilityResult supportAbility(ItemStack stack, AbstractRuneAbility ability)
	{
		if (!getAbilitiesSupport().contains(ability.index)) return SupportAbilityResult.NO_ABILITY;
		if (HeartDataFlag.fromItemStack(stack).getMaterialLevel() < ability.level())
			return SupportAbilityResult.LOW_LEVEL;
		return SupportAbilityResult.SUPPORT;
	}

	private static Settings handleSettings(Settings settings, Function<Integer, Integer> initializer, int lvl)
	{
		settings.maxCount(1);
		int e = initializer == null ? 0 : initializer.apply(lvl);
		if (e > 0) settings.component(DataComponentTypes.ENCHANTABLE, new EnchantableComponent(e));
		settings.component(IWComponents.HEART_FLAG, HeartDataFlag.builder().setMaterialLevel(lvl).build());
		return settings;
	}

	public AbstractHeart(Settings settings, Function<Integer, Integer> initializer, int defaultMaterialLevel)
	{
		super(handleSettings(settings, initializer, defaultMaterialLevel));
		this.enchantAbility = initializer;
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		var type = HeartDataFlag.fromItemStack(stack).getTypeTaking();
		return (type == HeartDataFlag.HeartTypeTaking.ABILITY) || (type == HeartDataFlag.HeartTypeTaking.ENCHANT);
	}


	public abstract int getNameColorRGB();

	public final boolean supportEnchant()
	{
		return enchantAbility != null;
	}

	public abstract boolean supportAbility();


	public void removeEnchant(ItemStack stack)
	{
		HeartDataFlag flag = HeartDataFlag.fromItemStack(stack);
		if (flag.getTypeTaking() == HeartDataFlag.HeartTypeTaking.ENCHANT)
		{
			flag.getBuilder().setTypeTaking(HeartDataFlag.HeartTypeTaking.NULL).dump(stack);
			stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
			if (supportEnchant()) stack.set(DataComponentTypes.ENCHANTABLE,
					new EnchantableComponent(getEnchantAbility(flag.getMaterialLevel())));
		}
	}

	public boolean setEnchant(ItemStack stack, ItemEnchantmentsComponent component, int enchantCost)
	{
		if (supportEnchant())
		{
			HeartDataFlag.Builder flag = HeartDataFlag.builder(stack);
			flag.setTypeTaking(HeartDataFlag.HeartTypeTaking.ENCHANT);
			flag.setTakingLevel(getWorldLevelOfXpCost(enchantCost));
			stack.set(IWComponents.HEART_FLAG, flag.build());
			stack.set(DataComponentTypes.STORED_ENCHANTMENTS, component);
			stack.remove(DataComponentTypes.ENCHANTABLE);
			return true;
		}
		return false;
	}

	public boolean dumpPreEnchant(ItemStack stack, ItemEnchantmentsComponent component, int enchantCost)
	{
		if (supportEnchant())
		{
			HeartDataFlag flag = HeartDataFlag.fromItemStack(stack);
			if (flag.getTypeTaking() == HeartDataFlag.HeartTypeTaking.PREENCHANT)
			{
				var builder = flag.getBuilder();
				builder.setTypeTaking(HeartDataFlag.HeartTypeTaking.ENCHANT);
				builder.setTakingLevel(getWorldLevelOfXpCost(enchantCost));
				stack.set(IWComponents.HEART_FLAG, builder.build());
				stack.set(DataComponentTypes.STORED_ENCHANTMENTS, component);
				stack.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
				stack.remove(DataComponentTypes.ENCHANTABLE);
				return true;
			}
		}
		return false;
	}

	public void setPreEnchantFlag(ItemStack stack)
	{
		if (supportEnchant())
		{
			stack.set(IWComponents.HEART_FLAG,
					HeartDataFlag.builder(stack).setTypeTaking(HeartDataFlag.HeartTypeTaking.PREENCHANT).build());
		}
	}

	public boolean setAbility(ItemStack stack, short abilityIndex)
	{
		if (supportAbility())
		{
			AbstractRuneAbility ab = getAbility(abilityIndex);
			if (ab != IWRuneAbilities.DEFAULT_ABILITY && getAbilitiesSupport().contains(abilityIndex))
			{
				HeartDataFlag.Builder flag = HeartDataFlag.builder(stack);
				flag.setTypeTaking(HeartDataFlag.HeartTypeTaking.ABILITY);
				flag.setTakingLevel(ab.level());
				stack.set(IWComponents.HEART_FLAG, flag.build());
				stack.set(IWComponents.ABILITY_INDEX, abilityIndex);
				stack.remove(DataComponentTypes.ENCHANTABLE);
				return true;
			}
		}
		return false;
	}

	public boolean setAbility(ItemStack stack, AbstractRuneAbility ability)
	{
		if (supportAbility())
		{
			if (ability != IWRuneAbilities.DEFAULT_ABILITY && getAbilitiesSupport().contains(ability.index))
			{
				HeartDataFlag.Builder flag = HeartDataFlag.builder(stack);
				flag.setTypeTaking(HeartDataFlag.HeartTypeTaking.ABILITY);
				flag.setTakingLevel(ability.level());
				stack.set(IWComponents.HEART_FLAG, flag.build());
				stack.set(IWComponents.ABILITY_INDEX, ability.index);
				stack.remove(DataComponentTypes.ENCHANTABLE);
				return true;
			}
		}
		return false;
	}

	public void removeAbility(ItemStack stack)
	{
		HeartDataFlag flag = HeartDataFlag.fromItemStack(stack);
		if (flag.getTypeTaking() == HeartDataFlag.HeartTypeTaking.ABILITY)
		{
			flag.getBuilder().setTypeTaking(HeartDataFlag.HeartTypeTaking.NULL).dump(stack);
			stack.remove(IWComponents.ABILITY_INDEX);
			if (supportEnchant()) stack.set(DataComponentTypes.ENCHANTABLE,
					new EnchantableComponent(getEnchantAbility(flag.getMaterialLevel())));
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		HeartDataFlag flagOnlyCheckable = HeartDataFlag.fromItemStack(stack);
		int lvl = flagOnlyCheckable.getMaterialLevel();
		tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_CONTAINER_LEVEL).withColor(Color.GRAY_RGB)
				.append(Text.literal(String.valueOf(lvl)).withColor(Color.getLevelColor(lvl))));
		switch (flagOnlyCheckable.getTypeTaking())
		{
			case NULL ->
			{
				if (supportEnchant())
				{
					tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_CAN_ENCHANT).withColor(Color.GRAY_RGB));
				}
				if (supportAbility())
				{
					tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_CAN_TAKE_ABILITY)
							.withColor(Color.GRAY_RGB));
				}
			}
			case PREENCHANT -> tooltip.add(
					Text.translatable(TranslationPool.TOOLTIP_HEART_NEED_FURTHER_ENCHANT).withColor(Color.GRAY_RGB));
			case ENCHANT ->
			{
				int takeLvl = flagOnlyCheckable.getTakingLevel();
				IWPersistentData data = Server.getPersistentData();
				if (data != null && takeLvl <= data.worldEnergyLevel) tooltip.add(
						Text.translatable(TranslationPool.TOOLTIP_HEART_ENCHANT_LEVEL).withColor(Color.GRAY_RGB)
								.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
				else tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_ENCHANT_LEVEL).withColor(Color.RED_RGB)
						.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
			}
			case ABILITY ->
			{
				int takeLvl = flagOnlyCheckable.getTakingLevel();
				IWPersistentData data = Server.getPersistentData();
				if (data != null && takeLvl <= data.worldEnergyLevel) tooltip.add(
						Text.translatable(TranslationPool.TOOLTIP_HEART_ABILITY_LEVEL).withColor(Color.GRAY_RGB)
								.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
				else tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_ABILITY_LEVEL).withColor(Color.RED_RGB)
						.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
				var ab = getAbility(stack);
				ab.appendToolTip(tooltip);
			}
		}
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(getNameColorRGB());
	}

	public ItemStack getDefaultStack(int materialLevel)
	{
		var stack = getDefaultStack();
		stack.set(IWComponents.HEART_FLAG, HeartDataFlag.builder(stack).setMaterialLevel(materialLevel).build());
		return stack;
	}
}
