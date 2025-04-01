package org.yang.iw.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.item.IWItems;
import org.yang.iw.item.MaterialPacketItem;
import org.yang.iw.network.payload.C2SAbilityKeyPressPayload;
import org.yang.iw.network.payload.C2SMaterialPacketPayload;
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

	public static void handleMaterialPacketPayload(C2SMaterialPacketPayload payload,
												   ServerPlayNetworking.Context context)
	{
		var handler = context.player().currentScreenHandler;
		int slot = payload.slotIndex();
		int index = payload.index();
		if ((index >= 0 || (index == -1 && payload.shift())) && slot >= 0 && slot < handler.slots.size())
		{
			Slot slot1 = handler.slots.get(slot);
			ItemStack itemStack = slot1.getStack();
			if (!itemStack.isEmpty() && itemStack.getItem() == IWItems.MATERIAL_PACKET)
			{
				MaterialPacketItem.setIndex(itemStack, payload.shift(), index);
				slot1.inventory.markDirty();
			}
		}
	}

	static
	{
		PayloadTypeRegistry.playC2S().register(C2SAbilityKeyPressPayload.ID, C2SAbilityKeyPressPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SAbilityKeyPressPayload.ID,
				(payload, context) -> context.server()
				.execute(() -> handleAbilityOpenConditionChangedPayload(payload, context)));
		PayloadTypeRegistry.playC2S().register(C2SMaterialPacketPayload.ID, C2SMaterialPacketPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SMaterialPacketPayload.ID,
				(payload, context) -> context.server().execute(() -> handleMaterialPacketPayload(payload, context)));
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
