package org.yang.interestingworld.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

public record S2CItemBreakParticlePayload(ParticleEffect effect) implements CustomPayload
{
	public static final Id<S2CItemBreakParticlePayload> ID = new CustomPayload.Id<>(
			Identifier.of(Base.MOD_ID, "cooldown_sound"));
	public static final PacketCodec<RegistryByteBuf, S2CItemBreakParticlePayload> CODEC = PacketCodec.of(
			(S2CItemBreakParticlePayload payload, RegistryByteBuf buf) -> ParticleTypes.PACKET_CODEC.encode(buf,
					payload.effect),
			(RegistryByteBuf buf) -> new S2CItemBreakParticlePayload(ParticleTypes.PACKET_CODEC.decode(buf)));

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
