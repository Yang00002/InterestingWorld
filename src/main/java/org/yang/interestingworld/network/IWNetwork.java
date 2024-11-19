package org.yang.interestingworld.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataManager;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;

import static org.yang.interestingworld.IWUtil.RuneAbility.getAbility;


public class IWNetwork
{
	public record ItemBreakParticlePayload(ParticleEffect effect) implements CustomPayload
	{
		public static final Id<ItemBreakParticlePayload> ID = new CustomPayload.Id<>(
				Identifier.of(IWUtil.Base.MOD_ID, "cooldown_sound"));
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

	public record PlayerEnergyPayload(int energy) implements CustomPayload
	{
		public static final Id<PlayerEnergyPayload> ID = new CustomPayload.Id<>(
				Identifier.of(IWUtil.Base.MOD_ID, "player_energy"));
		public static final PacketCodec<RegistryByteBuf, PlayerEnergyPayload> CODEC = PacketCodec.of(
				(PlayerEnergyPayload payload, RegistryByteBuf buf) -> buf.writeInt(payload.energy),
				(RegistryByteBuf buf) -> new PlayerEnergyPayload(buf.readInt()));

		@Override
		public Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}

	public record AbilityPayload(IWAbstractRuneAbility ability, ServerPlayerDataManager serverData,
								 ByteBuf clientData) implements CustomPayload
	{
		public static final Id<AbilityPayload> ID = new CustomPayload.Id<>(
				Identifier.of(IWUtil.Base.MOD_ID, "ability"));

		public static final PacketCodec<RegistryByteBuf, AbilityPayload> CODEC = PacketCodec.of(AbilityPayload::encode,
				AbilityPayload::decode);

		private static void encode(AbilityPayload payload, RegistryByteBuf buf)
		{
			buf.writeShort(payload.ability.index);
			payload.ability.writeClientRenderDataToBuf(payload.serverData, buf);
		}

		private static AbilityPayload decode(RegistryByteBuf buf)
		{
			var ret = new AbilityPayload(getAbility(buf.readShort()), null, buf.copy());
			buf.clear();
			return ret;
		}

		@Override
		public Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}

}
