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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWEffects;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.playerdatamanager.PlayerDataManager;

import java.util.List;

import static org.yang.interestingworld.IWUtil.EnergyTool.getPlayerData;
import static org.yang.interestingworld.IWUtil.Registry.createDamageSource;

public class InfiniteSlashingAbility extends InfiniteAbility
{
	public static final short AbilityDuration = 20;
	public static final float AbilityDamage = 12;
	public static final int EffectDuration = 120;
	public static final int EffectAmplipier = 3;
	public static final double AttackMaxAngleCosine = 0.3;
	public static final double AttackMaxLength = 5;
	public static final double AttackYExpanse = 1.5;
	public static final double KnockbackDistance = 0.8;

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
	public void serverPlayerWeaponTick(PlayerEntity entity, PlayerDataManager data, ItemStack stack)
	{
		if (data.timing < AbilityDuration)
		{
			data.timing++;
			if (data.timing == AbilityDuration - 1 && entity instanceof PlayerEntity)
				IWUtil.Network.playSoundToPlayer(entity, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
						SoundCategory.PLAYERS);
		}
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof PlayerEntity player)
		{
			var manager = getPlayerData(player);
			if (manager.sweeping)
			{
				manager.sweeping = false;
				if (manager.timing >= AbilityDuration)
				{
					manager.timing = 0;
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
												AbilityDamage) * EffectDuration / AbilityDamage), EffectAmplipier, 20);
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
	public void onEnter(PlayerEntity entity, PlayerDataManager manager)
	{
		manager.sweeping = false;
		manager.timing = 0;
	}

	@Override
	public void onLeave(PlayerEntity entity, PlayerDataManager manager)
	{
		manager.charged = false;
		manager.timing = -1;
	}
}
