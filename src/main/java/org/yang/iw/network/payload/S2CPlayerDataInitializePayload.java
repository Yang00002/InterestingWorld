package org.yang.iw.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.bitio.BitWriter;
import org.yang.iw.util.Base;

public record S2CPlayerDataInitializePayload(IWServerPlayerData serverData,
											 BitReader clientData) implements CustomPayload
{
	public static final CustomPayload.Id<S2CPlayerDataInitializePayload> ID = new CustomPayload.Id<>(
			Identifier.of(Base.MOD_ID, "player_ini"));
	public static final PacketCodec<RegistryByteBuf, S2CPlayerDataInitializePayload> CODEC = PacketCodec.of(
			S2CPlayerDataInitializePayload::encode, S2CPlayerDataInitializePayload::decode);

	private static void encode(S2CPlayerDataInitializePayload payload, RegistryByteBuf buf)
	{
		var writer = new BitWriter(buf);
		payload.serverData.writeS2CInitializeDataToBuffer(writer);
		writer.close();
	}

	private static S2CPlayerDataInitializePayload decode(RegistryByteBuf buf)
	{
		var ret = new S2CPlayerDataInitializePayload(null, new BitReader(buf.copy()));
		buf.clear();
		return ret;
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
