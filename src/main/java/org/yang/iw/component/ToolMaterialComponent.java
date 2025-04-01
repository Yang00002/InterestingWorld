package org.yang.iw.component;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.tool.material.ToolMaterial;

import java.util.List;

public record ToolMaterialComponent(ObjectArrayList<ToolMaterial> materials, boolean all_same)
{
	public static final ToolMaterialComponent DEFAULT = new ToolMaterialComponent(new ObjectArrayList<>(), true);
	public static final Codec<ToolMaterialComponent> CODEC = Codec.list(IWRegistries.TOOL_MATERIAL.getCodec())
			.xmap(ToolMaterialComponent::create, ToolMaterialComponent::materials);
	public static final PacketCodec<RegistryByteBuf, ToolMaterialComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.collection(ObjectArrayList::new, PacketCodecs.registryValue(IWRegistryKeys.TOOL_MATERIAL)),
			ToolMaterialComponent::materials, PacketCodecs.BOOLEAN, ToolMaterialComponent::all_same,
			ToolMaterialComponent::new);

	public static ToolMaterialComponent create(List<ToolMaterial> materials)
	{
		if (materials.isEmpty()) return DEFAULT;
		var l = new ObjectArrayList<>(materials);
		var first = l.getFirst();
		int size = l.size();
		for (int i = 1; i < size; i++)
			if (l.get(i) != first) return new ToolMaterialComponent(l, false);
		return new ToolMaterialComponent(l, true);
	}

	public ToolMaterial at(int index)
	{
		if (materials.isEmpty()) return ToolMaterial.getDefault();
		if (index < 0 || index >= materials.size()) return materials.getFirst();
		return materials.get(index);
	}

	public MutableText name(String suffix)
	{
		if (materials.isEmpty()) return Text.translatable(suffix);
		if (all_same) return Text.translatable(materials.getFirst().translateKey()).append(Text.translatable(suffix))
				.withColor(materials.getFirst().materialColor());
		MutableText text = Text.empty();
		for (int i = materials.size() - 1; i >= 0; i--)
			text.append(Text.translatable(materials.get(i).translateKey()).withColor(materials.get(i).materialColor()));
		text.append(Text.translatable(suffix).withColor(materials.getFirst().materialColor()));
		return text;
	}
}
