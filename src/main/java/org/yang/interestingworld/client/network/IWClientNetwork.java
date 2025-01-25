package org.yang.interestingworld.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.yang.interestingworld.entity.player.IWClientPlayerData;
import org.yang.interestingworld.network.payload.*;
import org.yang.interestingworld.rune_ability.AbstractRuneAbility;

public class IWClientNetwork
{
	private static final java.util.Random RANDOM = new java.util.Random();

	private static void handleItemBreakParticlePayload(S2CItemBreakParticlePayload payload,
													   ClientPlayNetworking.Context context)
	{
		ClientPlayerEntity player = context.player();
		World world = player.getWorld();
		var rd = world.random;
		for (int i = 0; i < 5; ++i)
		{
			Vec3d vec3d = new Vec3d(((double) rd.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-player.getPitch() * 0.017453292F);
			vec3d = vec3d.rotateY(-player.getYaw() * 0.017453292F);
			double d = (double) (-rd.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double) rd.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-player.getPitch() * 0.017453292F);
			vec3d2 = vec3d2.rotateY(-player.getYaw() * 0.017453292F);
			vec3d2 = vec3d2.add(player.getX(), player.getEyeY(), player.getZ());
			world.addParticle(payload.effect(), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
		}
	}

	private static void handlePlayerEnergyPayload(S2CPlayerEnergyDataPayload payload,
												  ClientPlayNetworking.Context context)
	{
		ClientPlayerEntity player = context.player();
		IWClientPlayerData manager = player.getIWClientPlayerData();
		manager.shown_energy = payload.energy();
	}

	private static void handlePlayerAbilityBarPayload(S2CAbilityDataPayload payload,
													  ClientPlayNetworking.Context context)
	{
		ClientPlayerEntity player = context.player();
		IWClientPlayerData manager = player.getIWClientPlayerData();
		AbstractRuneAbility ab = payload.ability();
		manager.WeaponAbility = ab;
		ab.readClientRenderDataFromBuf(player, manager, payload.clientData());
	}

	private static void handleDeferSoundPayload(S2CDeferSoundPayload payload, ClientPlayNetworking.Context context)
	{
		var client = context.client();
		PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(payload.sound().value(),
				payload.category(), payload.volume(), payload.pitch(), Random.create(RANDOM.nextLong()), payload.x(),
				payload.y(), payload.z());
		client.getSoundManager().play(positionedSoundInstance, payload.delay());
	}

	private static void handlePlayerDataInitializePayload(S2CPlayerDataInitializePayload payload,
														  ClientPlayNetworking.Context context)
	{
		var client = context.client();
		if (client != null)
		{
			var player = client.player;
			if (player != null)
			{
				var data = player.getIWClientPlayerData();
				data.handleS2CInitializeDataBuffer(payload.clientData());
			}
		}
	}

	public static void initialize()
	{
		PayloadTypeRegistry.playS2C().register(S2CItemBreakParticlePayload.ID, S2CItemBreakParticlePayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(S2CItemBreakParticlePayload.ID,
				(payload, context) -> context.client().execute(() -> handleItemBreakParticlePayload(payload,
						context)));
		PayloadTypeRegistry.playS2C().register(S2CPlayerEnergyDataPayload.ID, S2CPlayerEnergyDataPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(S2CPlayerEnergyDataPayload.ID,
				(payload, context) -> context.client().execute(() -> handlePlayerEnergyPayload(payload, context)));
		PayloadTypeRegistry.playS2C().register(S2CAbilityDataPayload.ID, S2CAbilityDataPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(S2CAbilityDataPayload.ID,
				(payload, context) -> context.client().execute(() -> handlePlayerAbilityBarPayload(payload, context)));
		PayloadTypeRegistry.playS2C().register(S2CDeferSoundPayload.ID, S2CDeferSoundPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(S2CDeferSoundPayload.ID,
				(payload, context) -> context.client().execute(() -> handleDeferSoundPayload(payload, context)));
		PayloadTypeRegistry.playS2C().register(S2CPlayerDataInitializePayload.ID,
				S2CPlayerDataInitializePayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(S2CPlayerDataInitializePayload.ID,
				(payload, context) -> context.client()
						.execute(() -> handlePlayerDataInitializePayload(payload, context)));
	}
}
