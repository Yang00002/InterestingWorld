package org.yang.iw.ability;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.bitio.BitWriter;
import org.yang.iw.particle_type.IWParticleTypes;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;

import static org.yang.iw.util.Return.FAIL;
import static org.yang.iw.util.Return.PASS;

public class InfiniteCurseAbility extends InfiniteAbility
{
	public static final short AbilityDuration = 40;
	public static final int CooldownAmplifier = 1;
	public static final int HurtingAmplifier = 9;
	public static final double AttackMaxLength = 4;
	public static final double Knockback = 1;
	public static final int ExplodeTick = 100;
	public static final int ExplodeDamage = 10;

	InfiniteCurseAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	public String id()
	{
		return "infinitecurse";
	}

	@Override
	public AbilityCooldownGroup getCooldownGroup()
	{
		return AbilityCooldownGroup.ASSISTANCE;
	}

	@Override
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (user instanceof ServerPlayerEntity player)
		{
			var data = player.interestingWorld$getIWServerPlayerData();
			if (!data.isAbilityOn() || data.isInCooldown(this)) return FAIL;
			IWSoundUtil.playSoundToPlayer(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS);
			double x = user.getX();
			double y = user.getY();
			double z = user.getZ();
			IWParticleUtil.spawnParticleAtPos(world, IWParticleTypes.INFINITECURSE_CYCLE, x, y + 0.001, z);
			IWParticleUtil.spawnParticleAtPos(world, new DustParticleEffect(0X323232, 1.0f), x, y + 0.5, z, 48, 2.8,
					2.3, 2.8, 0);
			var atx = user.getX();
			var atz = user.getZ();
			var knox = MathHelper.sin(user.getYaw() * 0.017453292F);
			var knoz = -MathHelper.cos(user.getYaw() * 0.017453292F);
			IWLivingEntityUtil.influenceEntityAround(user, AttackMaxLength, (attacker, entity, distance) -> {
				var ex = entity.getX();
				var ez = entity.getZ();
				var tx = atx - ex;
				var tz = atz - ez;
				var len = Math.sqrt(tx * tx + tz * tz);
				if (len < 0.01) entity.takeKnockback(Knockback, knox, knoz);
				else entity.takeKnockback(Knockback, tx, tz);
				entity.addStatusEffect(new StatusEffectInstance(IWEffects.HURTING, -1, HurtingAmplifier));
				if (entity.getStatusEffect(IWEffects.INFINITECURSE) != null)
					entity.removeStatusEffect(IWEffects.INFINITECURSE);
				entity.addStatusEffect(
						new StatusEffectInstance(IWEffects.INFINITECURSE, ExplodeTick + 1, ExplodeDamage - 1, false,
								true));
				entity.addStatusEffect(
						new StatusEffectInstance(IWEffects.COOLDOWN, -1, CooldownAmplifier, false, false));
			});
			data.setCooldown(this, AbilityDuration);
			return FAIL;
		}
		return PASS;
	}

	@Override
	public UseAction getUseAction(ItemStack stack, UseAction before)
	{
		return UseAction.BOW;
	}

	@Override
	public AbstractAbility getToolRenderAbility()
	{
		return IWAbilities.INFINITECURSE_ABILITY;
	}
}
