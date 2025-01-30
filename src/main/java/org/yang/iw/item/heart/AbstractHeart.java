package org.yang.iw.item.heart;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.yang.iw.IWComponents;
import org.yang.iw.persistentdata.IWPersistentData;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.util.Server;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.heartflag.HeartFlagOnlyCheckable;
import org.yang.iw.util.style.Color;

import java.util.ArrayList;
import java.util.List;

import static org.yang.iw.util.IWEnchantmentUtil.getWorldLevelOfXpCost;
import static org.yang.iw.util.IWRuneAbilityUtil.getAbility;

public abstract class AbstractHeart extends Item
{
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return supportEnchant() &&
			   HeartDataFlag.getFromItemStack(stack).getTypeTaking() == HeartFlagOnlyCheckable.HeartTypeTaking.NULL;
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
		if (HeartDataFlag.getFromItemStack(stack).getMaterialLevel() < ability.level())
			return SupportAbilityResult.LOW_LEVEL;
		return SupportAbilityResult.SUPPORT;
	}

	public AbstractHeart(Settings settings)
	{
		super(settings.maxCount(1));
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		var type = HeartDataFlag.getFromItemStack(stack).getTypeTaking();
		return (type == HeartFlagOnlyCheckable.HeartTypeTaking.ABILITY) ||
			   (type == HeartFlagOnlyCheckable.HeartTypeTaking.ENCHANT);
	}

	public abstract int getNameColorRGB();

	public abstract boolean supportEnchant();

	public abstract boolean supportAbility();


	public void removeEnchant(ItemStack stack)
	{
		HeartDataFlag flag = HeartDataFlag.copyFromItemStack(stack);
		if (flag.getTypeTaking() == HeartFlagOnlyCheckable.HeartTypeTaking.ENCHANT)
		{
			flag.setTypeTaking(HeartFlagOnlyCheckable.HeartTypeTaking.NULL);
			stack.set(IWComponents.HEART_FLAG, flag);
			stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
		}
	}

	public boolean setEnchant(ItemStack stack, ItemEnchantmentsComponent component, int enchantCost)
	{
		if (supportEnchant())
		{
			HeartDataFlag flag = HeartDataFlag.copyFromItemStack(stack);
			flag.setTypeTaking(HeartFlagOnlyCheckable.HeartTypeTaking.ENCHANT);
			flag.setTakingLevel(getWorldLevelOfXpCost(enchantCost));
			stack.set(IWComponents.HEART_FLAG, flag);
			stack.set(DataComponentTypes.STORED_ENCHANTMENTS, component);
			return true;
		}
		return false;
	}

	public boolean dumpPreEnchant(ItemStack stack, ItemEnchantmentsComponent component, int enchantCost)
	{
		if (supportEnchant())
		{
			HeartDataFlag flag = HeartDataFlag.copyFromItemStack(stack);
			if (flag.getTypeTaking() == HeartFlagOnlyCheckable.HeartTypeTaking.PREENCHANT)
			{
				flag.setTypeTaking(HeartFlagOnlyCheckable.HeartTypeTaking.ENCHANT);
				flag.setTakingLevel(getWorldLevelOfXpCost(enchantCost));
				stack.set(IWComponents.HEART_FLAG, flag);
				stack.set(DataComponentTypes.STORED_ENCHANTMENTS, component);
				stack.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
				return true;
			}
		}
		return false;
	}

	public void setPreEnchantFlag(ItemStack stack)
	{
		if (supportEnchant())
		{
			stack.set(IWComponents.HEART_FLAG, HeartDataFlag.copyFromItemStack(stack)
					.setTypeTaking(HeartFlagOnlyCheckable.HeartTypeTaking.PREENCHANT));
		}
	}

	public boolean setAbility(ItemStack stack, short abilityIndex)
	{
		if (supportAbility())
		{
			AbstractRuneAbility ab = getAbility(abilityIndex);
			if (ab != IWRuneAbilities.DEFAULT_ABILITY && getAbilitiesSupport().contains(abilityIndex))
			{
				HeartDataFlag flag = HeartDataFlag.copyFromItemStack(stack);
				flag.setTypeTaking(HeartFlagOnlyCheckable.HeartTypeTaking.ABILITY);
				flag.setTakingLevel(ab.level());
				stack.set(IWComponents.HEART_FLAG, flag);
				stack.set(IWComponents.ABILITY_INDEX, abilityIndex);
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
				HeartDataFlag flag = HeartDataFlag.copyFromItemStack(stack);
				flag.setTypeTaking(HeartFlagOnlyCheckable.HeartTypeTaking.ABILITY);
				flag.setTakingLevel(ability.level());
				stack.set(IWComponents.HEART_FLAG, flag);
				stack.set(IWComponents.ABILITY_INDEX, ability.index);
				return true;
			}
		}
		return false;
	}

	public void removeAbility(ItemStack stack)
	{
		HeartDataFlag flag = HeartDataFlag.copyFromItemStack(stack);
		if (flag.getTypeTaking() == HeartFlagOnlyCheckable.HeartTypeTaking.ABILITY)
		{
			flag.setTypeTaking(HeartFlagOnlyCheckable.HeartTypeTaking.NULL);
			stack.set(IWComponents.HEART_FLAG, flag);
			stack.remove(IWComponents.ABILITY_INDEX);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		HeartFlagOnlyCheckable flagOnlyCheckable = HeartDataFlag.getFromItemStack(stack);
		int lvl = flagOnlyCheckable.getMaterialLevel();
		tooltip.add(Text.translatable("tooltip.heart.containerLevel").withColor(Color.GRAY_RGB)
				.append(Text.literal(String.valueOf(lvl)).withColor(Color.getLevelColor(lvl))));
		switch (flagOnlyCheckable.getTypeTaking())
		{
			case NULL ->
			{
				if (supportEnchant())
				{
					tooltip.add(Text.translatable("tooltip.heart.canEnchant").withColor(Color.GRAY_RGB));
				}
				if (supportAbility())
				{
					tooltip.add(Text.translatable("tooltip.heart.canHaveAbility").withColor(Color.GRAY_RGB));
				}
			}
			case PREENCHANT -> tooltip.add(Text.translatable("tooltip.heart.furtherEnchant").withColor(Color.GRAY_RGB));
			case ENCHANT ->
			{
				int takeLvl = flagOnlyCheckable.getTakingLevel();
				IWPersistentData data = Server.getPersistentData();
				if (data != null && takeLvl <= data.worldEnergyLevel) tooltip.add(
						Text.translatable("tooltip.heart.enchantLevel").withColor(Color.GRAY_RGB)
								.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
				else tooltip.add(Text.translatable("tooltip.heart.enchantLevel").withColor(Color.RED_RGB)
						.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
			}
			case ABILITY ->
			{
				int takeLvl = flagOnlyCheckable.getTakingLevel();
				IWPersistentData data = Server.getPersistentData();
				if (data != null && takeLvl <= data.worldEnergyLevel) tooltip.add(
						Text.translatable("tooltip.heart.abilityLevel").withColor(Color.GRAY_RGB)
								.append(Text.literal(String.valueOf(takeLvl)).withColor(Color.getLevelColor(takeLvl))));
				else tooltip.add(Text.translatable("tooltip.heart.abilityLevel").withColor(Color.RED_RGB)
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
		stack.set(IWComponents.HEART_FLAG, HeartDataFlag.copyFromItemStack(stack).setMaterialLevel(materialLevel));
		return stack;
	}
}
