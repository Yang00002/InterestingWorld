package org.yang.interestingworld.rune.ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWEffects;
import org.yang.interestingworld.IWSounds;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.playerdatamanager.ClientPlayerDataManager;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataManager;
import org.yang.interestingworld.rune.IWRuneAbility;

import java.util.List;

import static org.yang.interestingworld.IWUtil.EnergyTool.getPlayerData;
import static org.yang.interestingworld.IWUtil.Registry.createDamageSource;
import static org.yang.interestingworld.IWUtil.Return.*;
import static org.yang.interestingworld.IWUtil.TextStyle.RED_RGB;

public class SlashingAbility extends IWRuneAbility
{
	public static final short MaxUseTime = 15;
	public static final float AbilityDamage = 3;
	public static final int EffectDuration = 120;
	public static final double AttackMaxAngleCosine = 0.5;
	public static final double AttackMaxLength = 4;
	public static final float EnergyCosume = 10;
	public static final double KnockbackDistance = 0.4;

	public int getColor()
	{
		return RED_RGB;
	}

	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("slashing_ability_detail").withColor(getColor()));
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		Item it = stack.getItem();
		if (it instanceof EnergyToolItem)
		{
			return ((EnergyToolItem) it).canSweep(stack);
		}
		return false;
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("slashing_ability_title");
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user, int before)
	{
		return MaxUseTime;
	}

	@Override
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (user instanceof ServerPlayerEntity player)
		{
			var data = getPlayerData(player);
			if (data.charged || !data.tryExtractAutomicEnergy(stack, player, EnergyCosume)) return FAIL;
			return CONSUME;
		}
		return PASS;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if (user instanceof ServerPlayerEntity player)
		{
			var data = getPlayerData(player);
			if (remainingUseTicks <= 0)
			{
				boolean res = data.extractAutomicEnergy(stack, player, EnergyCosume);
				if (res) data.charged = true;
				data.isCharging = false;
			}
		}
	}


	/*
	@Override
	public IWUtil.Return.AbilityItemBarMessageTaker getAbilityItemBarRenderMessage(ItemStack stack)
	{
		boolean isCharged = stack.getOrDefault(IWComponents.CHARGE_OVER, false);
		if (isCharged)
		{
			AbilityItemBarMessageTaker taker = new AbilityItemBarMessageTaker();
			taker.baseColor = 0;
			taker.step = 13;
			taker.contentColor = RED_RGB;
			return taker;
		}
		else
		{
			int rt = stack.getOrDefault(IWComponents.LEFT_USE_TIME, (short) -1);
			if (rt >= 0)
			{
				AbilityItemBarMessageTaker taker = new AbilityItemBarMessageTaker();
				taker.baseColor = 0;
				taker.step = (rt < MaxUseTime) ? (MaxUseTime - rt) * 13 / MaxUseTime : (0);
				taker.contentColor = GRAY_RGB;
				return taker;
			}
		}
		return null;
	}*/

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		if (remainingUseTicks >= 0 && user instanceof ServerPlayerEntity player)
		{
			var data = getPlayerData(player);
			if (remainingUseTicks != data.leftusetime)
			{
				data.leftusetime = remainingUseTicks;
				data.shouldSync = true;
			}
			if (remainingUseTicks == 0 && data.isCharging) user.stopUsingItem();
		}
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof ServerPlayerEntity player)
		{
			var manager = getPlayerData(player);
			if (manager.sweeping)
			{
				manager.sweeping = false;
				if (manager.charged)
				{
					manager.charged = false;
					manager.leftusetime = MaxUseTime;
					manager.shouldSync = true;
					World world = attacker.getWorld();
					var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
					var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
					var damageSource = createDamageSource(world, IWDamageTypes.ENERGEE_MELEE, attacker);
					IWUtil.Network.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
					IWUtil.EnergyTool.sweepEntity(attacker, AttackMaxAngleCosine, AttackMaxLength, target,
							(attacker1, entity, distance) -> {
								entity.takeKnockback(KnockbackDistance, knox, knoz);
								entity.damage(damageSource, AbilityDamage);
								IWUtil.EntityAbout.addHiddenStatusEffectWithConsistence(entity, IWEffects.BLOOD,
										(int) (IWUtil.EntityAbout.getArmoredDamage(entity, damageSource,
												AbilityDamage) * EffectDuration / AbilityDamage), 20);
								double x = entity.getX();
								double y = entity.getBodyY(0.5);
								double z = entity.getZ();
								IWUtil.Network.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.5,
										0.5, 0.5);
							});
				}
			}
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
		manager.leftusetime = MaxUseTime;
		manager.charged = false;
		manager.sweeping = false;
		manager.isCharging = false;
	}

	@Override
	public void onLeave(PlayerEntity entity, ServerPlayerDataManager manager)
	{
		manager.leftusetime = 0;
		manager.charged = false;
		manager.sweeping = false;
		manager.isCharging = false;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack, boolean before)
	{
		return true;
	}

	@Override
	public int abilityBarForegroundColor()
	{
		return RED_RGB;
	}

	@Override
	public int abilityProcess(ClientPlayerDataManager data)
	{
		return data.charge_rate16;
	}

	@Override
	public void writeClientRenderDataToBuf(ServerPlayerDataManager data, RegistryByteBuf buf)
	{
		buf.writeInt(data.leftusetime);
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, ClientPlayerDataManager data, ByteBuf buf)
	{
		int leftusetime = buf.readInt();
		int c = Math.clamp((MaxUseTime - leftusetime) * 16L / MaxUseTime, 0, 16);
		if (c == 16 && data.charge_rate16 != 16)
		{
			playChargedOverSound(entity);
		}
		data.charge_rate16 = c;
	}
}
