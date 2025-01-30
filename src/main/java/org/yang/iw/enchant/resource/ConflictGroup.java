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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.yang.iw.util.Base.iwlogger;

public class ConflictGroup
{
	public static final ConflictGroup fatalGroup = new ConflictGroup();
	public int addPunish = 0;
	public float mulPunish = 0.0f;
	public int weightPunish = 0;

	private static void accept(InputStream stream) throws IllegalArgumentException
	{
		JsonElement element = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream)));
		JsonObject object = element.getAsJsonObject();
		JsonArray array = object.get("values").getAsJsonArray();
		Set<EnchantData> conflictSet = new HashSet<>();
		for (var arrayObject : array)
		{
			String value = arrayObject.getAsString();
			EnchantData data = EnchantData.getEnchantDataFromString(value);
			if (data != null) conflictSet.add(data);
		}
		if (conflictSet.size() < 2) throw new IllegalArgumentException("expect values array size > 1");
		boolean fatal = false;
		if (object.has("fatal"))
		{
			fatal = object.get("fatal").getAsBoolean();
		}
		if (fatal)
		{
			for (var data : conflictSet)
			{
				for (var data2 : conflictSet)
				{
					if (data != data2)
					{
						data.conflicts.put(data2.registryEntry, fatalGroup);
					}
				}
			}
		}
		else
		{
			ConflictGroup group = new ConflictGroup();
			if (object.has("add_punish"))
			{
				group.addPunish = object.get("add_punish").getAsInt();
				if (group.addPunish < 0) throw new IllegalArgumentException("expect add_punish >= 0");
			}
			else group.addPunish = 0;
			if (object.has("mul_punish"))
			{
				group.mulPunish = object.get("mul_punish").getAsFloat();
				if (group.mulPunish < 0.0f) throw new IllegalArgumentException("expect mul_punish >= 0.0f");
			}
			if (object.has("weight_punish"))
			{
				group.weightPunish = object.get("weight_punish").getAsInt();
				if (group.weightPunish < 0) throw new IllegalArgumentException("expect weight_punish >= 0");
			}
			Map<ConflictGroup, ConflictGroup> transform = new HashMap<>();
			for (var data : conflictSet)
			{
				for (var data2 : conflictSet)
				{
					if (data != data2)
					{
						ConflictGroup g0 = data.conflicts.getOrDefault(data2, null);
						if (g0 == null) data.conflicts.put(data2.registryEntry, group);
						else if (g0 != fatalGroup)
						{
							ConflictGroup g1 = transform.getOrDefault(g0, null);
							if (g1 != null) data.conflicts.put(data2.registryEntry, g1);
							else
							{
								g1 = new ConflictGroup();
								g1.addPunish = g0.addPunish + group.addPunish;
								g1.mulPunish = g0.mulPunish + group.mulPunish;
								g1.weightPunish = g0.weightPunish + group.weightPunish;
								transform.put(g0, g1);
								data.conflicts.put(data2.registryEntry, g1);
							}
						}
					}
				}
			}
		}
	}

	public static void reInitialize(ResourceManager manager)
	{
		iwlogger.info("Reload ConflictGroups");
		EnchantData.clearConflictGroup();
		manager.findResources("iwdata/enchantconflict", (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					try (InputStream stream = resource.getInputStream())
					{
						accept(stream);
					} catch (Exception ignored)
					{
					}
				});
	}
}
