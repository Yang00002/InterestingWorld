package org.yang.interestingworld.rune.ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.yang.interestingworld.IWEffects;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.playerdatamanager.ClientPlayerDataManager;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataManager;
import org.yang.interestingworld.rune.IWRuneAbility;

import java.util.List;

import static org.yang.interestingworld.IWUtil.EnergyTool.getPlayerData;
import static org.yang.interestingworld.IWUtil.EnergyTool.influenceEntityAround;
import static org.yang.interestingworld.IWUtil.EntityAbout.addHiddenStatusEffect;
import static org.yang.interestingworld.IWUtil.EntityAbout.addStatusEffect;
import static org.yang.interestingworld.IWUtil.Return.*;
import static org.yang.interestingworld.IWUtil.TextStyle.PINK_RGB;

public class SweetCurseAbility extends IWRuneAbility
{
	public static final short AbilityDuration = 60;
	public static final int RengenAmplifier = 0;
	public static final int EffectDuration = 160;
	public static final int HurtingAmplifier = 4;
	public static final double AttackMaxLength = 3;
	public static final float EnergyCosume = 10;

	public int getColor()
	{
		return PINK_RGB;
	}

	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("sweetcurse_ability_detail").withColor(getColor()));
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("sweetcurse_ability_title");
	}

	@Override
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (user instanceof ServerPlayerEntity player)
		{
			var data = getPlayerData(player);
			if (data.chargeRate < AbilityDuration || !data.extractAutomicEnergy(stack, player, EnergyCosume))
				return FAIL;
			data.chargeRate = 0;
			data.shouldSync = true;
			StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.REGENERATION, EffectDuration);
			IWUtil.Network.playSoundToPlayer(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS);
			influenceEntityAround(user, AttackMaxLength, (attacker, entity, distance) -> {
				if (entity.canHaveStatusEffect(instance))
				{
					addStatusEffect(entity, StatusEffects.REGENERATION, EffectDuration, RengenAmplifier);
					addHiddenStatusEffect(entity, IWEffects.HURTING, EffectDuration, HurtingAmplifier);
				}
			});
			return SUCCESS;
		}
		return PASS;
	}

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, ServerPlayerDataManager data, ItemStack stack)
	{
		if (data.chargeRate < AbilityDuration)
		{
			data.chargeRate++;
			data.shouldSync = true;
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack, UseAction before)
	{
		return UseAction.BOW;
	}

	@Override
	public void onEnter(PlayerEntity entity, ServerPlayerDataManager manager)
	{
		manager.chargeRate = 0;
	}

	@Override
	public void onLeave(PlayerEntity entity, ServerPlayerDataManager manager)
	{
		manager.chargeRate = -1;
	}

	@Override
	public int abilityBarForegroundColor(ClientPlayerDataManager data)
	{
		return PINK_RGB;
	}

	@Override
	public int abilityProcess(ClientPlayerDataManager data)
	{
		return data.charge_rate16;
	}

	@Override
	public void writeClientRenderDataToBuf(ServerPlayerDataManager data, RegistryByteBuf buf)
	{
		buf.writeInt(data.chargeRate);
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, ClientPlayerDataManager data, ByteBuf buf)
	{
		int chargeRate = buf.readInt();
		int c = Math.clamp(chargeRate * 16L / AbilityDuration, 0, 16);
		if (c == 16 && data.charge_rate16 != 16)
		{
			playChargedOverSound(entity);
		}
		data.charge_rate16 = c;
	}
}
