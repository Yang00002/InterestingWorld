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

public class CherryHeart extends CommonHeart
{
	public CherryHeart(Settings settings)
	{
		super(settings, i -> 22 + 2 * i);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.PINK_RGB;
	}


	private static final List<Short> abilitiesSupport = new ArrayList<>();

	static
	{
		abilitiesSupport.add(IWRuneAbilities.SWEETCURSE_ABILITY.index);
		abilitiesSupport.add(IWRuneAbilities.INFINITECURSE_ABILITY.index);
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
		IWAttributeModifierUtil.addModifier(builder, EntityAttributes.LUCK, AttributeModifierIds.LUCK_ADD_OFF_HAND,
				lvl * 0.25 + 0.5, EntityAttributeModifier.Operation.ADD_VALUE, AttributeModifierSlot.OFFHAND);
		IWAttributeModifierUtil.applyAttributeModifierToItemStack(builder, stack);
		return stack;
	}
}
