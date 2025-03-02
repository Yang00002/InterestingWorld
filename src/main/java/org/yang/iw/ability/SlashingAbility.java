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
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;
import org.yang.iw.util.style.Color;

public class SlashingAbility extends CommonAbility
{
	public static final short CooldownTicks = 30;
	public static final float AttackDamage = 3;
	public static final double AttackMaxAngleCosine = 0.5;
	public static final double AttackMaxLength = 3.5;
	public static final float EnergyConsume = 5;
	public static final double KnockbackDistance = 0.5;

	SlashingAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	public String id()
	{
		return "slashing";
	}

	public int getColor()
	{
		return Color.RED_RGB;
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
			if (manager.isAbilityOn() && manager.sweeping && !manager.isInCooldown(this) &&
				manager.extractAtomicEnergy(stack, EnergyConsume))
			{
				ServerWorld world = (ServerWorld) player.getWorld();
				var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
				var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
				var damageSource = new DamageSource(IWDamageTypes.ENERGY_MELEE_ENTRY.get(), attacker);
				IWSoundUtil.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
				IWLivingEntityUtil.sweepEntity(attacker, AttackMaxAngleCosine, AttackMaxLength, target,
						(attacker1, entity, distance) -> {
							entity.takeKnockback(KnockbackDistance, knox, knoz);
							entity.damage(world, damageSource, AttackDamage);
							double x = entity.getX();
							double y = entity.getBodyY(0.5);
							double z = entity.getZ();
							IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.5, 0.5,
									0.5);
						});
				manager.setCooldown(this, CooldownTicks);
			}
		}
	}

	@Override
	public AbstractAbility getToolRenderAbility()
	{
		return IWAbilities.SLASHING_ABILITY;
	}
}
