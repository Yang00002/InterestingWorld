package org.yang.iw.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.network.payload.C2SAbilityKeyPressPayload;
import org.yang.iw.network.payload.S2CPlayerDataInitializePayload;

@IndependentRegister
public class IWNetwork
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
	}

	public static void handleAbilityOpenConditionChangedPayload(C2SAbilityKeyPressPayload payload,
																ServerPlayNetworking.Context context)
	{
		context.player().interestingWorld$getIWServerPlayerData()
				.changeAbilityOpenCondition(payload.isOn(), context.player());
	}

	static
	{
		PayloadTypeRegistry.playC2S().register(C2SAbilityKeyPressPayload.ID, C2SAbilityKeyPressPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SAbilityKeyPressPayload.ID,
				(payload, context) -> context.server()
				.execute(() -> handleAbilityOpenConditionChangedPayload(payload, context)));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> {
			ServerPlayerEntity player = handler.getPlayer();
			ServerPlayNetworking.send(player,
					new S2CPlayerDataInitializePayload(player.interestingWorld$getIWServerPlayerData(), null));
		}));
	}

	public static void initialize()
	{
	}
}
