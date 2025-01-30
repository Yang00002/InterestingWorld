package org.yang.iw.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.yang.iw.network.payload.S2CDeferSoundPayload;

import static org.yang.iw.util.Base.iwlogger;

public class IWSoundUtil
{
	public static void playSoundAtEntity(Entity entity, SoundEvent sound, SoundCategory category)
	{
		World world = entity.getWorld();
		if (world instanceof ServerWorld)
		{
			world.playSound(null, entity.getX(), entity.getBodyY(0.5), entity.getZ(), sound, category, 1.0f, 1.0f, 0);
		}
		else iwlogger.warn("playSoundAtEntity 尝试将 ClientWorld 转为 ServerWorld");
	}

	public static void playSoundAtPos(World world, double x, double y, double z, SoundEvent sound,
									  SoundCategory category)
	{
		if (world instanceof ServerWorld)
		{
			world.playSound(null, x, y, z, sound, category, 1.0f, 1.0f, 0);
		}
		else iwlogger.warn("playSoundAtPos 尝试将 ClientWorld 转为 ServerWorld");
	}

	public static void playSoundToPlayer(PlayerEntity player, SoundEvent sound, SoundCategory category)
	{
		if (player instanceof ServerPlayerEntity) player.playSoundToPlayer(sound, category, 1.0f, 1.0f);
		else iwlogger.warn("playSoundToPlayer 尝试将 ClientPlayerEntity 转为 ServerPlayerEntity");
	}

	public static void playSoundToPlayer(PlayerEntity player, SoundEvent sound, SoundCategory category, int delay)
	{
		if (player instanceof ServerPlayerEntity sp)
		{
			ServerPlayNetworking.send(sp,
					new S2CDeferSoundPayload(Registries.SOUND_EVENT.getEntry(sound), category, (float) sp.getX(),
							(float) sp.getY(), (float) sp.getZ(), 1.0f, 1.0f, delay));
		}
		else iwlogger.warn("playSoundToPlayer 尝试将 ClientPlayerEntity 转为 ServerPlayerEntity");
	}
}
