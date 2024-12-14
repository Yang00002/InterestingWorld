package org.yang.interestingworld.enchant;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Rand;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.yang.interestingworld.util.Base.iwlogger;

public class ToolGroup
{
	static Map<Item, ToolGroup> toolGroups;
	List<TableEnchantGroup> enchantGroups;
	List<Integer> enchantGroupWeights;
	Set<EnchantData> allowEnchants;
	int maxWeight;

	private static void accept(InputStream stream) throws IllegalArgumentException
	{
		JsonElement element = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream)));
		JsonObject object = element.getAsJsonObject();
		JsonArray array = object.get("items").getAsJsonArray();
		Set<Item> itemSet = new HashSet<>();
		for (var arrayObject : array)
		{
			String value = arrayObject.getAsString();
			Optional<Item> it = Registries.ITEM.getOrEmpty(Identifier.of(value));
			it.ifPresent(itemSet::add);
		}
		if (itemSet.isEmpty()) return;
		ToolGroup toolGroup = new ToolGroup();
		toolGroup.enchantGroups = new ArrayList<>();
		toolGroup.enchantGroupWeights = new ArrayList<>();
		toolGroup.allowEnchants = new HashSet<>();
		toolGroup.maxWeight = 0;
		array = object.get("allows").getAsJsonArray();
		for (var arrayObject : array)
		{
			var obj = arrayObject.getAsString();
			var ec = EnchantData.getEnchantDataFromString(obj);
			if (ec == null) continue;
			toolGroup.allowEnchants.add(ec);
		}
		if (toolGroup.allowEnchants.isEmpty()) return;
		array = object.get("groups").getAsJsonArray();
		for (var arrayObject : array)
		{
			var obj = arrayObject.getAsJsonObject();
			int weight = obj.get("weight").getAsInt();
			String g = obj.get("group").getAsString();
			if (weight < 1) throw new IllegalArgumentException("expect weight > 0");
			if (!TableEnchantGroup.tableEnchantGroups.containsKey(g)) continue;
			toolGroup.enchantGroups.add(TableEnchantGroup.tableEnchantGroups.get(g));
			toolGroup.enchantGroupWeights.add(weight);
			toolGroup.maxWeight += weight;
		}
		if (toolGroup.maxWeight < 1) return;
		itemSet.forEach((it) -> toolGroups.put(it, toolGroup));
	}

	public static void reInitialize(ResourceManager manager)
	{
		toolGroups = new HashMap<>();
		iwlogger.info("Load ToolGroup");
		manager.findResources("iwdata/toolgroup", (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					try (InputStream stream = resource.getInputStream())
					{
						accept(stream);
					} catch (Exception ignored)
					{
					}
				});
		TableEnchantGroup.tableEnchantGroups = null;
		for (var i : toolGroups.keySet())
		{
			iwlogger.info(Registries.ITEM.getId(i) + " added to toolgroup");
		}
	}

	public static Set<EnchantData> getAllowEnchantOfItemStack(ItemStack stack)
	{
		var ret = toolGroups.getOrDefault(stack.getItem(), null);
		if (ret == null) return null;
		if (ret.allowEnchants == null) return null;
		return Collections.unmodifiableSet(ret.allowEnchants);
	}

	public static ToolGroup getToolGroupOfItemStack(ItemStack stack)
	{
		return toolGroups.getOrDefault(stack.getItem(), null);
	}

	public static TableEnchantGroup getRandomTableEnchantGroupOfItemStack(ItemStack stack, Rand rand)
	{
		var toolgroup = toolGroups.getOrDefault(stack.getItem(), null);
		if (toolgroup == null) return null;
		var ecg = toolgroup.enchantGroups;
		if (ecg == null) return null;
		int size = ecg.size();
		if (size == 1) return ecg.getFirst();
		var wg = toolgroup.enchantGroupWeights;
		int w = rand.nextEvenInt(1, toolgroup.maxWeight);
		for (int i = 0; i < size; i++)
		{
			int ww = wg.get(i);
			if (w > ww) w -= ww;
			else return ecg.get(i);
		}
		return null;
	}
}
