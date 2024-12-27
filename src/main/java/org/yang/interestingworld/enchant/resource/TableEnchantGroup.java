package org.yang.interestingworld.enchant.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.yang.interestingworld.util.Base.iwlogger;

public class TableEnchantGroup
{
	static Map<String, TableEnchantGroup> tableEnchantGroups;
	List<EnchantData> enchants;

	private static void accept(String identifier, InputStream stream) throws IllegalArgumentException
	{
		JsonElement element = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream)));
		JsonArray array = element.getAsJsonArray();
		Set<EnchantData> groupSet = new LinkedHashSet<>();
		for (var arrayObject : array)
		{
			String value = arrayObject.getAsString();
			EnchantData data = EnchantData.getEnchantDataFromString(value);
			if (data != null) groupSet.add(data);
		}
		if (groupSet.isEmpty()) return;
		TableEnchantGroup g = new TableEnchantGroup();
		g.enchants = new ArrayList<>(groupSet);
		tableEnchantGroups.put(identifier, g);
		iwlogger.info("add tableEnchantGroup " + identifier + " contains " + g.enchants.size() + " enchant");
	}

	public static void reInitialize(ResourceManager manager)
	{
		tableEnchantGroups = new HashMap<>();
		iwlogger.info("Load tableEnchantGroup");
		manager.findResources("iwdata/enchantgroup", (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					try (InputStream stream = resource.getInputStream())
					{
						String path = identifier.getPath();
						accept(path.substring(20, path.length() - 5), stream);
					} catch (Exception ignored)
					{
					}
				});
	}

	public int getEnchantCount()
	{
		return enchants.size();
	}

	public List<EnchantData> getEnchants()
	{
		return enchants;
	}
}
