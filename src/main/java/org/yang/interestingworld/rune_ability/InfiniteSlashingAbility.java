package org.yang.interestingworld.rune_ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWSounds;
import org.yang.interestingworld.effect.IWEffects;
import org.yang.interestingworld.entity.player.IWClientPlayerData;
import org.yang.interestingworld.entity.player.IWServerPlayerData;
import org.yang.interestingworld.util.*;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.List;

public class InfiniteSlashingAbility extends InfiniteAbility
{
	public static final short AbilityDuration = 20;
	public static final float AbilityDamage = 28;
	public static final int EffectDuration = 120;
	public static final int EffectAmplifier = 5;
	public static final double AttackMaxAngleCosine = 0.3;
	public static final double AttackMaxLength = 5;
	public static final double KnockbackDistance = 0.8;

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return EnergyToolDataFlag.getFromItemStack(stack).canSweep();
	}

	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("infiniteslashing_ability_detail").withColor(getColor()));
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("infiniteslashing_ability_title");
	}
/*
	@Override
	public IWUtil.Return.AbilityItemBarMessageTaker getAbilityItemBarRenderMessage(ItemStack stack)
	{
		IWUtil.Return.AbilityItemBarMessageTaker taker = new IWUtil.Return.AbilityItemBarMessageTaker();
		short amount = stack.getOrDefault(IWComponents.LEFT_USE_TIME, (short) 0);
		taker.baseColor = 0;
		taker.step = (amount < AbilityDuration) ? amount * 13 / AbilityDuration : 13;
		if (amount >= AbilityDuration) taker.contentColor = CYAN_RGB;
		else taker.contentColor = GRAY_RGB;
		return taker;
	}*/

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, IWServerPlayerData data, ItemStack stack)
	{
		if (data.chargeRate < AbilityDuration)
		{
			data.chargeRate++;
			data.shouldSync = true;
		}
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof ServerPlayerEntity player)
		{
			var manager = player.getIWServerPlayerData();
			if (manager.sweeping)
			{
				manager.sweeping = false;
				if (manager.chargeRate >= AbilityDuration)
				{
					manager.chargeRate = 0;
					manager.shouldSync = true;
					World world = attacker.getWorld();
					var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
					var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
					var damageSource = new DamageSource(IWDamageTypes.ENERGEE_MELEE_entry.get(), attacker);
					IWSoundUtil.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
					IWLivingEntityUtil.sweepEntity(attacker, AttackMaxAngleCosine, AttackMaxLength, target,
							(attacker1, entity, distance) -> {
								entity.takeKnockback(KnockbackDistance, knox, knoz);
								entity.damage(damageSource, AbilityDamage);
								IWStatusEffectUtil.addHiddenStatusEffectWithConsistence(entity, IWEffects.BLOOD,
										(int) (IWDamageUtil.getArmoredDamage(entity, damageSource, AbilityDamage) *
											   EffectDuration / AbilityDamage), EffectAmplifier, 20);
								double x = entity.getX();
								double y = entity.getBodyY(0.5);
								double z = entity.getZ();
								IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.5,
										0.5, 0.5);
							});
				}
			}
		}
	}

	@Override
	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.sweeping = false;
		manager.chargeRate = 0;
	}

	@Override
	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.charged = false;
		manager.chargeRate = -1;
	}

	@Override
	public int abilityBarForegroundColor(IWClientPlayerData data)
	{
		return Color.CYAN_RGB;
	}

	@Override
	public int abilityProcess(IWClientPlayerData data)
	{
		return data.charge_rate16;
	}

	@Override
	public void writeClientRenderDataToBuf(IWServerPlayerData data, RegistryByteBuf buf)
	{
		buf.writeInt(data.chargeRate);
	}

	@Override
	public AbstractRuneAbility getToolIndexParent()
	{
		return IWRuneAbilities.SLASHING_ABILITY;
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, ByteBuf buf)
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
