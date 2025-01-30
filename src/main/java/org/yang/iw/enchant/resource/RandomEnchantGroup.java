package org.yang.iw.enchant.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.yang.iw.util.Base.iwlogger;

public class RandomEnchantGroup extends TableEnchantGroup
{
	static int maxWeight;
	static List<RandomEnchantGroup> enchantGroups;
	int weight;

	public static void reInitialize(ResourceManager manager)
	{
		enchantGroups = new ArrayList<>();
		maxWeight = 0;
		Set<RandomEnchantGroup> ec = new HashSet<>();
		iwlogger.info("Load EnchantGroup");
		manager.findResources("iwdata/enchantpool", (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					try (InputStream stream = resource.getInputStream())
					{
						JsonElement element = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream)));
						JsonObject object = element.getAsJsonObject();
						int weight = object.get("weight").getAsInt();
						JsonArray array = object.get("enchants").getAsJsonArray();
						Set<EnchantData> groupSet = new HashSet<>();
						for (var arrayObject : array)
						{
							String value = arrayObject.getAsString();
							EnchantData data = EnchantData.getEnchantDataFromString(value);
							if (data != null) groupSet.add(data);
						}
						if (groupSet.isEmpty()) return;
						RandomEnchantGroup g = new RandomEnchantGroup();
						g.enchants = new ArrayList<>(groupSet);
						g.weight = weight;
						ec.add(g);
					} catch (Exception ignored)
					{
					}
				});
		for (var i : ec)
		{
			enchantGroups.add(i);
			maxWeight += i.weight;
		}
	}

	public static int getMaxWeight()
	{
		return maxWeight;
	}

	public static List<RandomEnchantGroup> getEnchantGroups()
	{
		return Collections.unmodifiableList(enchantGroups);
	}

	public int getWeight()
	{
		return weight;
	}
}
