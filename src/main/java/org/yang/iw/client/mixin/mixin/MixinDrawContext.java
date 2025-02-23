package org.yang.iw.client.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.component.IWComponents;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.util.style.Color;

import java.util.List;

import static org.yang.iw.client.mixin.helper.HelperDrawContext.fixTooltip;

@Debug(export = true)
@Mixin(DrawContext.class)
public abstract class MixinDrawContext
{
	@Shadow
	public abstract int getScaledWindowWidth();


	@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;" +
							 "IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V"
			, at = @At(value = "HEAD"), argsOnly = true)
	public List<TooltipComponent> makeListMutableAndFix(List<TooltipComponent> components,
														@Local(argsOnly = true) TextRenderer textRenderer,
														@Local(argsOnly = true, ordinal = 0) int x)
	{
		return fixTooltip(components, textRenderer, x, getScaledWindowWidth());
	}


	@Shadow
	public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

	@Inject(method = "drawItemBar", at = @At(value = "HEAD"), cancellable = true)
	public void addSecondItemBar(ItemStack stack, int x, int y, CallbackInfo ci)
	{
		Item item = stack.getItem();
		if (item instanceof EnergyToolItem && stack.contains(IWComponents.MAX_ENERGY))
		{
			float maxEnergy = stack.getOrDefault(IWComponents.MAX_ENERGY, 1f);
			float currentEnergy = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 1f);
			if (maxEnergy <= currentEnergy) return;
			int cl = Math.clamp((int) ((currentEnergy * 13.0f) / maxEnergy), 0, 13);
			AbstractAbility ab = stack.interestingWorld$getAbility().ability();
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
			ci.cancel();
		}
	}
}
