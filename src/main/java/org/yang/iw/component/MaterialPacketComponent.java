package org.yang.iw.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.yang.iw.block.forgingblock.ForgingBlockScreen;
import org.yang.iw.item.IWItems;
import org.yang.iw.resource.MaterialProvider;
import org.yang.iw.resource.MaterialVEntry;
import org.yang.iw.tool.material.ToolMaterial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MaterialPacketComponent
{
	final ArrayList<MaterialVEntry> value;
	final ArrayList<MaterialVEntry> overlay;
	final int valueIdx;
	final int overlayIdx;
	final int size;

	private MaterialPacketComponent(ArrayList<MaterialVEntry> value, ArrayList<MaterialVEntry> overlay, int valueIdx,
									int overlayIdx, int size)
	{
		this.value = value;
		this.overlay = overlay;
		this.valueIdx = valueIdx;
		this.overlayIdx = overlayIdx;
		this.size = size;
	}

	private MaterialPacketComponent()
	{
		this.value = new ArrayList<>();
		this.overlay = new ArrayList<>();
		this.valueIdx = -1;
		this.overlayIdx = -1;
		this.size = 0;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MaterialPacketComponent that = (MaterialPacketComponent) o;
		return valueIdx == that.valueIdx && overlayIdx == that.overlayIdx && size == that.size;
	}

	public static final MaterialPacketComponent DEFAULT = new MaterialPacketComponent();
	public static final Codec<MaterialPacketComponent> SHORT_CODEC = Codec.list(MaterialVEntry.CODEC)
			.xmap(MaterialPacketComponent::fromList, MaterialPacketComponent::toList);
	private static final Codec<MaterialPacketComponent> FULL_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Codec.list(MaterialVEntry.CODEC).fieldOf("value")
									.forGetter(MaterialPacketComponent::mergeValueList),
							Codec.INT.fieldOf("index").forGetter(component -> component.valueIdx),
							Codec.INT.fieldOf("overlay_index").forGetter(component -> component.overlayIdx))
					.apply(instance, MaterialPacketComponent::fromFullCodec));

	public static final Codec<MaterialPacketComponent> CODEC = Codec.withAlternative(FULL_CODEC, SHORT_CODEC);

	public static final PacketCodec<RegistryByteBuf, MaterialPacketComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.collection(ArrayList::new, MaterialVEntry.PACKET_CODEC), c -> c.value,
			PacketCodecs.collection(ArrayList::new, MaterialVEntry.PACKET_CODEC), c -> c.overlay, PacketCodecs.INTEGER,
			c -> c.valueIdx, PacketCodecs.INTEGER, c -> c.overlayIdx, MaterialPacketComponent::fromPacketCodec);

	private static MaterialPacketComponent fromList(List<MaterialVEntry> list)
	{
		Object2IntLinkedOpenHashMap<ToolMaterial> value = new Object2IntLinkedOpenHashMap<>();
		Object2IntLinkedOpenHashMap<ToolMaterial> overlay = new Object2IntLinkedOpenHashMap<>();
		MutableInt size = new MutableInt(0);
		list.forEach(e -> {
			if (e.value() > 0)
			{
				if (e.material().isOverlay())
					overlay.putAndMoveToLast(e.material(), overlay.getOrDefault(e.material(), 0) + e.value());
				else value.putAndMoveToLast(e.material(), overlay.getOrDefault(e.material(), 0) + e.value());
				size.add(e.value());
			}
		});
		if (value.isEmpty() && overlay.isEmpty()) return DEFAULT;
		ArrayList<MaterialVEntry> valueList = new ArrayList<>(value.size());
		ArrayList<MaterialVEntry> overlayList = new ArrayList<>(overlay.size());
		value.forEach((k, v) -> valueList.add(new MaterialVEntry(k, v)));
		overlay.forEach((k, v) -> overlayList.add(new MaterialVEntry(k, v)));
		return new MaterialPacketComponent(valueList, overlayList, valueList.isEmpty() ? -1 : 0, -1, size.getValue());
	}

	private List<MaterialVEntry> mergeValueList()
	{
		var ret = new ArrayList<>(value);
		ret.addAll(overlay);
		return ret;
	}

	private static MaterialPacketComponent fromFullCodec(List<MaterialVEntry> list, int idx, int odx)
	{
		Object2IntLinkedOpenHashMap<ToolMaterial> value = new Object2IntLinkedOpenHashMap<>();
		Object2IntLinkedOpenHashMap<ToolMaterial> overlay = new Object2IntLinkedOpenHashMap<>();
		MutableInt size = new MutableInt(0);
		list.forEach(e -> {
			if (e.value() > 0)
			{
				if (e.material().isOverlay())
					overlay.putAndMoveToLast(e.material(), overlay.getOrDefault(e.material(), 0) + e.value());
				else value.putAndMoveToLast(e.material(), overlay.getOrDefault(e.material(), 0) + e.value());
				size.add(e.value());
			}
		});
		if (value.isEmpty() && overlay.isEmpty()) return DEFAULT;
		ArrayList<MaterialVEntry> valueList = new ArrayList<>(value.size());
		ArrayList<MaterialVEntry> overlayList = new ArrayList<>(overlay.size());
		value.forEach((k, v) -> valueList.add(new MaterialVEntry(k, v)));
		overlay.forEach((k, v) -> overlayList.add(new MaterialVEntry(k, v)));
		if (value.isEmpty()) idx = -1;
		else idx = Math.clamp(idx, 0, value.size() - 1);
		if (overlay.isEmpty()) odx = -1;
		else odx = Math.clamp(odx, -1, overlay.size() - 1);
		return new MaterialPacketComponent(valueList, overlayList, idx, odx, size.getValue());
	}

	private static MaterialPacketComponent fromPacketCodec(ArrayList<MaterialVEntry> v, ArrayList<MaterialVEntry> o,
														   int idx, int odx)
	{
		int size = 0;
		for (var i : v)
			size += i.value();
		for (var i : o)
			size += i.value();
		return new MaterialPacketComponent(v, o, idx, odx, size);
	}

	private static List<MaterialVEntry> toList(MaterialPacketComponent component)
	{
		LinkedList<MaterialVEntry> list = new LinkedList<>(component.value);
		list.addAll(component.overlay);
		return list;
	}

	public MaterialVEntry provideMaterial(boolean isOverlay)
	{
		if (isOverlay)
		{
			if (overlayIdx < 0 || overlayIdx >= overlay.size()) return null;
			return overlay.get(overlayIdx);
		}
		return provideMaterial();
	}

	public MaterialVEntry provideMaterial()
	{
		if (valueIdx < 0 || valueIdx >= value.size()) return null;
		return value.get(valueIdx);
	}

	public MaterialPacketComponent takeMaterial(ToolMaterial material, int amount)
	{
		ArrayList<MaterialVEntry> list = new ArrayList<>(value);
		int valueIdx2 = valueIdx;
		int l = list.size();
		for (int i = 0; i < l; i++)
		{
			var e = list.get(i);
			if (e.material() == material)
			{
				if (e.value() > amount)
				{
					list.set(i, new MaterialVEntry(material, e.value() - amount));
					return new MaterialPacketComponent(list, overlay, valueIdx, overlayIdx, size - amount);
				}
				if (valueIdx2 >= i) valueIdx2--;
				list.remove(i);
				return new MaterialPacketComponent(list, overlay, valueIdx2, overlayIdx, size - e.value());
			}
		}
		return this;
	}

	public boolean isEmpty()
	{
		return size == 0;
	}

	public int size(boolean isOverlay)
	{
		return isOverlay ? overlay.size() : value.size();
	}

	public MaterialPacketComponent setIndex(boolean shift, int idx)
	{
		if (shift)
		{
			if (idx < -1 || idx == overlayIdx || idx >= overlay.size()) return this;
			return new MaterialPacketComponent(value, overlay, valueIdx, idx, size);
		}
		else
		{
			if (idx < 0 || idx == valueIdx || idx >= value.size()) return this;
			return new MaterialPacketComponent(value, overlay, idx, overlayIdx, size);
		}
	}

	public int getIndex(boolean shift)
	{
		return shift ? overlayIdx : valueIdx;
	}

	public void appendToolTip(List<Text> tooltip)
	{
		int idx = 0;
		for (var entry : value)
		{
			var material = entry.material();
			var value = entry.value();
			tooltip.add(ForgingBlockScreen.appendMaterialWorthText(idx == valueIdx ? Text.literal("> ") : Text.empty(),
					material, value));
			idx++;
		}
		tooltip.add(Text.empty());
		idx = 0;
		for (var entry : overlay)
		{
			var material = entry.material();
			var value = entry.value();
			tooltip.add(
					ForgingBlockScreen.appendMaterialWorthText(idx == overlayIdx ? Text.literal("> ") : Text.empty(),
							material, value));
			idx++;
		}
	}

	public void appendToolTip(List<Text> tooltip, int taking1, int taking2)
	{
		int idx = 0;
		for (var entry : value)
		{
			var material = entry.material();
			var value = entry.value();
			tooltip.add(ForgingBlockScreen.appendMaterialWorthText(idx == valueIdx ? Text.literal("> ") : Text.empty(),
					material, value, taking1, idx == valueIdx));
			idx++;
		}
		tooltip.add(Text.empty());
		idx = 0;
		for (var entry : overlay)
		{
			var material = entry.material();
			var value = entry.value();
			tooltip.add(
					ForgingBlockScreen.appendMaterialWorthText(idx == overlayIdx ? Text.literal("> ") : Text.empty(),
							material, value, taking2, idx == overlayIdx));
			idx++;
		}
	}

	public boolean insertCommonItem(ItemStack stack, ItemStack to)
	{
		if (stack.isEmpty() || stack.getItem() == IWItems.MATERIAL_PACKET) return false;
		MaterialVEntry entry = MaterialProvider.get(stack);
		if (entry == null) return false;
		int provide = entry.value() * stack.getCount();
		return insertMaterialVEntry(new MaterialVEntry(entry.material(), provide), to, false) > 0;
	}

	public boolean insertMaterialPacket(ItemStack from, ItemStack to, boolean overlay)
	{
		if (from.isEmpty() || from.getItem() != IWItems.MATERIAL_PACKET) return false;
		MaterialPacketComponent component = from.getOrDefault(IWComponents.MATERIAL_PACKET, DEFAULT);
		if (component.isEmpty()) return false;
		if (overlay)
		{
			if (component.overlay.isEmpty() || component.overlayIdx == -1) return false;
			int vdx = Math.clamp(component.overlayIdx, 0, component.overlay.size() - 1);
			MaterialVEntry v = component.overlay.get(vdx);
			int ret = insertMaterialVEntry(component.overlay.getFirst(), to, true);
			if (ret == 0) return false;
			int overlayIdx1 = component.overlayIdx >= vdx ? component.overlayIdx - 1 : component.overlayIdx;
			if (component.overlay.size() > 1 && component.overlayIdx == 0) overlayIdx1 = 0;
			if (ret == 1)
			{
				var l = new ArrayList<>(component.overlay);
				l.remove(vdx);
				from.set(IWComponents.MATERIAL_PACKET,
						new MaterialPacketComponent(component.value, l, component.valueIdx, overlayIdx1,
								component.size - v.value()));
				return true;
			}
			var l = new ArrayList<>(component.overlay);
			int size0 = v.value();
			int size1 = size0 >> 1;
			if (size1 == 0)
			{
				l.remove(vdx);
				from.set(IWComponents.MATERIAL_PACKET,
						new MaterialPacketComponent(component.value, l, component.valueIdx, overlayIdx1,
								component.size - size0));
			}
			else
			{
				l.set(0, new MaterialVEntry(v.material(), size1));
				from.set(IWComponents.MATERIAL_PACKET,
						new MaterialPacketComponent(component.value, l, component.valueIdx, component.overlayIdx,
								component.size - (size0 - size1)));
			}
		}
		else
		{
			if (component.value.isEmpty()) return false;
			int vdx = Math.clamp(component.valueIdx, 0, component.value.size() - 1);
			MaterialVEntry v = component.value.get(vdx);
			int ret = insertMaterialVEntry(v, to, true);
			if (ret == 0) return false;
			int valueIdx1 = component.valueIdx >= vdx ? component.valueIdx - 1 : component.valueIdx;
			if (component.value.size() > 1 && component.valueIdx == 0) valueIdx1 = 0;
			if (ret == 1)
			{
				var l = new ArrayList<>(component.value);
				l.remove(vdx);
				from.set(IWComponents.MATERIAL_PACKET,
						new MaterialPacketComponent(l, component.overlay, valueIdx1, component.overlayIdx,
								component.size - v.value()));
				return true;
			}
			var l = new ArrayList<>(component.value);
			int size0 = v.value();
			int size1 = size0 >> 1;
			if (size1 == 0)
			{
				l.remove(vdx);
				from.set(IWComponents.MATERIAL_PACKET,
						new MaterialPacketComponent(l, component.overlay, valueIdx1, component.overlayIdx,
								component.size - size0));
			}
			else
			{
				l.set(vdx, new MaterialVEntry(v.material(), size1));
				from.set(IWComponents.MATERIAL_PACKET,
						new MaterialPacketComponent(l, component.overlay, component.valueIdx, component.overlayIdx,
								component.size - (size0 - size1)));
			}
		}
		return true;
	}

	private int insertMaterialVEntry(@NotNull MaterialVEntry entry, ItemStack to, boolean takeHalfIfNotHave)
	{
		int provide = entry.value();
		if (provide <= 0) return 0;
		boolean isOverlay = entry.material().isOverlay();
		int provideHalf = takeHalfIfNotHave ? (((provide & 1) > 0) ? (provide >> 1 + 1) : (provide >> 1)) : provide;
		int len;
		if (isOverlay)
		{
			len = overlay.size();
			for (int i = 0; i < len; i++)
			{
				MaterialVEntry entry1 = overlay.get(i);
				if (entry1.material() == entry.material())
				{
					int next = entry1.value() + provide;
					if (next <= entry1.value() || next < provide) return 0;
					var l = new ArrayList<>(overlay);
					l.set(i, new MaterialVEntry(entry.material(), next));
					to.set(IWComponents.MATERIAL_PACKET,
							new MaterialPacketComponent(value, l, valueIdx, i, size + provide));
					return 1;
				}
			}
			provide = provideHalf;
			if (provide == 0) return 0;
			var l = new ArrayList<>(overlay);
			l.add(new MaterialVEntry(entry.material(), provide));
			to.set(IWComponents.MATERIAL_PACKET,
					new MaterialPacketComponent(value, l, valueIdx, l.size() - 1, size + provide));
		}
		else
		{
			len = value.size();
			for (int i = 0; i < len; i++)
			{
				MaterialVEntry entry1 = value.get(i);
				if (entry1.material() == entry.material())
				{
					int next = entry1.value() + provide;
					if (next <= entry1.value() || next < provide) return 0;
					var l = new ArrayList<>(value);
					l.set(i, new MaterialVEntry(entry.material(), next));
					to.set(IWComponents.MATERIAL_PACKET,
							new MaterialPacketComponent(l, overlay, i, overlayIdx, size + provide));
					return 1;
				}
			}
			provide = provideHalf;
			if (provide == 0) return 0;
			var l = new ArrayList<>(value);
			l.add(new MaterialVEntry(entry.material(), provide));
			to.set(IWComponents.MATERIAL_PACKET,
					new MaterialPacketComponent(l, overlay, l.size() - 1, overlayIdx, size + provide));
		}
		return takeHalfIfNotHave ? 2 : 1;
	}
}
