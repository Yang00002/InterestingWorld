package org.yang.interestingworld.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.yang.interestingworld.network.payload.C2SAbilityKeyPressPayload;
import org.yang.interestingworld.network.payload.S2CPlayerDataInitializePayload;

public class IWNetwork
{

	public static void handleAbilityOpenConditionChangedPayload(C2SAbilityKeyPressPayload payload,
																ServerPlayNetworking.Context context)
	{
		context.player().getIWServerPlayerData().changeAbilityOpenCondition(payload.isOn(), context.player());
	}

	public static void initialize()
	{
		PayloadTypeRegistry.playC2S().register(C2SAbilityKeyPressPayload.ID, C2SAbilityKeyPressPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SAbilityKeyPressPayload.ID,
				(payload, context) -> context.server()
				.execute(() -> handleAbilityOpenConditionChangedPayload(payload, context)));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> {
			ServerPlayerEntity player = handler.getPlayer();
			ServerPlayNetworking.send(player, new S2CPlayerDataInitializePayload(player.getIWServerPlayerData(),
					null));
		}));
	}
}
