package org.yang.iw.client.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.mixin.mixin_interface.InterfaceClientPlayerEntity;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.util.Base;

import static org.yang.iw.util.style.Color.rgbToArgb;
import static org.yang.iw.util.style.Color.rgbToDarkenArgb;

@Debug(export = true)
@Mixin(InGameHud.class)
public abstract class MixinInGameHud
{
	@Unique
	private static final Identifier EMPTY_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_empty");
	@Unique
	private static final Identifier FULL_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_full");
	@Unique
	private static final Identifier HALF_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_half");
	@Unique
	private static final Identifier FFULL_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_glowing");

	@Shadow
	public abstract TextRenderer getTextRenderer();

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler" +
																			 "/Profilers;" +
																			 "get()Lnet/minecraft/util/profiler" +
																			 "/Profiler;", ordinal = 2))
	public void renderEnergy(DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity, @Local(ordinal =
			3) int m, @Local(ordinal = 8) int r)

	{
		Profilers.get().swap("energy");
		IWClientPlayerData manager = ((InterfaceClientPlayerEntity) playerEntity).getIWClientPlayerData();
		int energy = manager.shown_energy;
		RenderSystem.enableBlend();
		if (energy >= 20)
		{
			for (int j = 0; j < 10; j++)
			{
				int l = m - j * 8 - 9;
				context.drawGuiTexture(RenderLayer::getGuiTextured, FFULL_ENERGY, l, r, 9, 9);
			}
		}
		else
		{
			for (int j = 0; j < 10; j++)
			{
				int t = j * 2 + 1;
				int l = m - j * 8 - 9;
				if (t > energy) context.drawGuiTexture(RenderLayer::getGuiTextured, EMPTY_ENERGY, l, r, 9, 9);
				else if (t == energy) context.drawGuiTexture(RenderLayer::getGuiTextured, HALF_ENERGY, l, r, 9, 9);
				else context.drawGuiTexture(RenderLayer::getGuiTextured, FULL_ENERGY, l, r, 9, 9);
			}
		}
		RenderSystem.disableBlend();
		AbstractRuneAbility ability = manager.WeaponAbility;
		if (ability.shouldRenderAbilityBar(manager))
		{
			int j = (context.getScaledWindowWidth() - 18) / 2;
			int k = context.getScaledWindowHeight() - 50;
			int div = ability.abilityProcess(manager);
			int fc = ability.abilityBarForegroundColor(manager);
			if (div == 16) context.fill(j, k, j + 17, k + 5, rgbToDarkenArgb(fc, 0.2f));
			else
			{
				context.fill(j, k, j + 17, k + 5, rgbToArgb(0));
				context.fill(j + 1 + div, k + 1, j + 16, k + 4, rgbToDarkenArgb(fc, 0.2f));
			}
			if (div > 0) context.fill(j + 1, k + 1, j + div, k + 4, rgbToArgb(fc));
		}
		if (ability.shouldRenderAbilityNumber(manager))
		{
			String number = ability.abilityNumber(manager) + "";
			int j = (context.getScaledWindowWidth() - this.getTextRenderer().getWidth(number)) / 2;
			int k = context.getScaledWindowHeight() - 48;
			RenderSystem.enableBlend();
			context.drawText(getTextRenderer(), number, j - 1, k, 0, false);
			RenderSystem.disableBlend();
		}
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud" +
																				"/InGameHud;renderAirBubbles" +
																				"(Lnet/minecraft/client/gui" +
																				"/DrawContext;Lnet/minecraft/entity" +
																				"/player/PlayerEntity;III)V"), index
			= 3)
	private int changeAirPlace(int x)
	{
		return x - 10;
	}
}
