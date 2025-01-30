package org.yang.iw.item.heart.common;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.util.IWAttributeModifierUtil;
import org.yang.iw.util.constants.AttributeModifierIds;
import org.yang.iw.util.style.Color;

import java.util.ArrayList;
import java.util.List;

public class BloodHeart extends CommonHeart
{
	public BloodHeart(Settings settings)
	{
		super(settings);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.RED_RGB;
	}

	@Override
	public int getEnchantability()
	{
		return 10;
	}

	private static final List<Short> abilitiesSupport = new ArrayList<>();

	static
	{
		abilitiesSupport.add(IWRuneAbilities.EVISCERATE_ABILITY.index);
		abilitiesSupport.add(IWRuneAbilities.INFINITEEVISCERATE_ABILITY.index);
	}

	@Override
	public List<Short> getAbilitiesSupport()
	{
		return abilitiesSupport;
	}

	@Override
	public ItemStack boostStackByLevel(int lvl, ItemStack stack)
	{
		AttributeModifiersComponent.Builder builder = IWAttributeModifierUtil.getModifiersFromItemStack(stack);
		IWAttributeModifierUtil.addModifier(builder, EntityAttributes.GENERIC_ATTACK_DAMAGE,
				AttributeModifierIds.ATTACK_DAMAGE_MULADD_OFF_HAND, lvl * 0.04 + 0.02,
				EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE, AttributeModifierSlot.OFFHAND);
		IWAttributeModifierUtil.applyAttributeModifierToItemStack(builder, stack);
		return stack;
	}
}
