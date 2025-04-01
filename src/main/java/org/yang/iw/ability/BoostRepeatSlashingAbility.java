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
import net.minecraft.util.math.MathHelper;
import org.yang.iw.IWDamageTypes;
import org.yang.iw.IWSounds;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.component.ToolFlagComponent;
import org.yang.iw.entity.player.AbilityBarType;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.bitio.BitWriter;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;
import org.yang.iw.util.style.Color;

public class BoostRepeatSlashingAbility extends CommonAbility
{
	public static final int AbilityCooldown = 30;
	public static final int EnergyConsume = 20;
	public static final int SecondAttackTimeLimit = 40;
	public static final int ThirdAttackTimeLimit = 40;
	public static final int FirstAttackReturnEnergy = 15;
	public static final int SecondAttackReturnEnergy = 10;
	public static final int FirstAttackDamage = 10;
	public static final int SecondAttackDamage = 10;
	public static final int ThirdAttackDamage = 20;
	public static final float FirstAttackAngleCosine = 0.4f;
	public static final float SecondAttackAngleCosine = 0.3f;
	public static final float ThirdAttackAngleCosine = 0.2f;
	public static final float ThirdAttackKnockback = 0.6f;
	public static final int FirstAttackRange = 3;
	public static final int SecondAttackRange = 3;
	public static final int ThirdAttackRange = 6;

	BoostRepeatSlashingAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	@Override
	public AbilityCooldownGroup getCooldownGroup()
	{
		return AbilityCooldownGroup.ATTACK;
	}

	@Override
	public int level()
	{
		return 7;
	}

	public int getColor()
	{
		return Color.DARK_RED_RGB;
	}

	public String id()
	{
		return "boostrepeatslashing";
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return ToolFlagComponent.fromItemStack(stack).canSweep();
	}

	private int step(IWServerPlayerData data)
	{
		return data.loadRegister(0);
	}

	private void setStep(IWServerPlayerData data, int step)
	{
		data.setRegister(0, step);
		switch (step)
		{
			case 1 ->
			{
				data.setBarType(AbilityBarType.TICK_REVERSE);
				data.getTickManager().resetAndStart(ThirdAttackTimeLimit);
			}
			case 2 ->
			{
				data.setBarType(AbilityBarType.TICK_REVERSE);
				data.getTickManager().resetAndStart(SecondAttackTimeLimit);
			}
			case 3 ->
			{
				data.setBarType(AbilityBarType.EMPTY);
				data.getTickManager().pause();
			}
		}
		data.sync();
	}

	@Override
	public void onClientTickOver(ClientPlayerEntity player, IWClientPlayerData data)
	{
		player.playSound(SoundEvents.BLOCK_ANVIL_LAND);
	}

	@Override
	public void onServerTickOver(ServerPlayerEntity player, IWServerPlayerData data)
	{
		int step = step(data);
		switch (step)
		{
			case 2 -> data.returnEnergyToTool(player.getWeaponStack(), FirstAttackReturnEnergy);
			case 1 -> data.returnEnergyToTool(player.getWeaponStack(), SecondAttackReturnEnergy);
		}
		data.setCooldown(this, AbilityCooldown);
		setStep(data, 3);
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof ServerPlayerEntity player)
		{
			var manager = player.interestingWorld$getIWServerPlayerData();
			int step = step(manager);
			if (manager.isAbilityOn() && !manager.isInCooldown(this) &&
				(step < 3 || (manager.sweeping && manager.extractAtomicEnergy(stack, EnergyConsume))))
			{
				int damage;
				int range;
				float angle;
				switch (step)
				{
					case 3 ->
					{
						damage = FirstAttackDamage;
						range = FirstAttackRange;
						angle = FirstAttackAngleCosine;
						setStep(manager, 2);
					}
					case 2 ->
					{
						damage = SecondAttackDamage;
						range = SecondAttackRange;
						angle = SecondAttackAngleCosine;
						setStep(manager, 1);
					}
					case 1 ->
					{
						damage = ThirdAttackDamage;
						range = ThirdAttackRange;
						angle = ThirdAttackAngleCosine;
						setStep(manager, 3);
						manager.setCooldown(this, AbilityCooldown);
					}
					default ->
					{
						damage = 0;
						range = 0;
						angle = 0.0f;
					}
				}
				manager.sync();
				ServerWorld world = (ServerWorld) player.getWorld();
				var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
				var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
				var damageSource = new DamageSource(IWDamageTypes.ENERGY_EXPLODE.entry(), attacker);
				var pc = 3 + ((3 - step) >> 1);
				IWSoundUtil.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
				IWLivingEntityUtil.sweepEntity(attacker, angle, range, target, (attacker1, entity, distance) -> {
					if (step > 2) entity.takeKnockback(ThirdAttackKnockback, knox, knoz);
					entity.damage(world, damageSource, damage);
					double x = entity.getX();
					double y = entity.getBodyY(0.5);
					double z = entity.getZ();
					IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, pc, 0.5, 0.5, 0.5);
				});
			}
		}
	}

	@Override
	public void onServerAbilityClose(ServerPlayerEntity player, IWServerPlayerData data)
	{
		ItemStack weaponStack = player.getWeaponStack();
		if (!weaponStack.isEmpty())
		{
			switch (step(data))
			{
				case 2 ->
				{
					data.returnEnergyToTool(weaponStack, FirstAttackReturnEnergy);
					data.setCooldown(this, AbilityCooldown);
					setStep(data, 3);
				}
				case 1 ->
				{
					data.returnEnergyToTool(weaponStack, SecondAttackReturnEnergy);
					data.setCooldown(this, AbilityCooldown);
					setStep(data, 3);
				}
			}
		}
	}

	@Override
	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
		setStep(manager, 3);
	}

	@Override
	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
	{
		switch (step(manager))
		{
			case 2 ->
			{
				manager.returnEnergyToPlayer(FirstAttackReturnEnergy);
				manager.setCooldown(this, AbilityCooldown);
				setStep(manager, 3);
			}
			case 1 ->
			{
				manager.returnEnergyToPlayer(SecondAttackReturnEnergy);
				manager.setCooldown(this, AbilityCooldown);
				setStep(manager, 3);
			}
		}
	}

	@Override
	public void writeClientRenderDataToBuf(IWServerPlayerData data, BitWriter buf)
	{
		buf.writeUnsignedInt(step(data), 2);
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, BitReader buf)
	{
		data.setRegister(0, buf.readUnsignedInt(2));
	}

	@Override
	public String abilityText(IWClientPlayerData data)
	{
		return String.valueOf(data.loadRegister(0));
	}

	@Override
	public int abilityTextColor()
	{
		return Color.DARK_RED_RGB;
	}

	@Override
	public AbstractAbility getToolRenderAbility()
	{
		return IWAbilities.SLASHING_ABILITY;
	}

}
