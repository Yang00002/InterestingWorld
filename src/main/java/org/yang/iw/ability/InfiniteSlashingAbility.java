package org.yang.iw.ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
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
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;
import org.yang.iw.util.style.Color;

import static org.yang.iw.util.Return.*;

public class InfiniteSlashingAbility extends InfiniteAbility
{
	public static final short MaxAbilityDuration = 20;
	public static final short MinAbilityDuration = 10;
	public static final short AbilityDurationDownPerHit = 2;
	public static final float AbilityDamage = 28;
	public static final float AttackMaxAngleCosine = 0.3f;
	public static final float AttackMaxLength = 5;
	public static final float KnockbackDistance = 0.8f;
	public static final short ChargeTime = 40;
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

	public String id()
	{
		return "infiniteslashing";
	}

	public static int getAbilityDuration(IWServerPlayerData data)
	{
		return Math.max(MaxAbilityDuration - data.chargeStep * AbilityDurationDownPerHit, MinAbilityDuration);
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return EnergyToolDataFlag.fromItemStack(stack).canSweep();
	}

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, IWServerPlayerData data, ItemStack stack)
	{
		if (data.isCharged && data.used)
		{
			if (data.chargeRate > 0)
			{
				data.chargeRate--;
				if (data.chargeRate == 0) data.isCharged = false;
				data.shouldSync = true;
			}
		}
		else if (!data.isCharging && data.chargeRate < getAbilityDuration(data))
		{
			data.chargeRate++;
			data.shouldSync = true;
		}
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
				if (manager.isCharged)
				{
					manager.used = true;
					if (manager.sweeping)
						damage(player, target, ChargedSweepDamage, ChargedSweepKnockback, ChargedSweepMaxAngleCosine,
								ChargedSweepMaxLength);
					else
						damage(player, target, ChargedAttackDamage, ChargedAttackKnockback,
								ChargedAttackMaxAngleCosine,
								ChargedAttackMaxLength);
				}
				else
				{
					int duration = getAbilityDuration(manager);
					if (manager.chargeRate >= duration)
					{
						if (manager.sweeping)
						{
							if (duration > MinAbilityDuration) manager.chargeStep++;
							manager.chargeRate = 0;
							damage(player, target, AbilityDamage, KnockbackDistance, AttackMaxAngleCosine,
									AttackMaxLength);
						}
						else manager.chargeStep = 0;
						manager.shouldSync = true;
					}
				}
			}
		}
	}

	@Override
	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = 0;
		manager.isCharged = false;
		manager.chargeStep = 0;
		manager.isCharging = false;
		manager.used = false;
	}

	@Override
	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = -1;
		manager.isCharged = false;
		manager.chargeStep = -1;
		manager.isCharging = false;
		manager.used = false;
	}

	@Override
	public  AbstractAbility getToolRenderAbility()
	{
		return IWAbilities.SLASHING_ABILITY;
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
		buf.writeInt(data.isCharged ? ChargedTime : (data.isCharging ? ChargeTime : getAbilityDuration(data)));
		buf.writeBoolean(data.isCharged);
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, ByteBuf buf)
	{
		int chargeRate = buf.readInt();
		int maxRate = buf.readInt();
		boolean charged = buf.readBoolean();
		int c = Math.clamp(chargeRate * 16L / maxRate, 0, 16);
		if (data.client_ability_on)
		{
			if (c == 16 && data.charge_rate16 != 16) playChargedOverSound(entity);
			if (!data.is_charged && charged) entity.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
			else if (data.is_charged && !charged) entity.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
		}
		data.charge_rate16 = c;
		data.is_charged = charged;
	}

	@Override
	public void onServerAbilityClose(ServerPlayerEntity player, IWServerPlayerData data)
	{
		if (data.isCharged)
		{
			data.isCharged = false;
			data.isCharging = false;
			data.chargeStep = 0;
			data.shouldSync = true;
		}
		else if (data.isCharging)
		{
			player.stopUsingItem();
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
		return "o";
	}

	@Override
	public boolean shouldRenderAbilityText(IWClientPlayerData data)
	{
		return data.client_ability_on && data.is_charged;
	}

	@Override
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (!world.isClient)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) user;
			IWServerPlayerData data = player.interestingWorld$getIWServerPlayerData();
			if (!data.isAbilityOn() || data.isCharged) return IGNORE;
			if (data.chargeRate >= getAbilityDuration(data))
			{
				data.chargeRate = 0;
				data.chargeStep = 0;
				data.isCharging = true;
				data.shouldSync = true;
				data.used = false;
				return CONSUME;
			}
			else return FAIL;
		}
		return FAIL;
	}

	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		if (!world.isClient && user instanceof ServerPlayerEntity player)
		{
			var data = player.interestingWorld$getIWServerPlayerData();
			if (data.isCharging)
			{
				data.shouldSync = true;
				data.chargeRate = ChargeTime - remainingUseTicks;
			}
		}
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if (!world.isClient && user instanceof ServerPlayerEntity player)
		{
			IWServerPlayerData data = player.interestingWorld$getIWServerPlayerData();
			data.shouldSync = true;
			data.isCharging = false;
			data.chargeRate = 0;
		}
		return false;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		if (!world.isClient && user instanceof ServerPlayerEntity player)
		{
			IWServerPlayerData data = player.interestingWorld$getIWServerPlayerData();
			if (data.isCharging)
			{
				data.shouldSync = true;
				data.isCharging = false;
				data.isCharged = true;
				data.chargeRate = ChargedTime;
			}
		}
		return stack;
	}

	public int getMaxUseTime(ItemStack stack, LivingEntity user, int before)
	{
		return ChargeTime;
	}

}
