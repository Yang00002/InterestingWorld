package org.yang.iw.rune_ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.iw.IWSounds;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.persistentdata.IWPersistentData;
import org.yang.iw.util.Server;
import org.yang.iw.util.style.Color;
import org.yang.iw.util.style.TextStyle;

import java.util.List;

public class RuneAbility extends AbstractRuneAbility
{
	private final MutableText tip = Text.translatable("ability.tip." + id()).withColor(getColor());
	private final MutableText title = Text.translatable("ability.title." + id());

	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(TextStyle.BOLD_STYLE)
				.withColor(getColor()));
		tooltip.add(tip);
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return true;
	}

	@Override
	public void appendBanedToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(TextStyle.BOLD_STYLE)
				.withColor(Color.GRAY_RGB));
		tooltip.add(Text.translatable(TranslationPool.TOOLTIP_BAN_ABILITY_BEGIN).withColor(Color.GRAY_RGB));
	}

	@Override
	public MutableText getTitleText()
	{
		return title;
	}

	@Override
	public boolean canWork()
	{
		IWPersistentData data = Server.getPersistentData();
		return data != null && level() <= data.worldEnergyLevel;
	}

	@Override
	public boolean shouldRenderAbilityBar(IWClientPlayerData data)
	{
		return data.client_ability_on || data.charge_rate16 < 16;
	}

	public void playChargedOverSound(PlayerEntity entity)
	{
		entity.playSound(IWSounds.ABILITYBAR_FULL, 1.0f, 1.0f);
	}
}
