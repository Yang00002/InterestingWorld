package org.yang.iw.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.yang.iw.IWMain;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.component.*;
import org.yang.iw.item.heart.HideAbilityTooltip;
import org.yang.iw.item.upgrade.HideUpgradeTooltip;
import org.yang.iw.mixin.helper.HelperItemStack;
import org.yang.iw.mixin.mixin_interface.InterfaceItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Debug(export = true)
@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ComponentHolder, InterfaceItemStack
{
	@Override
	@Unique
	public @Nullable BoostFunctionMap interestingWorld$uniqueBoost()
	{
		return getItem().interestingWorld$uniqueBoostFor((ItemStack) (Object) this);
	}

	@Unique
	public BoostComponent interestingWorld$getBoosts()
	{
		return getOrDefault(IWComponents.BOOST, BoostComponent.DEFAULT);
	}

	@Unique
	public AbilityComponent interestingWorld$getAbility()
	{
		return getOrDefault(IWComponents.ABILITY, AbilityComponent.DEFAULT);
	}

	@Unique
	public UpgradeComponent interestingWorld$getUpgrade()
	{
		return getOrDefault(IWComponents.UPGRADE, UpgradeComponent.DEFAULT);
	}

	@Shadow
	public abstract void applyAttributeModifier(AttributeModifierSlot slot, BiConsumer<RegistryEntry<EntityAttribute>,
			EntityAttributeModifier> attributeModifierConsumer);

	@Shadow
	public abstract Item getItem();


	@Inject(method = "appendAttributeModifiersTooltip", at = @At(value = "HEAD"), cancellable = true)
	private void mixinAppendAttributeModifiersTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player,
													  CallbackInfo ci)
	{
		AttributeModifiersComponent attributeModifiersComponent = this.getOrDefault(
				DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		if (attributeModifiersComponent.showInTooltip())
		{
			Map<RegistryEntry<EntityAttribute>, HelperItemStack.ModifierSummarizer> attributeMap =
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
					HelperItemStack.ModifierSummarizer modifierSum;
					if (attributeMap.containsKey(attribute)) modifierSum = attributeMap.get(attribute);
					else
					{
						modifierSum = new HelperItemStack.ModifierSummarizer();
						attributeMap.put(attribute, modifierSum);
					}
					switch (modifier.operation())
					{
						case ADD_VALUE -> modifierSum.base += modifier.value();
						case ADD_MULTIPLIED_BASE -> modifierSum.mul1 += modifier.value();
						case ADD_MULTIPLIED_TOTAL -> modifierSum.mul2 *= (1 + modifier.value());
					}
				});
				org.yang.iw.mixin.helper.HelperItemStack.appendAttributeModifierToolTip(textConsumer, player,
						attributeMap, attributeModifierSlot);
			}
		}
		ci.cancel();
	}

	@Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip" +
																	   "(Lnet/minecraft/component/ComponentType;" +
																	   "Lnet/minecraft/item/Item$TooltipContext;" +
																	   "Ljava/util/function/Consumer;" +
																	   "Lnet/minecraft/item/tooltip/TooltipType;)V",
			ordinal = 2), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void mixinAppendBoostTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type,

										 CallbackInfoReturnable<List<Text>> cir, @Local List<Text> list)
	{
		int worldLevel = IWMain.getPersistentData().worldEnergyLevel;
		var cls = getItem().getClass();
		boolean keyPressed = false;
		boolean nset = true;
		UpgradeComponent upgradeComponent = getOrDefault(IWComponents.UPGRADE, UpgradeComponent.DEFAULT);
		if (!upgradeComponent.isEmpty() && !HideUpgradeTooltip.class.isAssignableFrom(cls))
		{
			keyPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
					GLFW.GLFW_KEY_LEFT_SHIFT);
			nset = false;
			if (keyPressed) upgradeComponent.appendDetailedTooltip(context, list, type);
			else upgradeComponent.appendTooltip(context, list, type);
		}
		AbilityComponent abilityComponent = getOrDefault(IWComponents.ABILITY, AbilityComponent.DEFAULT);
		if (!abilityComponent.isEmpty() && !HideAbilityTooltip.class.isAssignableFrom(cls))
		{
			if (nset)
			{
				keyPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
						GLFW.GLFW_KEY_LEFT_SHIFT);
				nset = false;
			}
			if (keyPressed) abilityComponent.appendDetailedTooltip(context, list, type, worldLevel);
			else abilityComponent.appendTooltip(context, list, type, worldLevel);
		}
		if (!nset) list.add(Text.empty());
		getOrDefault(IWComponents.BOOST, BoostComponent.DEFAULT).appendTooltip(context, list::add, type);
	}

	@Inject(method = "isEnchantable", at = @At(value = "RETURN"), cancellable = true)
	private void setBaseHeartNotEnchantable(CallbackInfoReturnable<Boolean> cir)
	{
		var r = cir.getReturnValue();
		if (r)
		{
			if (!interestingWorld$getBoosts().isEmpty())
			{
				cir.setReturnValue(false);
				return;
			}
			var boostable = getOrDefault(IWComponents.BOOSTABLE, BoostableComponent.DEFAULT);
			if (!boostable.isEmpty() && boostable.remainBoostTime() <= 0) cir.setReturnValue(false);
		}
	}
}
