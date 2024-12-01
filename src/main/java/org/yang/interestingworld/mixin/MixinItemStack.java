package org.yang.interestingworld.mixin;

import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.IWUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ComponentHolder
{

	@Shadow
	public abstract void applyAttributeModifier(AttributeModifierSlot slot, BiConsumer<RegistryEntry<EntityAttribute>,
			EntityAttributeModifier> attributeModifierConsumer);

	@Inject(method = "appendAttributeModifiersTooltip", at = @At(value = "HEAD"), cancellable = true)
	private void changeAttributeModifierShown(Consumer<Text> textConsumer, @Nullable PlayerEntity player,
											  CallbackInfo ci)
	{
		AttributeModifiersComponent attributeModifiersComponent = this.getOrDefault(
				DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		if (attributeModifiersComponent.showInTooltip())
		{
			IWUtil.Components.MutableAttributeContainer container = new IWUtil.Components.MutableAttributeContainer();
			for (AttributeModifierSlot attributeModifierSlot : AttributeModifierSlot.values())
			{
				MutableBoolean mutableBoolean = new MutableBoolean(true);
				container.clear();
				this.applyAttributeModifier(attributeModifierSlot, (attribute, modifier) -> {
					if (mutableBoolean.isTrue())
					{
						textConsumer.accept(ScreenTexts.EMPTY);
						textConsumer.accept(Text.translatable("item.modifiers." + attributeModifierSlot.asString())
								.formatted(Formatting.GRAY));
						mutableBoolean.setFalse();
					}
					if (!container.hideAttribute(attribute, modifier))
					{
						this.appendAttributeModifierTooltip2(textConsumer, player, attribute, modifier,
								attributeModifierSlot, container);
					}
				});
			}
		}
		ci.cancel();
	}

	@Unique
	private void appendAttributeModifierTooltip2(Consumer<Text> textConsumer, @Nullable PlayerEntity player,
												 RegistryEntry<EntityAttribute> attribute,
												 EntityAttributeModifier modifier, AttributeModifierSlot slot,
												 IWUtil.Components.MutableAttributeContainer container)
	{
		double d = modifier.value();
		boolean bl = false;
		if (player != null)
		{
			if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID))
			{
				container.findDamage();
				MutableDouble valueBase = new MutableDouble(
						player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + d);
				MutableDouble valueX1 = new MutableDouble(1.0);
				MutableDouble valueX2 = new MutableDouble(1.0);
				this.applyAttributeModifier(slot, (at, md) -> {
					if (IWUtil.Components.MutableAttributeContainer.attributeEntryEqual(at,
							EntityAttributes.GENERIC_ATTACK_DAMAGE) &&
						IWUtil.Components.MutableAttributeContainer.modifierAddByEnchantment(md))
					{
						switch (md.operation())
						{
							case EntityAttributeModifier.Operation.ADD_VALUE -> valueBase.add(md.value());
							case EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE -> valueX1.add(md.value());
							case EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL ->
									valueX2.setValue(valueX2.getValue() * (1.0 + md.value()));
						}
					}
				});
				d = valueBase.getValue() * valueX1.getValue() * valueX2.getValue();
				bl = true;
			}
			else if (modifier.idMatches(Item.BASE_ATTACK_SPEED_MODIFIER_ID))
			{
				container.findSpeed();
				MutableDouble valueBase = new MutableDouble(
						player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED) + d);
				MutableDouble valueX1 = new MutableDouble(1.0);
				MutableDouble valueX2 = new MutableDouble(1.0);
				this.applyAttributeModifier(slot, (at, md) -> {
					if (IWUtil.Components.MutableAttributeContainer.attributeEntryEqual(at,
							EntityAttributes.GENERIC_ATTACK_SPEED) &&
						IWUtil.Components.MutableAttributeContainer.modifierAddByEnchantment(md))
					{
						switch (md.operation())
						{
							case EntityAttributeModifier.Operation.ADD_VALUE -> valueBase.add(md.value());
							case EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE -> valueX1.add(md.value());
							case EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL ->
									valueX2.setValue(valueX2.getValue() * (1.0 + md.value()));
						}
					}
				});
				d = valueBase.getValue() * valueX1.getValue() * valueX2.getValue();
				bl = true;
			}
		}

		double e;
		if (modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE ||
			modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
		{
			e = d * 100.0;
		}
		else if (IWUtil.Components.MutableAttributeContainer.attributeEntryEqual(attribute,
				EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
		{
			e = d * 10.0;
		}
		else
		{
			e = d;
		}

		if (bl)
		{
			textConsumer.accept(ScreenTexts.space()
					.append(Text.translatable("attribute.modifier.equals." + modifier.operation().getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
							Text.translatable(attribute.value().getTranslationKey())))
					.formatted(Formatting.DARK_GREEN));
		}
		else if (d > 0.0)
		{
			textConsumer.accept(Text.translatable("attribute.modifier.plus." + modifier.operation().getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
							Text.translatable(attribute.value().getTranslationKey()))
					.formatted(attribute.value().getFormatting(true)));
		}
		else if (d < 0.0)
		{
			textConsumer.accept(Text.translatable("attribute.modifier.take." + modifier.operation().getId(),
							AttributeModifiersComponent.DECIMAL_FORMAT.format(-e),
							Text.translatable(attribute.value().getTranslationKey()))
					.formatted(attribute.value().getFormatting(false)));
		}
	}
}
