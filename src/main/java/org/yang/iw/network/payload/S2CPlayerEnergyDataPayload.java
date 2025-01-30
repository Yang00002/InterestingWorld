package org.yang.iw.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;

public record S2CPlayerEnergyDataPayload(int energy) implements CustomPayload
{
	public static final Id<S2CPlayerEnergyDataPayload> ID = new CustomPayload.Id<>(
			Identifier.of(Base.MOD_ID, "player_energy"));
	public static final PacketCodec<RegistryByteBuf, S2CPlayerEnergyDataPayload> CODEC = PacketCodec.of(
			(S2CPlayerEnergyDataPayload payload, RegistryByteBuf buf) -> buf.writeInt(payload.energy),
			(RegistryByteBuf buf) -> new S2CPlayerEnergyDataPayload(buf.readInt()));

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
