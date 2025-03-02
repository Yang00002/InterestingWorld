package org.yang.iw.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.yang.iw.IWSounds;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.entity.player.AbilityBarType;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.payload.S2CDeferSoundPayload;
import org.yang.iw.network.payload.S2CItemBreakParticlePayload;
import org.yang.iw.network.payload.S2CPlayerDataInitializePayload;
import org.yang.iw.network.payload.S2CPlayerDataPayload;

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

	private static void handlePlayerAbilityBarPayload(S2CPlayerDataPayload payload,
													  ClientPlayNetworking.Context context)
	{
		AbstractAbility ab = payload.ability();
		ClientPlayerEntity player = context.player();
		IWClientPlayerData manager = player.getIWClientPlayerData();
		BitReader clientData = payload.clientData();
		if (clientData.readBoolean()) manager.setEnergy(clientData.readUnsignedInt(5));
		if (ab == null || ab.isEmpty())
		{
			manager.WeaponAbility = AbstractAbility.getDefault();
			manager.setNotCooldown();
		}
		else
		{
			manager.setAbilityOn(payload.clientData().readBoolean());
			boolean tickOver = payload.clientData().readBoolean();
			boolean isCooldown = payload.clientData().readBoolean();
			if (tickOver) ab.onClientTickOver(player, manager);
			if (isCooldown) manager.setCooldown(payload.clientData().readUnsignedInt(5));
			else
			{
				if (manager.isCooldown() && manager.WeaponAbility == ab) player.playSound(IWSounds.ABILITY_BAR_FULL);
				manager.setNotCooldown();
				AbilityBarType type = AbilityBarType.values()[clientData.readUnsignedInt(AbilityBarType.radixs)];
				switch (type)
				{
					case TICK ->
					{
						var n = clientData.readUnsignedInt(5);
						manager.setTickStep(n);
					}
					case TICK_REVERSE ->
					{
						var n = clientData.readUnsignedInt(5);
						manager.setTickStep(16 - n);
					}
				}
				manager.setBarType(type);
				ab.readClientRenderDataFromBuf(player, manager, payload.clientData());
			}
			manager.WeaponAbility = ab;
		}
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
		PayloadTypeRegistry.playS2C().register(S2CPlayerDataPayload.ID, S2CPlayerDataPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(S2CPlayerDataPayload.ID,
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
