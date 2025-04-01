package org.yang.iw.component;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.block.forgingblock.ForgingBlockScreen;
import org.yang.iw.item.IWItems;
import org.yang.iw.resource.MaterialProvider;
import org.yang.iw.resource.MaterialVEntry;
import org.yang.iw.tool.material.ToolMaterial;

import java.util.List;
import java.util.Map;

public record RepairPacketComponent(Object2IntLinkedOpenHashMap<ToolMaterial> value, int size)
{
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RepairPacketComponent that = (RepairPacketComponent) o;
		boolean same = that.size == size;
		if (same && size == 0) return true;
		return same && that.value.firstKey() == value.firstKey();
	}

	public static final RepairPacketComponent DEFAULT = new RepairPacketComponent(new Object2IntLinkedOpenHashMap<>(),
			0);

	public static final Codec<RepairPacketComponent> CODEC = Codec.list(MaterialVEntry.CODEC)
			.xmap(RepairPacketComponent::fromList, RepairPacketComponent::toList);

	public static final PacketCodec<RegistryByteBuf, RepairPacketComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.map(Object2IntLinkedOpenHashMap::new,
					PacketCodecs.registryValue(IWRegistryKeys.TOOL_MATERIAL),
					PacketCodecs.INTEGER), RepairPacketComponent::value, RepairPacketComponent::createFromMap);

	public static class Builder
	{
		Object2IntLinkedOpenHashMap<ToolMaterial> value = new Object2IntLinkedOpenHashMap<>();
		int size = 0;

		public RepairPacketComponent build()
		{
			return new RepairPacketComponent(value, size);
		}

		public Builder add(ToolMaterial material, int count)
		{
			if (count > 0)
			{
				size += count;
				value.put(material, count + value.getOrDefault(material, 0));
			}
			return this;
		}
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static RepairPacketComponent createFromMap(Object2IntLinkedOpenHashMap<ToolMaterial> map)
	{
		int size = 0;
		for (var e : map.values())
			size += e;
		return new RepairPacketComponent(map, size);
	}

	private static RepairPacketComponent fromList(List<MaterialVEntry> list)
	{
		Object2IntLinkedOpenHashMap<ToolMaterial> map = new Object2IntLinkedOpenHashMap<>();
		list.forEach(e -> {
			if (e.value() > 0) map.putAndMoveToLast(e.material(), e.value());
		});
		if (map.isEmpty()) return DEFAULT;
		int size = 0;
		for (var e : map.values())
			size += e;
		return new RepairPacketComponent(map, size);
	}

	private static List<MaterialVEntry> toList(RepairPacketComponent component)
	{
		List<MaterialVEntry> list = new ObjectArrayList<>(component.value.size());
		component.value.forEach((i, j) -> list.addLast(new MaterialVEntry(i, j)));
		return list;
	}

	public void appendHalfToolTip(List<Text> tooltip, float amount)
	{
		for (var entry : value.object2IntEntrySet())
		{
			var material = entry.getKey();
			var value = entry.getIntValue();
			tooltip.add(ForgingBlockScreen.appendMaterialWorthText(Text.empty(), material,
					Math.round(value * amount)));
		}
	}

	public void appendToolTip(List<Text> tooltip)
	{
		for (var entry : value.object2IntEntrySet())
		{
			var material = entry.getKey();
			var value = entry.getIntValue();
			tooltip.add(ForgingBlockScreen.appendMaterialWorthText(Text.empty(), material, value));
		}
	}

	public boolean isEmpty()
	{
		return value.isEmpty();
	}

	public float canRepairWithInventory(Inventory inventory)
	{
		if (size == 0) return 1;
		Map<ToolMaterial, Integer> map = new Object2ObjectOpenHashMap<>();
		for (int i = 0; i < 9; i++)
		{
			var stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == IWItems.MATERIAL_PACKET)
			{
				stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).value.forEach(
						entry -> {
							if (value.containsKey(entry.material()))
								map.put(entry.material(), map.getOrDefault(entry.material(), 0) + entry.value());
						});
				stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).overlay.forEach(
						entry -> {
							if (value.containsKey(entry.material()))
								map.put(entry.material(), map.getOrDefault(entry.material(), 0) + entry.value());
						});
			}
			else
			{
				var pair = MaterialProvider.get(stack);
				if (pair == null) continue;
				var material = pair.material();
				if (value.containsKey(material))
					map.put(material, map.getOrDefault(material, 0) + pair.value() * stack.getCount());
			}
		}
		float amount = 0;
		for (var entry : value.object2IntEntrySet())
		{
			var key = entry.getKey();
			var v = entry.getIntValue();
			int have = map.getOrDefault(key, 0);
			if (have == 0) return 0;
			float next = ((float) have) / v;
			if (amount < next) amount = next;
		}
		return amount;
	}

	public boolean canUseForRepair(ItemStack stack)
	{
		if (size == 0) return false;
		if (stack.isEmpty()) return false;
		if (stack.getItem() == IWItems.MATERIAL_PACKET)
		{
			for (var material :
					stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).value)
				if (value.containsKey(material.material())) return true;
			for (var material : stack.getOrDefault(IWComponents.MATERIAL_PACKET,
					MaterialPacketComponent.DEFAULT).overlay)
				if (value.containsKey(material.material())) return true;
		}
		var pair = MaterialProvider.get(stack);
		if (pair == null) return false;
		return value.containsKey(pair.material());
	}

	public void repairWithInventory(Inventory inventory, float frac)
	{
		if (size == 0) return;
		Map<ToolMaterial, Integer> map = new Object2ObjectOpenHashMap<>();
		for (var entry : value.object2IntEntrySet())
		{
			var key = entry.getKey();
			var v = entry.getIntValue();
			int n = Math.round(v * frac);
			map.put(key, n);
		}
		for (int i = 0; i < 9; i++)
		{
			var stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == IWItems.MATERIAL_PACKET)
			{
				stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).value.forEach(
						entry -> {
							var material = entry.material();
							var v = entry.value();
							var pre = map.getOrDefault(material, 0);
							if (pre > v)
							{
								map.put(material, v - pre);
								stack.set(IWComponents.MATERIAL_PACKET,
										stack.getOrDefault(IWComponents.MATERIAL_PACKET,
										MaterialPacketComponent.DEFAULT).takeMaterial(material, v));
							}
							else if (pre == v)
							{
								map.remove(material);
								stack.set(IWComponents.MATERIAL_PACKET,
										stack.getOrDefault(IWComponents.MATERIAL_PACKET,
										MaterialPacketComponent.DEFAULT).takeMaterial(material, v));
							}
							else
							{
								map.remove(material);
								stack.set(IWComponents.MATERIAL_PACKET,
										stack.getOrDefault(IWComponents.MATERIAL_PACKET,
										MaterialPacketComponent.DEFAULT).takeMaterial(material, pre));
							}
						});
				stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).overlay.forEach(
						entry -> {
							var material = entry.material();
							var v = entry.value();
							var pre = map.getOrDefault(material, 0);
							if (pre > v)
							{
								map.put(material, v - pre);
								stack.set(IWComponents.MATERIAL_PACKET,
										stack.getOrDefault(IWComponents.MATERIAL_PACKET,
										MaterialPacketComponent.DEFAULT).takeMaterial(material, v));
							}
							else if (pre == v)
							{
								map.remove(material);
								stack.set(IWComponents.MATERIAL_PACKET,
										stack.getOrDefault(IWComponents.MATERIAL_PACKET,
										MaterialPacketComponent.DEFAULT).takeMaterial(material, v));
							}
							else
							{
								map.remove(material);
								stack.set(IWComponents.MATERIAL_PACKET,
										stack.getOrDefault(IWComponents.MATERIAL_PACKET,
										MaterialPacketComponent.DEFAULT).takeMaterial(material, pre));
							}
						});
			}
			else
			{
				var pair = MaterialProvider.get(stack);
				if (pair == null) continue;
				var material = pair.material();
				var pre = map.getOrDefault(material, 0);
				var have = pair.value() * stack.getCount();
				if (pre > have)
				{
					map.put(material, have - pre);
					inventory.removeStack(i);
				}
				else if (pre == have)
				{
					map.remove(material);
					inventory.removeStack(i);
				}
				else
				{
					map.remove(material);
					int per = pair.value();
					int take = (pre + per - 1) / per;
					int nextCount = stack.getCount() - take;
					if (nextCount > 0) stack.setCount(nextCount);
					else inventory.removeStack(i);
				}
			}
		}
	}
}
