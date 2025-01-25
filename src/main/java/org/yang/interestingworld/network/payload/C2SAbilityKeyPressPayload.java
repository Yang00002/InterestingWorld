package org.yang.interestingworld.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

public record C2SAbilityKeyPressPayload(boolean isOn) implements CustomPayload
{
	public static final Id<C2SAbilityKeyPressPayload> ID = new CustomPayload.Id<>(
			Identifier.of(Base.MOD_ID, "ability_press"));
	public static final PacketCodec<RegistryByteBuf, C2SAbilityKeyPressPayload> CODEC = PacketCodec.of(
			(C2SAbilityKeyPressPayload payload, RegistryByteBuf buf) -> buf.writeBoolean(payload.isOn),
			(RegistryByteBuf buf) -> new C2SAbilityKeyPressPayload(buf.readBoolean()));

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
