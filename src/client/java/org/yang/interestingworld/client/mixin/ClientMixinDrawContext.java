package org.yang.interestingworld.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
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
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.client.tooltip.TooltipHelper;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;

import java.util.ArrayList;
import java.util.List;

@Mixin(DrawContext.class)
public abstract class ClientMixinDrawContext
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

	/*
	@Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;" +
					 "IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math" +
																				   "/MatrixStack;push()V", shift =
			At.Shift.AFTER), cancellable = true)
	public void addSecondItemBar(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride,
								 CallbackInfo ci)
	{
		Item item = stack.getItem();
		if (item instanceof EnergyToolItem)
		{
			float maxEnergy = stack.getOrDefault(IWComponents.MAX_ENERGY, 1f);
			float currentEnergy = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 1f);
			IWAbstractRuneAbility ab = IWUtil.RuneAbility.getAbility(stack);
			boolean canwork = ab.canWork();
			int l = y + 13;
			int k = x + 2;
			IWUtil.Return.AbilityItemBarMessageTaker message = null;
			if (canwork) message = ab.getAbilityItemBarRenderMessage(stack);
			if ((!canwork || ab.canEnergyItemBarVisible()) && maxEnergy > currentEnergy)
			{
				float frac = currentEnergy / maxEnergy;
				int step;
				int color;
				if (currentEnergy < 0)
				{
					step = 0;
					color = 0;
				}
				else
				{
					step = MathHelper.clamp(Math.round(13.0f * frac), 0, 13);
					if (frac > 1.0f) frac = 1.0f;
					color = MathHelper.hsvToRgb(frac / 3.0F, 1.0F, 1.0F);
				}
				if (message == null)
				{
					fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
					fill(RenderLayer.getGuiOverlay(), k, l, k + step, l + 1, color | -16777216);
				}
				else
				{
					fill(RenderLayer.getGuiOverlay(), k, l - 1, k + 13, l + 1, message.baseColor | -16777216);
					fill(RenderLayer.getGuiOverlay(), k, l - 1, k + message.step, l, message.contentColor | -16777216);
					fill(RenderLayer.getGuiOverlay(), k, l + 1, k + 13, l + 3, -16777216);
					fill(RenderLayer.getGuiOverlay(), k, l + 1, k + step, l + 2, color | -16777216);
				}
			}
			else if (message != null)
			{
				fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, message.baseColor | -16777216);
				fill(RenderLayer.getGuiOverlay(), k, l, k + message.step, l + 1, message.contentColor | -16777216);
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
	}*/
}
