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
import org.yang.interestingworld.util.IWLivingEntityUtil;
import org.yang.interestingworld.util.IWParticleUtil;
import org.yang.interestingworld.util.IWSoundUtil;
import org.yang.interestingworld.util.IWStatusEffectUtil;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.List;

public class InfiniteEviscerateAbility extends InfiniteAbility
{
	public static final short AbilityCooldown = 20;
	public static final float PerLevelAbilityDamage = 1.5f;
	public static final int EffectDuration = 80;
	public static final double AttackMaxAngleCosine = 0.4;
	public static final double AttackMaxLength = 5;
	public static final int LevelCap = 10;
	public static final float Knockback = 0.4f;
	public String id()
	{
		return "infiniteeviscerate";
	}
	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return EnergyToolDataFlag.getFromItemStack(stack).canSweep();
	}

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, IWServerPlayerData data, ItemStack stack)
	{
		if (data.chargeRate < AbilityCooldown)
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
			if (manager.isAbilityOn() && manager.sweeping)
			{
				World world = attacker.getWorld();
				var damageSource = new DamageSource(IWDamageTypes.BLOOD_EFFECT_entry.get(), attacker);
				if (manager.chargeRate >= AbilityCooldown)
				{
					manager.chargeRate = 0;
					manager.shouldSync = true;
					IWSoundUtil.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
					var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
					var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
					IWLivingEntityUtil.sweepEntity(attacker, AttackMaxAngleCosine, AttackMaxLength, target,
							(attacker1, entity, distance) -> {
								entity.takeKnockback(Knockback, knox, knoz);
								int amplifier = 0;
								var preEffect = entity.getStatusEffect(IWEffects.BLOOD);
								if (preEffect != null)
								{
									amplifier = Math.min(preEffect.getAmplifier() + 1, LevelCap - 1);
									entity.damage(damageSource, amplifier * PerLevelAbilityDamage);
								}
								IWStatusEffectUtil.addHiddenStatusEffectWithConsistence(entity, IWEffects.BLOOD,
										EffectDuration, amplifier, 20);
								double x = entity.getX();
								double y = entity.getBodyY(0.5);
								double z = entity.getZ();
								IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.5,
										0.5, 0.5);
							});
				}
				else
				{
					var targetEffect = target.getStatusEffect(IWEffects.BLOOD);
					if (targetEffect != null)
						target.damage(damageSource, (targetEffect.getAmplifier() + 1) * PerLevelAbilityDamage);
				}
			}
		}
	}

	@Override
	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = 0;
	}

	@Override
	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = -1;
	}

	@Override
	public int abilityBarForegroundColor(IWClientPlayerData data)
	{
		return data.client_ability_on ? Color.CYAN_RGB : Color.GRAY_RGB;
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
	public AbstractRuneAbility getRuneIndexParent()
	{
		return IWRuneAbilities.INFINITESLASHING_ABILITY;
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, ByteBuf buf)
	{
		int chargeRate = buf.readInt();
		int c = Math.clamp(chargeRate * 16L / AbilityCooldown, 0, 16);
		if (data.client_ability_on && c == 16 && data.charge_rate16 != 16)
		{
			playChargedOverSound(entity);
		}
		data.charge_rate16 = c;
	}
}
