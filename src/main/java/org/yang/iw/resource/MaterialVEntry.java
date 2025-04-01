package org.yang.iw.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.tool.material.ToolMaterial;

public record MaterialVEntry(ToolMaterial material, int value)
{
	public static final Codec<MaterialVEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IWRegistries.TOOL_MATERIAL.getCodec().fieldOf("material").forGetter(entry -> entry.material),
			Codec.INT.fieldOf("value").forGetter(entry -> entry.value)).apply(instance, MaterialVEntry::new));
	public static final PacketCodec<RegistryByteBuf, MaterialVEntry> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.registryValue(IWRegistryKeys.TOOL_MATERIAL), MaterialVEntry::material, PacketCodecs.INTEGER,
			MaterialVEntry::value, MaterialVEntry::new);
}