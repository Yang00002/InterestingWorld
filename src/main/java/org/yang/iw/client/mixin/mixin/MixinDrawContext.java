package org.yang.iw.client.mixin.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.IWComponents;
import org.yang.iw.client.tooltip.TooltipHelper;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.util.IWRuneAbilityUtil;
import org.yang.iw.util.style.Color;

import java.util.ArrayList;
import java.util.List;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext
{
	@Shadow
	public abstract int getScaledWindowWidth();

	@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;" + "IILnet" +
							 "/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "HEAD"), index = 2
			, argsOnly = true)
	public List<TooltipComponent> makeListMutable(List<TooltipComponent> value)
	{
		return new ArrayList<>(value);
	}

	@Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;" + "IILnet/minecraft" +
					 "/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "HEAD"))
	public void fix(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y,
					TooltipPositioner positioner, CallbackInfo ci)
	{
		TooltipHelper.newFix(components, textRenderer, x, getScaledWindowWidth());
	}

	@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;" + "IILnet" +
							 "/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "INVOKE", target =
			"Lnet" + "/minecraft/client/util/math/MatrixStack;push()V"), index = 11)
	public int modifyRenderX(int value, TextRenderer textRenderer, List<TooltipComponent> components, int x)
	{
		return TooltipHelper.shouldFlip(components, textRenderer, x);
	}

	@Shadow
	@Final
	private MatrixStack matrices;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

	@Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;" +
					 "IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math" +
																				   "/MatrixStack;push()V", shift =
			At.Shift.AFTER), cancellable = true)
	public void addSecondItemBar(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride,
								 CallbackInfo ci)
	{
		Item item = stack.getItem();
		if (item instanceof EnergyToolItem && stack.contains(IWComponents.MAX_ENERGY))
		{
			float maxEnergy = stack.getOrDefault(IWComponents.MAX_ENERGY, 1f);
			float currentEnergy = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 1f);
			if (maxEnergy <= currentEnergy) return;
			int cl = Math.clamp((int) ((currentEnergy * 13.0f) / maxEnergy), 0, 13);
			AbstractRuneAbility ab = IWRuneAbilityUtil.getAbility(stack);
			int l = y + 13;
			int k = x + 2;
			int c = ab.canWork() ? ab.getColor() : Color.GRAY_RGB;
			if (stack.isItemBarVisible())
			{
				int i = stack.getItemBarStep();
				int j = stack.getItemBarColor();
				fill(RenderLayer.getGuiOverlay(), k, l - 1, k + 13, l + 1, -16777216);
				fill(RenderLayer.getGuiOverlay(), k, l - 1, k + cl, l, c | -16777216);
				fill(RenderLayer.getGuiOverlay(), k, l + 1, k + 13, l + 3, -16777216);
				fill(RenderLayer.getGuiOverlay(), k, l + 1, k + i, l + 2, j | -16777216);
			}
			else
			{
				fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
				fill(RenderLayer.getGuiOverlay(), k, l, k + cl, l + 1, c | -16777216);
			}
			ClientPlayerEntity clientPlayerEntity = client.player;
			float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager()
					.getCooldownProgress(stack.getItem(), this.client.getRenderTickCounter().getTickDelta(true));
			if (f > 0.0F)
			{
				k = y + MathHelper.floor(16.0F * (1.0F - f));
				l = k + MathHelper.ceil(16.0F * f);
				fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
			}
			matrices.pop();
			ci.cancel();
		}
	}
}
