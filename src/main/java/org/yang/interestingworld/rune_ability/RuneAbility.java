package org.yang.interestingworld.rune_ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWSounds;
import org.yang.interestingworld.entity.player.IWClientPlayerData;
import org.yang.interestingworld.persistentdata.IWPersistentData;
import org.yang.interestingworld.util.Server;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.style.TextStyle;

import java.util.List;

public class RuneAbility extends AbstractRuneAbility
{
	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		tooltip.add(Text.empty());
		tooltip.add(Text.literal("【").append(getTitleText()).append("】").setStyle(TextStyle.BOLD_STYLE)
				.withColor(getColor()));
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
		tooltip.add(Text.translatable("banedabilitydetail").withColor(Color.GRAY_RGB));
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
