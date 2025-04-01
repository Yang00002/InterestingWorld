package org.yang.iw.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;

import static org.yang.iw.util.Base.iwlogger;

public record C2SMaterialPacketPayload(int index, int slotIndex, boolean shift) implements CustomPayload
{
	public static final Id<C2SMaterialPacketPayload> ID = new Id<>(Identifier.of(Base.MOD_ID, "material_packet"));
	public static final PacketCodec<RegistryByteBuf, C2SMaterialPacketPayload> CODEC = PacketCodec.of(
			(C2SMaterialPacketPayload payload, RegistryByteBuf buf) -> {
				if (payload.index == -1 && payload.shift) buf.writeInt(-1);
				else buf.writeInt((payload.index << 1) | (payload.shift ? 1 : 0));
				buf.writeInt(payload.slotIndex);
			}, (RegistryByteBuf buf) -> {
				int v = buf.readInt();
				int idx = v >> 1;
				boolean shift = (v & 1) == 1;
				if (shift && v == -1) idx = -1;
				return new C2SMaterialPacketPayload(idx, buf.readInt(), shift);
			});

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
