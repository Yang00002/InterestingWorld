package org.yang.interestingworld.rune.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWEffects;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.playerdatamanager.PlayerDataManager;
import org.yang.interestingworld.rune.IWRuneAbility;

import java.util.List;

import static org.yang.interestingworld.IWUtil.EnergyTool.getPlayerData;
import static org.yang.interestingworld.IWUtil.Registry.createDamageSource;
import static org.yang.interestingworld.IWUtil.Return.CONSUME;
import static org.yang.interestingworld.IWUtil.Return.FAIL;
import static org.yang.interestingworld.IWUtil.TextStyle.RED_RGB;

public class SlashingAbility extends IWRuneAbility
{
	public static final short MaxUseTime = 15;
	public static final float AbilityDamage = 3;
	public static final int EffectDuration = 120;
	public static final double AttackMaxAngleCosine = 0.5;
	public static final double AttackMaxLength = 4;
	public static final double AttackYExpanse = 1.0;
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
		var data = getPlayerData(user);
		if (data.charged || !data.tryExtractAutomicEnergy(stack, user, EnergyCosume)) return FAIL;
		data.timing = 0;
		return CONSUME;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if (!world.isClient && user instanceof PlayerEntity player)
		{
			var data = getPlayerData(player);
			if (remainingUseTicks <= 0)
			{
				boolean res = data.extractAutomicEnergy(stack, player, EnergyCosume);
				if (res)
				{
					data.charged = true;
					IWUtil.Network.playSoundToPlayer((PlayerEntity) user, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
							SoundCategory.PLAYERS);
				}
				data.timing = -1;
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
		if (!world.isClient && remainingUseTicks >= 0 && user instanceof PlayerEntity player)
		{
			var data = getPlayerData(player);
			if (remainingUseTicks == 0 && data.timing == 0) user.stopUsingItem();
		}
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof PlayerEntity)
		{
			var manager = getPlayerData((PlayerEntity) attacker);
			if (manager.sweeping)
			{
				manager.sweeping = false;
				if (manager.charged)
				{
					manager.charged = false;
					World world = attacker.getWorld();
					var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
					var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
					var damageSource = createDamageSource(world, IWDamageTypes.ENERGEE_MELEE, attacker);
					IWUtil.EnergyTool.horizontalSweepEntity(attacker, AttackMaxAngleCosine, AttackMaxLength,
							AttackYExpanse, target, (attacker1, entity, distance) -> {
								entity.takeKnockback(KnockbackDistance, knox, knoz);
								entity.damage(damageSource, AbilityDamage);
								IWUtil.EntityAbout.addHiddenStatusEffectWithConsistence(entity, IWEffects.BLOOD,
										(int) (IWUtil.EntityAbout.getArmoredDamage(entity, damageSource,
												AbilityDamage) * EffectDuration / AbilityDamage), 20);
								double x = entity.getX();
								double y = entity.getBodyY(0.5);
								double z = entity.getZ();
								IWUtil.Network.spawnParticleAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z);
								IWUtil.Network.playSoundAtPos(world, x, y, z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
										SoundCategory.PLAYERS);
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
	public void onEnter(PlayerEntity entity, PlayerDataManager manager)
	{
		manager.charged = false;
		manager.sweeping = false;
		manager.timing = -1;
	}

	@Override
	public void onLeave(PlayerEntity entity, PlayerDataManager manager)
	{
		manager.charged = false;
		manager.sweeping = false;
		manager.timing = -1;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack, boolean before)
	{
		return true;
	}
}
