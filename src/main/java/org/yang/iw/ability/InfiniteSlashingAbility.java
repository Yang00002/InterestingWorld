package org.yang.iw.ability;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.iw.IWDamageTypes;
import org.yang.iw.IWSounds;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.entity.player.AbilityBarType;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.bitio.BitWriter;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;

import static org.yang.iw.util.Return.FAIL;
import static org.yang.iw.util.Return.IGNORE;

public class InfiniteSlashingAbility extends InfiniteAbility
{
	public static final short MaxAbilityDuration = 20;
	public static final short MaxChargeStep = 5;
	public static final short AbilityDurationDownPerHit = 2;
	public static final float AbilityDamage = 28;
	public static final float AttackMaxAngleCosine = 0.3f;
	public static final float AttackMaxLength = 5;
	public static final float KnockbackDistance = 0.8f;
	public static final short ChargeTime = 60;
	public static final short ChargedTime = 60;
	public static final float ChargedAttackDamage = 15;
	public static final float ChargedSweepDamage = 40;
	public static final float ChargedAttackKnockback = 0.1f;
	public static final float ChargedSweepKnockback = 0.4f;
	public static final float ChargedAttackMaxAngleCosine = 0.5f;
	public static final float ChargedAttackMaxLength = 4;
	public static final float ChargedSweepMaxAngleCosine = 0.2f;
	public static final float ChargedSweepMaxLength = 6;

	InfiniteSlashingAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	@Override
	public AbilityCooldownGroup getCooldownGroup()
	{
		return AbilityCooldownGroup.ATTACK;
	}

	public String id()
	{
		return "infiniteslashing";
	}

	public int getAbilityDuration(IWServerPlayerData data)
	{
		return MaxAbilityDuration - step(data) * AbilityDurationDownPerHit;
	}

	private int step(IWServerPlayerData data)
	{
		return data.loadRegister(0);
	}

	private void setStep(IWServerPlayerData data, int step)
	{
		if (step <= MaxChargeStep)
		{
			data.setRegister(0, step);
		}
	}

	private boolean charged(IWServerPlayerData data)
	{
		return data.loadRegister(1) > 0;
	}

	private void setCharged(IWServerPlayerData data, boolean c)
	{
		data.setRegister(1, c ? 1 : 0);
		data.sync();
		if (!c)
		{
			data.setBarType(AbilityBarType.EMPTY);
			data.getTickManager().pause();
		}
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return EnergyToolDataFlag.fromItemStack(stack).canSweep();
	}

	public static void damage(ServerPlayerEntity player, LivingEntity target, float damage, float knockback,
							  float angleCosine, float range)
	{
		ServerWorld world = (ServerWorld) player.getWorld();
		var knox = MathHelper.sin(player.getYaw() * 0.017453292F);
		var knoz = -MathHelper.cos(player.getYaw() * 0.017453292F);
		var damageSource = new DamageSource(IWDamageTypes.ENERGY_MELEE_ENTRY.get(), player);
		IWSoundUtil.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
		IWLivingEntityUtil.sweepEntity(player, angleCosine, range, target, (attacker1, entity, distance) -> {
			entity.takeKnockback(knockback, knox, knoz);
			entity.damage(world, damageSource, damage);
			double x = entity.getX();
			double y = entity.getBodyY(0.5);
			double z = entity.getZ();
			IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.5, 0.5, 0.5);
		});
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof ServerPlayerEntity player)
		{
			var manager = player.interestingWorld$getIWServerPlayerData();
			if (manager.isAbilityOn())
			{
				if (!manager.isInCooldown(this))
				{
					if (charged(manager))
					{
						if (!manager.getTickManager().isOn())
						{
							manager.getTickManager().resetAndStart(ChargedTime);
							manager.setBarType(AbilityBarType.TICK_REVERSE);
						}
						if (manager.sweeping) damage(player, target, ChargedSweepDamage, ChargedSweepKnockback,
								ChargedSweepMaxAngleCosine, ChargedSweepMaxLength);
						else damage(player, target, ChargedAttackDamage, ChargedAttackKnockback,
								ChargedAttackMaxAngleCosine, ChargedAttackMaxLength);
					}
					else
					{
						if (manager.sweeping)
						{
							int step = step(manager);
							if (step < MaxChargeStep) setStep(manager, step + 1);
							manager.setCooldown(this, getAbilityDuration(manager));
							damage(player, target, AbilityDamage, KnockbackDistance, AttackMaxAngleCosine,
									AttackMaxLength);
						}
						else setStep(manager, 0);
					}
				}
				else if (!manager.sweeping) setStep(manager, 0);
			}
		}
	}

	@Override
	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
		setStep(manager, 0);
		setCharged(manager, false);
	}

	@Override
	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
	{
		if (charged(manager))
		{
			setCharged(manager, false);
			manager.setCooldown(this, ChargeTime);
		}
	}

	@Override
	public AbstractAbility getToolRenderAbility()
	{
		return IWAbilities.SLASHING_ABILITY;
	}

	@Override
	public void writeClientRenderDataToBuf(IWServerPlayerData data, BitWriter buf)
	{
		buf.writeBoolean(charged(data));
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, BitReader buf)
	{
		data.setRegister(0, buf.readBoolean() ? 1 : 0);
	}

	@Override
	public void onServerAbilityClose(ServerPlayerEntity player, IWServerPlayerData data)
	{
		if (charged(data))
		{
			setCharged(data, false);
			data.setCooldown(this, ChargeTime);
		}
	}

	@Override
	public int abilityTextColor()
	{
		return getColor();
	}

	@Override
	public String abilityText(IWClientPlayerData data)
	{
		return data.loadRegister(0) > 0 ? "o" : null;
	}

	@Override
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (!world.isClient)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) user;
			IWServerPlayerData data = player.interestingWorld$getIWServerPlayerData();
			if (!data.isAbilityOn() || charged(data) || data.isInCooldown(this)) return IGNORE;
			IWSoundUtil.playSoundToPlayer(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS);
			setCharged(data, true);
			setStep(data, 0);
		}
		return FAIL;
	}

	@Override
	public void onClientTickOver(ClientPlayerEntity player, IWClientPlayerData data)
	{
		player.playSound(SoundEvents.BLOCK_ANVIL_LAND);
	}

	@Override
	public void onServerTickOver(ServerPlayerEntity player, IWServerPlayerData data)
	{
		setCharged(data, false);
		data.setCooldown(this, ChargeTime);
	}
}
