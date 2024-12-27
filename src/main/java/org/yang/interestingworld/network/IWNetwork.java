package org.yang.interestingworld.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.entity.player.IWServerPlayerData;
import org.yang.interestingworld.rune_ability.AbstractRuneAbility;
import org.yang.interestingworld.util.Base;

import static org.yang.interestingworld.util.IWRuneAbilityUtil.getAbility;


public class IWNetwork
{
	public record ItemBreakParticlePayload(ParticleEffect effect) implements CustomPayload
	{
		public static final Id<ItemBreakParticlePayload> ID = new CustomPayload.Id<>(
				Identifier.of(Base.MOD_ID, "cooldown_sound"));
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
				Identifier.of(Base.MOD_ID, "player_energy"));
		public static final PacketCodec<RegistryByteBuf, PlayerEnergyPayload> CODEC = PacketCodec.of(
				(PlayerEnergyPayload payload, RegistryByteBuf buf) -> buf.writeInt(payload.energy),
				(RegistryByteBuf buf) -> new PlayerEnergyPayload(buf.readInt()));

		@Override
		public Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}

	public record AbilityPayload(AbstractRuneAbility ability, IWServerPlayerData serverData,
								 ByteBuf clientData) implements CustomPayload
	{
		public static final Id<AbilityPayload> ID = new CustomPayload.Id<>(
				Identifier.of(Base.MOD_ID, "ability"));

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

	public record DeferSoundPayload(RegistryEntry<SoundEvent> sound, SoundCategory category, float x, float y, float z,
									float volume, float pitch, int delay) implements CustomPayload
	{
		public static final CustomPayload.Id<DeferSoundPayload> ID = new CustomPayload.Id<>(
				Identifier.of(Base.MOD_ID, "defersound"));
		public static final PacketCodec<RegistryByteBuf, DeferSoundPayload> CODEC = PacketCodec.of(
				DeferSoundPayload::encode, DeferSoundPayload::decode);

		private static void encode(DeferSoundPayload payload, RegistryByteBuf buf)
		{
			SoundEvent.ENTRY_PACKET_CODEC.encode(buf, payload.sound);
			buf.writeEnumConstant(payload.category);
			buf.writeFloat(payload.x);
			buf.writeFloat(payload.y);
			buf.writeFloat(payload.z);
			buf.writeFloat(payload.volume);
			buf.writeFloat(payload.pitch);
			buf.writeInt(payload.delay);
		}

		private static DeferSoundPayload decode(RegistryByteBuf buf)
		{
			return new DeferSoundPayload(SoundEvent.ENTRY_PACKET_CODEC.decode(buf),
					buf.readEnumConstant(SoundCategory.class), buf.readFloat(), buf.readFloat(), buf.readFloat(),
					buf.readFloat(), buf.readFloat(), buf.readInt());
		}

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId()
		{
			return ID;
		}
	}
}
