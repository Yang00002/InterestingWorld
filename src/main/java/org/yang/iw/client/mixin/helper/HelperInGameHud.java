package org.yang.iw.client.mixin.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.mixin.mixin_interface.InterfaceClientPlayerEntity;
import org.yang.iw.util.Base;
import org.yang.iw.util.style.Color;

import static org.yang.iw.util.style.Color.rgbToArgb;
import static org.yang.iw.util.style.Color.rgbToDarkenArgb;

public class HelperInGameHud
{
	private static final Identifier EMPTY_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_empty");

	private static final Identifier FULL_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_full");

	private static final Identifier HALF_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_half");

	private static final Identifier FFULL_ENERGY = Identifier.of(Base.MOD_ID, "hud/energy/e_glowing");

	private static void renderEnergy(DrawContext context, IWClientPlayerData manager, int x, int y)
	{
		Profilers.get().swap("iw_custom");
		int energy = manager.getEnergy();
		RenderSystem.enableBlend();
		if (energy >= 20)
		{
			for (int j = 0; j < 10; j++)
			{
				int l = x - j * 8 - 9;
				context.drawGuiTexture(RenderLayer::getGuiTextured, FFULL_ENERGY, l, y, 9, 9);
			}
		}
		else
		{
			for (int j = 0; j < 10; j++)
			{
				int t = j * 2 + 1;
				int l = x - j * 8 - 9;
				if (t > energy) context.drawGuiTexture(RenderLayer::getGuiTextured, EMPTY_ENERGY, l, y, 9, 9);
				else if (t == energy) context.drawGuiTexture(RenderLayer::getGuiTextured, HALF_ENERGY, l, y, 9, 9);
				else context.drawGuiTexture(RenderLayer::getGuiTextured, FULL_ENERGY, l, y, 9, 9);
			}
		}
		RenderSystem.disableBlend();
	}

	private static void drawText(String text, DrawContext context, TextRenderer renderer, int color)
	{
		int j = (context.getScaledWindowWidth() - renderer.getWidth(text)) / 2;
		int k = context.getScaledWindowHeight() - 47;
		RenderSystem.enableBlend();
		context.drawText(renderer, text, j + 1, k, 0, false);
		context.drawText(renderer, text, j - 1, k, 0, false);
		context.drawText(renderer, text, j, k + 1, 0, false);
		context.drawText(renderer, text, j, k - 1, 0, false);
		context.drawText(renderer, text, j, k, color, false);
		RenderSystem.disableBlend();
	}

	private static void drawBar(int step, DrawContext context, int color, boolean fullChange)
	{
		int j = (context.getScaledWindowWidth() - 18) / 2;
		int k = context.getScaledWindowHeight() - 50;
		// fill 左闭右开 a , a + 1 即渲染坐标 a 1 个
		if (step == 16) context.fill(j, k, j + 18, k + 6, fullChange ? rgbToDarkenArgb(color, 0.2f) : 0XFF000000);
		else
		{
			context.fill(j, k, j + 18, k + 6, 0XFF000000);
			context.fill(j + 1 + step, k + 1, j + 17, k + 5, rgbToDarkenArgb(color, 0.2f));
		}
		if (step > 0) context.fill(j + 1, k + 1, j + step + 1, k + 5, rgbToArgb(color));
	}

	private static void drawBarNF(int step, DrawContext context, int color)
	{
		int j = (context.getScaledWindowWidth() - 18) / 2;
		int k = context.getScaledWindowHeight() - 50;
		context.fill(j, k, j + 18, k + 6, 0XFF000000);
		context.fill(j + 1 + step, k + 1, j + 17, k + 5, rgbToDarkenArgb(color, 0.2f));
		if (step > 0) context.fill(j + 1, k + 1, j + step + 1, k + 5, rgbToArgb(color));
	}

	public static void renderCustom(DrawContext context, TextRenderer renderer, PlayerEntity playerEntity, int x,
									int y)
	{
		IWClientPlayerData manager = ((InterfaceClientPlayerEntity) playerEntity).getIWClientPlayerData();
		renderEnergy(context, manager, x, y);
		AbstractAbility ability = manager.WeaponAbility;
		if (!ability.isEmpty())
		{
			if (manager.isCooldown())
			{
				int color = manager.isAbilityOn() ? ability.abilityBarForegroundColor(manager) : Color.GRAY_RGB;
				drawBarNF(manager.cooldownProgress(), context, color);
				drawText("cd", context, renderer, color);
			}
			else if (manager.isAbilityOn())
			{
				int step;
				switch (manager.abilityBarType())
				{
					case TICK, TICK_REVERSE -> step = manager.tickStep();
					default -> step = ability.abilityProcess(manager);
				}
				drawBar(step, context, ability.abilityBarForegroundColor(manager), true);
				var text = ability.abilityText(manager);
				if (text != null) drawText(ability.abilityText(manager), context, renderer,
						ability.abilityTextColor());
			}
		}
	}
}
