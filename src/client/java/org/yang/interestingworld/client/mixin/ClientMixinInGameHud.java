package org.yang.interestingworld.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.playerenergymanager.PlayerEnergyAccessor;

@Mixin(InGameHud.class)
public class ClientMixinInGameHud
{
	@Unique
	private static final Identifier EMPTY_ENERGY = Identifier.of(IWUtil.Base.MOD_ID, "hud/energy/e_empty");
	@Unique
	private static final Identifier FULL_ENERGY = Identifier.of(IWUtil.Base.MOD_ID, "hud/energy/e_full");
	@Unique
	private static final Identifier HALF_ENERGY = Identifier.of(IWUtil.Base.MOD_ID, "hud/energy/e_half");
	@Unique
	private static final Identifier FFULL_ENERGY = Identifier.of(IWUtil.Base.MOD_ID, "hud/energy/e_glowing");
	@Unique
	private static final Identifier FHALF_ENERGY = Identifier.of(IWUtil.Base.MOD_ID, "hud/energy/e_fhalf");
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;" +
																			 "getProfiler()" +
																			 "Lnet/minecraft/util/profiler/Profiler;",
			ordinal = 2))
	public void renderEnergy(DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity, @Local(ordinal =
			3) int m, @Local(ordinal = 8) int r)

	{
		// m -> right
		client.getProfiler().swap("energy");
		int energy = ((PlayerEnergyAccessor) playerEntity).getEnergyManager().getShownEnergy();
		RenderSystem.enableBlend();
		if (energy > 20)
		{
			energy -= 20;
			for (int j = 0; j < 10; j++)
			{
				int t = j * 2 + 1;
				int l = m - j * 8 - 9;
				if (t > energy) context.drawGuiTexture(FULL_ENERGY, l, r, 9, 9);
				else if (t == energy) context.drawGuiTexture(FHALF_ENERGY, l, r, 9, 9);
				else context.drawGuiTexture(FFULL_ENERGY, l, r, 9, 9);
			}
		}
		else
		{
			for (int j = 0; j < 10; j++)
			{
				int t = j * 2 + 1;
				int l = m - j * 8 - 9;
				if (t > energy) context.drawGuiTexture(EMPTY_ENERGY, l, r, 9, 9);
				else if (t == energy) context.drawGuiTexture(HALF_ENERGY, l, r, 9, 9);
				else context.drawGuiTexture(FULL_ENERGY, l, r, 9, 9);
			}
		}
		RenderSystem.disableBlend();
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui" +
																				"/DrawContext;drawGuiTexture" +
																				"(Lnet/minecraft/util/Identifier;" +
																				"IIII)" + "V"), index = 2)
	private int changeAirPlace(int x)
	{
		return x - 10;
	}
}
