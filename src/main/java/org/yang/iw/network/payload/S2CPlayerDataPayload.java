package org.yang.iw.network.payload;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistries;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.entity.player.AbilityBarType;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.bitio.BitWriter;
import org.yang.iw.util.Base;

public record S2CPlayerDataPayload(AbstractAbility ability, IWServerPlayerData serverData,
								   BitReader clientData) implements CustomPayload
{
	public static final Id<S2CPlayerDataPayload> ID = new CustomPayload.Id<>(Identifier.of(Base.MOD_ID, "player_data"
	));

	public static final PacketCodec<RegistryByteBuf, S2CPlayerDataPayload> CODEC = PacketCodec.of(
			S2CPlayerDataPayload::encode, S2CPlayerDataPayload::decode);

	private static void encode(S2CPlayerDataPayload payload, RegistryByteBuf buf)
	{
		buf.writeInt(IWRegistries.ABILITY.getRawId(payload.ability));
		var buffer = new BitWriter(buf);
		boolean sync_energy = payload.serverData.syncEnergy();
		buffer.writeBoolean(sync_energy);
		if (sync_energy) buffer.writeUnsignedInt(payload.serverData.getLastEnergy(), 5);
		if (!payload.ability.isEmpty())
		{
			buffer.writeBoolean(payload.serverData.isAbilityOn());
			buffer.writeBoolean(payload.serverData.tickOver());
			payload.serverData.setTickOver(false);
			var step = payload.serverData.coolDownStep(payload.ability);
			if (step == 16)
			{
				buffer.writeBoolean(false);
				var type = payload.serverData.abilityBarType();
				buffer.writeUnsignedInt(type.ordinal(), AbilityBarType.radixs);
				switch (type)
				{
					case TICK, TICK_REVERSE -> buffer.writeUnsignedInt(payload.serverData().getTickManagerStep(), 5);
				}
				payload.ability.writeClientRenderDataToBuf(payload.serverData, buffer);
			}
			else
			{
				buffer.writeBoolean(true);
				buffer.writeUnsignedInt(step, 5);
			}
		}
		buffer.close();
	}

	private static S2CPlayerDataPayload decode(RegistryByteBuf buf)
	{
		var ability = IWRegistries.ABILITY.get(buf.readInt());
		var ret = new S2CPlayerDataPayload(ability, null, new BitReader(buf.copy()));
		buf.clear();
		return ret;
	}

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}

