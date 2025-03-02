package org.yang.iw.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.yang.iw.IWDamageTypes;
import org.yang.iw.IWSounds;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;
import org.yang.iw.util.IWStatusEffectUtil;

public class InfiniteEviscerateAbility extends InfiniteAbility
{
	public static final short AbilityCooldown = 20;
	public static final float PerLevelAbilityDamage = 1.5f;
	public static final int EffectDuration = 80;
	public static final double AttackMaxAngleCosine = 0.4;
	public static final double AttackMaxLength = 5;
	public static final int LevelCap = 10;
	public static final float Knockback = 0.4f;

	InfiniteEviscerateAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	public String id()
	{
		return "infiniteeviscerate";
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return EnergyToolDataFlag.fromItemStack(stack).canSweep();
	}

	@Override
	public AbilityCooldownGroup getCooldownGroup()
	{
		return AbilityCooldownGroup.ATTACK;
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof ServerPlayerEntity player)
		{
			var manager = player.interestingWorld$getIWServerPlayerData();
			if (manager.isAbilityOn() && manager.sweeping)
			{
				ServerWorld world = (ServerWorld) player.getWorld();
				var damageSource = new DamageSource(IWDamageTypes.BLOOD_EFFECT_ENTRY.get(), attacker);
				if (!manager.isInCooldown(this))
				{
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
									entity.damage(world, damageSource, amplifier * PerLevelAbilityDamage);
								}
								IWStatusEffectUtil.addHiddenStatusEffectWithConsistence(entity, IWEffects.BLOOD,
										EffectDuration, amplifier, 20);
								double x = entity.getX();
								double y = entity.getBodyY(0.5);
								double z = entity.getZ();
								IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.5,
										0.5, 0.5);
							});
					manager.setCooldown(this, AbilityCooldown);
				}
				else
				{
					var targetEffect = target.getStatusEffect(IWEffects.BLOOD);
					if (targetEffect != null)
						target.damage(world, damageSource, (targetEffect.getAmplifier() + 1) * PerLevelAbilityDamage);
				}
			}
		}
	}

	@Override
	public AbstractAbility getToolRenderAbility()
	{
		return IWAbilities.SLASHING_ABILITY;
	}

}
