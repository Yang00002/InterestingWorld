package org.yang.interestingworld.rune.ability;

import net.minecraft.entity.Entity;
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
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWEffects;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;

import java.util.List;

import static org.yang.interestingworld.IWUtil.Registry.createDamageSource;
import static org.yang.interestingworld.IWUtil.TextStyle.CYAN_RGB;
import static org.yang.interestingworld.IWUtil.TextStyle.GRAY_RGB;

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
	public void onRemoveAbility(ItemStack stack)
	{
		stack.remove(IWComponents.HAD_SWEEPING);
		stack.remove(IWComponents.LEFT_USE_TIME);
	}

	@Override
	public void onSetAbility(ItemStack stack)
	{
		stack.remove(IWComponents.HAD_SWEEPING);
		stack.remove(IWComponents.LEFT_USE_TIME);
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

	@Override
	public void atSweeping(ItemStack stack, PlayerEntity entity)
	{
		stack.set(IWComponents.HAD_SWEEPING, true);
	}

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
	}

	@Override
	public void ServerInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if (selected)
		{
			short t = stack.getOrDefault(IWComponents.LEFT_USE_TIME, (short) 0);
			if (t < AbilityDuration)
			{
				stack.set(IWComponents.LEFT_USE_TIME, (short) (t + 1));
				if (t == AbilityDuration - 1 && entity instanceof PlayerEntity)
					IWUtil.Network.playSoundToPlayer((PlayerEntity) entity, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
							SoundCategory.PLAYERS);
			}
		}
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (stack.getOrDefault(IWComponents.HAD_SWEEPING, false))
		{
			stack.set(IWComponents.HAD_SWEEPING, false);
			short time = stack.getOrDefault(IWComponents.LEFT_USE_TIME, (short) 0);
			if (time >= AbilityDuration)
			{
				stack.set(IWComponents.LEFT_USE_TIME, (short) 0);
				World world = attacker.getWorld();
				var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
				var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
				var damageSource = createDamageSource(world, IWDamageTypes.ENERGEE_MELEE, attacker);
				IWUtil.EnergyTool.horizontalSweepEntity(attacker, AttackMaxAngleCosine, AttackMaxLength,
						AttackYExpanse,
						target, (attacker1, entity, distance) -> {
							entity.takeKnockback(KnockbackDistance, knox, knoz);
							entity.damage(damageSource, AbilityDamage);
							IWUtil.EntityAbout.addHiddenStatusEffectWithConsistence(entity, IWEffects.BLOOD,
									(int) (IWUtil.EntityAbout.getArmoredDamage(entity, damageSource, AbilityDamage) *
										   EffectDuration / AbilityDamage), EffectAmplipier, 20);
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
