package org.yang.iw.enchant.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.yang.iw.util.Base.iwlogger;

public class EnchantData
{
	static Map<RegistryEntry<Enchantment>, EnchantData> enchantDatas;
	byte maxLevel; //max_level
	byte maxRandomLevel; // max_random_level
	byte maxTableLevel; //max_table_level
	int weight; //weight
	int worldLevelGate; //world_level
	int[] cost;
	RegistryKey<Enchantment> registryKey;
	RegistryEntry<Enchantment> registryEntry;
	Map<RegistryEntry<Enchantment>, ConflictGroup> conflicts;

	private EnchantData(InputStream stream, @NotNull RegistryKey<Enchantment> key,
						@NotNull RegistryEntry<Enchantment> entry) throws IOException, IllegalArgumentException
	{
		JsonElement element = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream)));
		JsonObject object = element.getAsJsonObject();
		maxLevel = object.get("max_level").getAsByte();
		if (maxLevel < 1) throw new IllegalArgumentException("expect max_level > 0");
		if (object.has("max_random_level"))
		{
			maxRandomLevel = object.get("max_random_level").getAsByte();
			if (maxRandomLevel > maxLevel) throw new IllegalArgumentException("expect max_random_level <= max_level");
		}
		else maxRandomLevel = maxLevel;
		if (object.has("max_table_level"))
		{
			maxTableLevel = object.get("max_table_level").getAsByte();
			if (maxTableLevel > maxRandomLevel)
				throw new IllegalArgumentException("expect max_table_level <= max_random_level");
		}
		else maxTableLevel = maxRandomLevel;
		weight = object.get("weight").getAsInt();
		if (weight < 1) throw new IllegalArgumentException("expect weight > 0");
		if (object.has("world_level"))
		{
			worldLevelGate = object.get("world_level").getAsInt();
			if (worldLevelGate < 0) throw new IllegalArgumentException("expect world_level >= 0");
		}
		else worldLevelGate = 0;
		String costtype = object.get("cost_type").getAsString();
		switch (costtype)
		{
			case "list", "array" ->
			{
				JsonArray array = object.getAsJsonArray("cost");
				if (array.size() < maxLevel) throw new IllegalArgumentException("expect cost array size >= max_level");
				cost = new int[maxLevel + 1];
				cost[0] = 0;
				for (int i = 1; i <= maxLevel; i++)
				{
					cost[i] = array.get(i - 1).getAsInt();
					if (cost[i] < cost[i - 1]) throw new IllegalArgumentException("expect cost[i] >= cost[i-1]");
				}
			}
			case "pile" ->
			{
				JsonArray array = object.getAsJsonArray("cost");
				if (array.size() < maxLevel) throw new IllegalArgumentException("expect cost array size >= max_level");
				cost = new int[maxLevel + 1];
				cost[0] = 0;
				for (int i = 1; i <= maxLevel; i++)
				{
					int n = array.get(i - 1).getAsInt();
					if (n < 0) throw new IllegalArgumentException("expect cost not decrease per level");
					cost[i] = cost[i - 1] + n;
				}
			}
			case "linear" ->
			{
				JsonObject obj2 = object.getAsJsonObject("cost");
				float a;
				if (obj2.has("a")) a = obj2.get("a").getAsFloat();
				else if (obj2.has("k")) a = obj2.get("k").getAsFloat();
				else throw new IllegalArgumentException("expect parameter a or k");
				if (a < 0.0f) throw new IllegalArgumentException("expect a >= 0");
				float b;
				if (obj2.has("b")) b = obj2.get("b").getAsFloat();
				else b = 0.0f;
				cost = new int[maxLevel + 1];
				cost[0] = 0;
				for (int i = 1; i <= maxLevel; i++)
				{
					cost[i] = (int) (a * i + b);
					if (cost[i] < cost[i - 1]) throw new IllegalArgumentException("expect cost[i] >= 0");
				}
			}
			case "fraction" ->
			{
				JsonObject obj2 = object.getAsJsonObject("cost");
				float a;
				if (obj2.has("a")) a = obj2.get("a").getAsFloat();
				else a = 0.0f;
				float b;
				if (obj2.has("b")) b = obj2.get("b").getAsFloat();
				else b = 0.0f;
				float k;
				if (obj2.has("k")) k = obj2.get("k").getAsFloat();
				else k = 0.0f;
				cost = new int[maxLevel + 1];
				cost[0] = 0;
				for (int i = 1; i <= maxLevel; i++)
				{
					float p = i + a;
					if (p == 0.0f) throw new IllegalArgumentException("divide by 0");
					cost[i] = (int) (k / p + b);
					if (cost[i] < cost[i - 1]) throw new IllegalArgumentException("expect cost[i] >= cost[i-1]");
				}
			}
			default -> throw new IllegalArgumentException(
					"should have cost_type of linear, list/array, pile or " + "fraction");
		}
		registryKey = key;
		registryEntry = entry;
		conflicts = new HashMap<>();
		stream.close();
	}

	public static void initialize(ResourceManager manager, RegistryWrapper<Enchantment> wrapper)
	{
		iwlogger.info("Load RuneEnchantData");
		enchantDatas = new HashMap<>();
		manager.findResources("iwdata/enchantdata", (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					String path = identifier.getPath();
					int namespaceEnd = path.indexOf('/', 19);
					String namespace;
					String id;
					if (namespaceEnd == -1)
					{
						namespace = "minecraft";
						id = path.substring(19, path.length() - 5);
					}
					else
					{
						namespace = path.substring(19, namespaceEnd);
						id = path.substring(namespaceEnd + 1, path.length() - 5);
					}
					RegistryKey<Enchantment> registryKey = RegistryKey.of(RegistryKeys.ENCHANTMENT,
							Identifier.of(namespace, id));
					Optional<RegistryEntry.Reference<Enchantment>> registryEntry = wrapper.getOptional(registryKey);
					if (registryEntry.isEmpty()) return;
					try (InputStream stream = resource.getInputStream())
					{
						EnchantData data = new EnchantData(stream, registryKey, registryEntry.get());
						enchantDatas.put(registryEntry.get(), data);
					} catch (Exception ignored)
					{
					}
				});
		for (var i : enchantDatas.entrySet())
			iwlogger.info("RuneEnchantData: Load EnchantmentData of " + i.getKey().getIdAsString());
	}

	static void clearConflictGroup()
	{
		for (var i : enchantDatas.values())
		{
			i.conflicts.clear();
		}
	}

	/**
	 * 这函数效率很低
	 */
	@Nullable
	public static EnchantData getEnchantDataFromString(String str)
	{
		return getEnchantmentDataFromRegistryKey(RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(str)));
	}

	public static boolean initialized()
	{
		return enchantDatas != null;
	}

	public static void clearData()
	{
		enchantDatas = null;
	}

	public RegistryKey<Enchantment> getRegistryKey()
	{
		return registryKey;
	}

	public RegistryEntry<Enchantment> getRegistryEntry()
	{
		return registryEntry;
	}

	/**
	 * 这函数效率很低
	 */
	@Nullable
	public static EnchantData getEnchantmentDataFromRegistryKey(RegistryKey<Enchantment> key)
	{
		for (var i : enchantDatas.values())
		{
			if (i.registryKey == key) return i;
		}
		return null;
	}

	@Nullable
	public static EnchantData getEnchantmentDataFromRegistryEntry(RegistryEntry<Enchantment> key)
	{
		return enchantDatas.getOrDefault(key, null);
	}

	public byte getMaxLevel()
	{
		return maxLevel;
	}

	public byte getMaxRandomLevel()
	{
		return maxRandomLevel;
	}

	public byte getMaxTableLevel()
	{
		return maxTableLevel;
	}

	public int getWeight()
	{
		return weight;
	}

	public int getWorldLevelGate()
	{
		return worldLevelGate;
	}

	public boolean tableConflictWith(Set<RegistryEntry<Enchantment>> entrySet)
	{
		for (var i : entrySet)
			if (conflicts.containsKey(i)) return true;
		return false;
	}

	public boolean tableConflictWith(RegistryEntry<Enchantment> entry)
	{
		return conflicts.containsKey(entry);
	}

	public int getAddLevelCost(int from, int to)
	{
		return cost[to] - cost[from];
	}

	public int getLevelCost(int lv)
	{
		return cost[lv];
	}

	public static void checkResource()
	{
		StringBuilder builder = new StringBuilder();
		if (enchantDatas == null)
		{
			builder.append('\n');
			builder.append("附魔数据未加载, 数据指针为 null");
			iwlogger.info(builder.toString());
			return;
		}
		builder.append('\n');
		builder.append("附魔数据有如下 %s 项:".formatted(enchantDatas.size())).append('\n');
		for (var i : enchantDatas.values())
		{
			builder.append(i.registryKey.getValue()).append('\n');
			builder.append('\t').append("最大等级 %s".formatted(i.maxLevel)).append('\n');
			builder.append('\t').append("最大随机生成等级 %s".formatted(i.maxRandomLevel)).append('\n');
			builder.append('\t').append("最大附魔台附魔等级 %s".formatted(i.maxTableLevel)).append('\n');
			builder.append('\t').append("附魔权重 %s".formatted(i.weight)).append('\n');
			builder.append('\t').append("最低生成世界等级 %s".formatted(i.worldLevelGate)).append('\n');
			builder.append('\t').append("附魔花费: ");
			for (var j : i.cost)
				builder.append(j).append(" ");
			builder.append('\n');
			for (var j : i.conflicts.entrySet())
			{
				if (j.getValue() == null)
					builder.append('\t').append('\t').append("与 %s 绝对冲突".formatted(j.getKey().getIdAsString()))
							.append('\n');
			}
			for (var j : i.conflicts.entrySet())
			{
				var k = j.getKey().getKeyOrValue();
				k.ifLeft(l -> {
					if (j.getValue() != null) builder.append('\t').append('\t')
							.append("与 %s 冲突, 加惩罚 %s, 乘惩罚 %s".formatted(j.getKey().getIdAsString(),
									j.getValue().addPunish, j.getValue().mulPunish)).append('\n');
				});
			}
		}
		iwlogger.info(builder.toString());
	}

	public byte maxLevelWithCost(byte left, byte right, int c)
	{
		for (byte j = right; j > left; j--)
		{
			if (cost[j] <= c)
			{
				return j;
			}
		}
		return left;
	}

	public Map<RegistryEntry<Enchantment>, ConflictGroup> getConflicts()
	{
		return Collections.unmodifiableMap(conflicts);
	}
}
