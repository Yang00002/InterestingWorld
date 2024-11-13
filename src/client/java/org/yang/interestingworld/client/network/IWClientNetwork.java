package org.yang.interestingworld.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.yang.interestingworld.network.IWNetwork;
import org.yang.interestingworld.network.IWNetwork.ItemBreakParticlePayload;
import org.yang.interestingworld.playerenergymanager.PlayerEnergyAccessor;
import org.yang.interestingworld.playerenergymanager.PlayerEnergyManager;

public class IWClientNetwork
{

	private static void handleItemBreakParticlePayload(ItemBreakParticlePayload payload,
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

	private static void handlePlayerEnergyPayload(IWNetwork.PlayerEnergyPayload payload,
												  ClientPlayNetworking.Context context)
	{
		ClientPlayerEntity player = context.player();
		PlayerEnergyManager manager = ((PlayerEnergyAccessor) player).getEnergyManager();
		manager.setShownEnergy(payload.energy());
	}

	public static void initialize()
	{
		PayloadTypeRegistry.playS2C().register(ItemBreakParticlePayload.ID, ItemBreakParticlePayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ItemBreakParticlePayload.ID,
				(payload, context) -> context.client().execute(() -> handleItemBreakParticlePayload(payload,
						context)));
		PayloadTypeRegistry.playS2C().register(IWNetwork.PlayerEnergyPayload.ID, IWNetwork.PlayerEnergyPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(IWNetwork.PlayerEnergyPayload.ID,
				(payload, context) -> context.client().execute(() -> handlePlayerEnergyPayload(payload, context)));
	}
}
