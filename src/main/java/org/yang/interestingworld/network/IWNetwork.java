package org.yang.interestingworld.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWUtil;


public class IWNetwork
{

	private static final Identifier ItemBreakParticlePayload_ID = Identifier.of(IWUtil.Base.MOD_ID, "cooldown_sound");

	public record ItemBreakParticlePayload(ParticleEffect effect) implements CustomPayload
	{
		public static final Id<ItemBreakParticlePayload> ID = new CustomPayload.Id<>(ItemBreakParticlePayload_ID);
		public static final PacketCodec<RegistryByteBuf, ItemBreakParticlePayload> CODEC = PacketCodec.of(
				(ItemBreakParticlePayload payload, RegistryByteBuf buf) -> ParticleTypes.PACKET_CODEC.encode(buf,
						payload.effect),
				(RegistryByteBuf buf) -> new ItemBreakParticlePayload(ParticleTypes.PACKET_CODEC.decode(buf)));

		@Override
		public Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}

	private static final Identifier PlayerEnergyPayload_ID = Identifier.of(IWUtil.Base.MOD_ID, "player_energy");

	public record PlayerEnergyPayload(int energy) implements CustomPayload
	{
		public static final Id<PlayerEnergyPayload> ID = new CustomPayload.Id<>(PlayerEnergyPayload_ID);
		public static final PacketCodec<RegistryByteBuf, PlayerEnergyPayload> CODEC = PacketCodec.of(
				(PlayerEnergyPayload payload, RegistryByteBuf buf) -> buf.writeInt(payload.energy),
				(RegistryByteBuf buf) -> new PlayerEnergyPayload(buf.readInt()));

		@Override
		public Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}

}
