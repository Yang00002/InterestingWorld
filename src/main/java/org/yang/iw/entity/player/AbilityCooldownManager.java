package org.yang.iw.entity.player;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.yang.iw.ability.AbilityCooldownGroup;
import org.yang.iw.ability.AbstractAbility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class AbilityCooldownManager
{
	private final IWServerPlayerData data;

	AbilityCooldownManager(IWServerPlayerData data)
	{
		this.data = data;
	}

	public void copy(AbilityCooldownManager manager)
	{
		this.cooldownTick = manager.cooldownTick;
		this.cooldownEntryMap = new HashMap<>(manager.cooldownEntryMap);
	}

	public static class Entry
	{
		int startTick;
		int endTick;

		public Entry(int start, int end)
		{
			startTick = start;
			endTick = end;
		}
	}

	Map<AbilityCooldownGroup, Entry> cooldownEntryMap = new HashMap<>();
	int cooldownTick = 0;

	private String readString(ByteBuffer buffer)
	{
		StringBuilder result = new StringBuilder();
		while (buffer.hasRemaining())
		{
			byte b = buffer.get();
			if (b == ' ') break;
			result.append((char) b);
		}
		return result.toString();
	}

	private Identifier readIdentifier(ByteBuffer buffer)
	{
		String namespace = readString(buffer);
		String id = readString(buffer);
		return Identifier.of(namespace, id);
	}

	private Entry readEntry(ByteBuffer buffer)
	{
		int start = buffer.getInt();
		int end = buffer.getInt();
		return new Entry(start, end);
	}

	private boolean readMapEntry(ByteBuffer buffer)
	{
		if (buffer.hasRemaining())
		{
			Identifier id = readIdentifier(buffer);
			Entry entry = readEntry(buffer);
			var group = AbilityCooldownGroup.groupOf(id);
			if (group != AbilityCooldownGroup.EMPTY && entry.startTick <= cooldownTick && entry.endTick > cooldownTick)
				cooldownEntryMap.put(group, entry);
			return true;
		}
		return false;
	}

	private void readMap(byte[] array)
	{
		if (array.length == 0) return;
		ByteBuffer buffer = ByteBuffer.wrap(array);
		while (readMapEntry(buffer)) ;
	}


	public void readNbt(NbtCompound nbt)
	{
		cooldownTick = nbt.getInt("tick");
		var array = nbt.getByteArray("value");
		readMap(array);
	}

	public NbtCompound writeNbt()
	{
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("tick", cooldownTick);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try
		{
			for (var entry : cooldownEntryMap.entrySet())
			{
				var id = entry.getKey().id;
				var et = entry.getValue();
				stream.write(id.getNamespace().getBytes());
				stream.write(' ');
				stream.write(id.getPath().getBytes());
				stream.write(' ');
				stream.write(ByteBuffer.allocate(4).putInt(et.startTick).array());
				stream.write(ByteBuffer.allocate(4).putInt(et.endTick).array());
			}
			nbt.putByteArray("value", stream.toByteArray());
		} catch (IOException exception)
		{
			nbt.putByteArray("value", new byte[0]);
		}
		return nbt;
	}

	public boolean tick(AbstractAbility care)
	{
		if (cooldownEntryMap.isEmpty())
		{
			cooldownTick = 0;
			return false;
		}
		cooldownTick++;
		boolean ret = false;
		var ca = cooldownEntryMap.getOrDefault(care.getCooldownGroup(), null);
		if (ca != null)
		{
			if (((((cooldownTick - ca.startTick) << 4) / (ca.endTick - ca.startTick)) != data.clientStep)) data.sync();
			if (cooldownTick < ca.endTick) ret = true;
		}
		this.cooldownEntryMap.entrySet().removeIf(entry -> entry.getValue().endTick <= cooldownTick);
		if (cooldownEntryMap.isEmpty()) cooldownTick = 0;
		return ret;
	}

	public void setCooldown(AbstractAbility ability, int duration)
	{
		data.sync();
		var group = ability.getCooldownGroup();
		if (group == AbilityCooldownGroup.EMPTY) return;
		if (duration <= 0) return;
		int maxTick = cooldownTick + duration;
		if (maxTick < cooldownTick)
		{
			this.cooldownEntryMap.forEach((id, entry) -> {
				entry.startTick -= cooldownTick;
				entry.endTick -= cooldownTick;
			});
			cooldownTick = 0;
			cooldownEntryMap.put(group, new Entry(0, duration));
		}
		else cooldownEntryMap.put(group, new Entry(cooldownTick, maxTick));
	}

	public boolean isInCooldown(AbstractAbility ability)
	{
		return cooldownEntryMap.containsKey(ability.getCooldownGroup());
	}

	public int cooldownStep(AbstractAbility ability)
	{
		var entry = cooldownEntryMap.getOrDefault(ability.getCooldownGroup(), null);
		if (entry != null)
		{
			int time = entry.endTick - entry.startTick;
			int process = cooldownTick - entry.startTick;
			return (process << 4) / time;
		}
		return 16;
	}
}
