package org.yang.iw.mixin.helper;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class HelperItemStack
{
	public static class ModifierSummerizer
	{
		public double base = 0;
		public double mul1 = 0;
		public double mul2 = 1.0;
	}

	public static void appendBasicToolTip(Consumer<Text> textConsumer, @NotNull PlayerEntity player,
										  ModifierSummerizer modifier, RegistryEntry<EntityAttribute> attribute)
	{
		double base = player.getAttributeBaseValue(attribute) + modifier.base;
		modifier.base = 0.0;
		textConsumer.accept(ScreenTexts.space().append(Text.translatable(
				"attribute.modifier.equals." + EntityAttributeModifier.Operation.ADD_VALUE.getId(),
				AttributeModifiersComponent.DECIMAL_FORMAT.format(base),
				Text.translatable(attribute.value().getTranslationKey()))).formatted(Formatting.DARK_GREEN));
	}

	public static void appendCommonToolTip(Consumer<Text> textConsumer, ModifierSummerizer modifierValue,
										   RegistryEntry<EntityAttribute> attribute)
	{
		if (modifierValue.base > 0.0)
		{
			textConsumer.accept(
					Text.translatable("attribute.modifier.iwp." + EntityAttributeModifier.Operation.ADD_VALUE.getId(),
									AttributeModifiersComponent.DECIMAL_FORMAT.format(modifierValue.base),
									Text.translatable(attribute.value().getTranslationKey()))
							.formatted(attribute.value().getFormatting(true)));
		}
		else if (modifierValue.base < 0.0)
		{
			textConsumer.accept(
					Text.translatable("attribute.modifier.iwt." + EntityAttributeModifier.Operation.ADD_VALUE.getId(),
									AttributeModifiersComponent.DECIMAL_FORMAT.format(-modifierValue.base),
									Text.translatable(attribute.value().getTranslationKey()))
							.formatted(attribute.value().getFormatting(false)));
		}
		if (modifierValue.mul1 > 0.0)
		{
			textConsumer.accept(Text.translatable(
							"attribute.modifier.iwp." + EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE.getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(modifierValue.mul1 * 100),
							Text.translatable(attribute.value().getTranslationKey()))
					.formatted(attribute.value().getFormatting(true)));
		}
		else if (modifierValue.mul1 < 0.0)
		{
			textConsumer.accept(Text.translatable(
							"attribute.modifier.iwt." + EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE.getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(-modifierValue.mul1 * 100),
							Text.translatable(attribute.value().getTranslationKey()))
					.formatted(attribute.value().getFormatting(false)));
		}
		if (modifierValue.mul2 > 1.0)
		{
			textConsumer.accept(Text.translatable(
							"attribute.modifier.iwp." + EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(modifierValue.mul2),
							Text.translatable(attribute.value().getTranslationKey()))
					.formatted(attribute.value().getFormatting(true)));
		}
		else if (modifierValue.mul2 < 1.0)
		{
			textConsumer.accept(Text.translatable(
							"attribute.modifier.iwt." + EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL.getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(-modifierValue.mul2),
							Text.translatable(attribute.value().getTranslationKey()))
					.formatted(attribute.value().getFormatting(false)));
		}
	}


	public static void appendAttributeModifierToolTip(Consumer<Text> textConsumer, @Nullable PlayerEntity player,
													  Map<RegistryEntry<EntityAttribute>, ModifierSummerizer> attributeMap, AttributeModifierSlot slot)
	{
		if (player != null && (slot == AttributeModifierSlot.HAND || slot == AttributeModifierSlot.MAINHAND))
		{
			if (attributeMap.containsKey(EntityAttributes.ATTACK_DAMAGE))
				appendBasicToolTip(textConsumer, player, attributeMap.get(EntityAttributes.ATTACK_DAMAGE),
						EntityAttributes.ATTACK_DAMAGE);
			if (attributeMap.containsKey(EntityAttributes.ATTACK_SPEED))
				appendBasicToolTip(textConsumer, player, attributeMap.get(EntityAttributes.ATTACK_SPEED),
						EntityAttributes.ATTACK_SPEED);
		}
		for (var entry : attributeMap.entrySet())
		{
			var attribute = entry.getKey();
			var modifier = entry.getValue();
			appendCommonToolTip(textConsumer, modifier, attribute);
		}
	}
}
