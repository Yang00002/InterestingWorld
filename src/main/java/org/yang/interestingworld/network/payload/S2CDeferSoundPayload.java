package org.yang.interestingworld.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

public record S2CDeferSoundPayload(RegistryEntry<SoundEvent> sound, SoundCategory category, float x, float y, float z,
								   float volume, float pitch, int delay) implements CustomPayload
{
	public static final CustomPayload.Id<S2CDeferSoundPayload> ID = new CustomPayload.Id<>(
			Identifier.of(Base.MOD_ID, "defersound"));
	public static final PacketCodec<RegistryByteBuf, S2CDeferSoundPayload> CODEC = PacketCodec.of(
			S2CDeferSoundPayload::encode, S2CDeferSoundPayload::decode);

	private static void encode(S2CDeferSoundPayload payload, RegistryByteBuf buf)
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

	private static S2CDeferSoundPayload decode(RegistryByteBuf buf)
	{
		return new S2CDeferSoundPayload(SoundEvent.ENTRY_PACKET_CODEC.decode(buf),
				buf.readEnumConstant(SoundCategory.class), buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readInt());
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
