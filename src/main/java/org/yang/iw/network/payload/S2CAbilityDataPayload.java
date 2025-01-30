package org.yang.iw.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.util.Base;

import static org.yang.iw.util.IWRuneAbilityUtil.getAbility;

public record S2CAbilityDataPayload(AbstractRuneAbility ability, IWServerPlayerData serverData,
									ByteBuf clientData) implements CustomPayload
{
	public static final Id<S2CAbilityDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Base.MOD_ID, "ability"));

	public static final PacketCodec<RegistryByteBuf, S2CAbilityDataPayload> CODEC = PacketCodec.of(
			S2CAbilityDataPayload::encode,
			S2CAbilityDataPayload::decode);

	private static void encode(S2CAbilityDataPayload payload, RegistryByteBuf buf)
	{
		buf.writeShort(payload.ability.index);
		payload.ability.writeClientRenderDataToBuf(payload.serverData, buf);
	}

	private static S2CAbilityDataPayload decode(RegistryByteBuf buf)
	{
		var ret = new S2CAbilityDataPayload(getAbility(buf.readShort()), null, buf.copy());
		buf.clear();
		return ret;
	}

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
