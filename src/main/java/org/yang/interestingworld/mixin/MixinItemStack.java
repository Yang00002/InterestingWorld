package org.yang.interestingworld.mixin;

import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.mixin_helper.MixinItemStackHelper;

import java.util.LinkedHashMap;
import java.util.Map;
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
			Map<RegistryEntry<EntityAttribute>, MixinItemStackHelper.ModifierSummerizer> attributeMap =
					new LinkedHashMap<>();
			for (AttributeModifierSlot attributeModifierSlot : AttributeModifierSlot.values())
			{
				MutableBoolean mutableBoolean = new MutableBoolean(true);
				attributeMap.clear();
				this.applyAttributeModifier(attributeModifierSlot, (attribute, modifier) -> {
					if (mutableBoolean.isTrue())
					{
						textConsumer.accept(ScreenTexts.EMPTY);
						textConsumer.accept(Text.translatable("item.modifiers." + attributeModifierSlot.asString())
								.formatted(Formatting.GRAY));
						mutableBoolean.setFalse();
					}
					MixinItemStackHelper.ModifierSummerizer modifierSum;
					if (attributeMap.containsKey(attribute)) modifierSum = attributeMap.get(attribute);
					else
					{
						modifierSum = new MixinItemStackHelper.ModifierSummerizer();
						attributeMap.put(attribute, modifierSum);
					}
					switch (modifier.operation())
					{
						case ADD_VALUE -> modifierSum.base += modifier.value();
						case ADD_MULTIPLIED_BASE -> modifierSum.mul1 += modifier.value();
						case ADD_MULTIPLIED_TOTAL -> modifierSum.mul2 *= modifier.value();
					}
				});
				MixinItemStackHelper.appendAttributeModifierToolTip(textConsumer, player, attributeMap,
						attributeModifierSlot);
			}
		}
		ci.cancel();
	}
}
