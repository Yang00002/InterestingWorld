package org.yang.iw.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.yang.iw.network.payload.S2CItemBreakParticlePayload;

import static org.yang.iw.util.Base.iwlogger;

public class IWParticleUtil
{
	public static void spawnItemParticle(Entity entity, ItemStack stack)
	{
		if (entity instanceof ServerPlayerEntity)
		{
			ServerPlayNetworking.send((ServerPlayerEntity) entity,
					new S2CItemBreakParticlePayload(new ItemStackParticleEffect(ParticleTypes.ITEM, stack)));
		}
		else iwlogger.warn("spawnItemParticle 尝试将 ClientPlayerEntity 转为 ServerPlayerEntity");
	}

	public static void spawnItemParticle(Entity entity, ParticleEffect effect)
	{
		if (entity instanceof ServerPlayerEntity)
		{
			ServerPlayNetworking.send((ServerPlayerEntity) entity, new S2CItemBreakParticlePayload(effect));
		}
		else iwlogger.warn("spawnItemParticle 尝试将 ClientPlayerEntity 转为 ServerPlayerEntity");
	}

	public static void spawnDamageIndicatorParticle(Entity entity, int amount)
	{
		World world = entity.getWorld();
		if (world instanceof ServerWorld)
		{
			((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(),
					entity.getBodyY(0.5), entity.getZ(), amount, 0.1, 0.0, 0.1, 0.2);
		}
		else iwlogger.warn("spawnDamageIndicatorParticle 尝试将 ClientWorld 转为 ServerWorld");
	}

	public static void spawnDamageIndicatorParticle(Entity entity, float damage)
	{
		World world = entity.getWorld();
		if (world instanceof ServerWorld)
		{
			((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(),
					entity.getBodyY(0.5), entity.getZ(), (int) damage, 0.1, 0.0, 0.1, 0.2);
		}
		else iwlogger.warn("spawnDamageIndicatorParticle 尝试将 ClientWorld 转为 ServerWorld");
	}

	public static void spawnParticleAtEntity(Entity entity, ParticleEffect particle)
	{
		World world = entity.getWorld();
		if (world instanceof ServerWorld)
		{
			((ServerWorld) world).spawnParticles(particle, entity.getX(), entity.getBodyY(0.5), entity.getZ(), 1, 0, 0,
					0, 0);
		}
		else iwlogger.warn("spawnParticleAtEntity 尝试将 ClientWorld 转为 ServerWorld");
	}

	public static void spawnParticleAtPos(World world, ParticleEffect particle, double x, double y, double z)
	{
		if (world instanceof ServerWorld) ((ServerWorld) world).spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
		else iwlogger.warn("spawnParticleAtPos 尝试将 ClientWorld 转为 ServerWorld");
	}

	public static void spawnParticlesAtPos(World world, ParticleEffect particle, double x, double y, double z,
										   int count, double dx, double dy, double dz)
	{
		if (world instanceof ServerWorld) ((ServerWorld) world).spawnParticles(particle, x, y, z, count, dx, dy, dz,
				0);
		else iwlogger.warn("spawnParticleAtPos 尝试将 ClientWorld 转为 ServerWorld");
	}

	public static void spawnParticleAtPos(World world, ParticleEffect particle, double x, double y, double z,
										  int count, double dx, double dy, double dz, double speed)
	{
		if (world instanceof ServerWorld)
			((ServerWorld) world).spawnParticles(particle, x, y, z, count, dx, dy, dz, speed);
		else iwlogger.warn("spawnParticleAtPos 尝试将 ClientWorld 转为 ServerWorld");
	}
}
