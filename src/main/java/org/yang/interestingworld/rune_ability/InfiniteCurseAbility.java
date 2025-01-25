package org.yang.interestingworld.rune_ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.yang.interestingworld.effect.IWEffects;
import org.yang.interestingworld.entity.player.IWClientPlayerData;
import org.yang.interestingworld.entity.player.IWServerPlayerData;
import org.yang.interestingworld.particle_type.IWParticleTypes;
import org.yang.interestingworld.util.IWLivingEntityUtil;
import org.yang.interestingworld.util.IWParticleUtil;
import org.yang.interestingworld.util.IWSoundUtil;
import org.yang.interestingworld.util.style.Color;

import java.util.List;

import static org.yang.interestingworld.util.Return.FAIL;
import static org.yang.interestingworld.util.Return.PASS;

public class InfiniteCurseAbility extends InfiniteAbility
{
	public static final short AbilityDuration = 40;
	public static final int CooldownAmplifier = 1;
	public static final int HurtingAmplifier = 9;
	public static final double AttackMaxLength = 4;
	public static final double Knockback = 1;
	public static final int ExplodeTick = 100;
	public static final int ExplodeDamage = 10;

	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("infinitecurse_ability_detail").withColor(getColor()));
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("infinitecurse_ability_title");
	}

	@Override
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (user instanceof ServerPlayerEntity player)
		{
			var data = player.getIWServerPlayerData();
			if (!data.isAbilityOn() || data.chargeRate < AbilityDuration) return FAIL;
			data.chargeRate = 0;
			data.shouldSync = true;
			IWSoundUtil.playSoundToPlayer(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS);
			double x = user.getX();
			double y = user.getY();
			double z = user.getZ();
			IWParticleUtil.spawnParticleAtPos(world, IWParticleTypes.INFINITECURSE_CYCLE, x, y + 0.001, z);
			IWParticleUtil.spawnParticleAtPos(world, new DustParticleEffect(new Vector3f(0.2f, 0.2f, 0.2f), 1.0f), x,
					y + 0.5, z, 48, 2.8, 2.3, 2.8, 0);
			data.chargeRate = 0;
			data.shouldSync = true;
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
				if (entity.getStatusEffect(IWEffects.INFINITECURSE) == null) entity.addStatusEffect(
						new StatusEffectInstance(IWEffects.INFINITECURSE, ExplodeTick + 1, ExplodeDamage - 1, false,
								true));
				entity.addStatusEffect(
						new StatusEffectInstance(IWEffects.COOLDOWN, -1, CooldownAmplifier, false, false));
			});
			return FAIL;
		}
		return PASS;
	}

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, IWServerPlayerData data, ItemStack stack)
	{
		if (data.chargeRate < AbilityDuration)
		{
			data.chargeRate++;
			data.shouldSync = true;
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack, UseAction before)
	{
		return UseAction.BOW;
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
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, ByteBuf buf)
	{
		int chargeRate = buf.readInt();
		int c = Math.clamp(chargeRate * 16L / AbilityDuration, 0, 16);
		if (data.client_ability_on && c == 16 && data.charge_rate16 != 16)
		{
			playChargedOverSound(entity);
		}
		data.charge_rate16 = c;
	}
}
