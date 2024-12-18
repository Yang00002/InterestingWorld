package org.yang.interestingworld.datagen.datastructure;

import com.google.gson.JsonParser;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbilityItemModelDataStructure
{
	private static Map<Short, String> pathCache = null;
	private final String parent;

	private static class Texture
	{
		private final String layer0;

		Texture(String layer0)
		{
			if (layer0.contains(":"))
			{
				String[] s = layer0.split(":");
				this.layer0 = s[0] + ":item/" + s[1];
			}
			else this.layer0 = "interestingworld:item/" + layer0;
		}
	}

	private final Texture textures;

	private static class Predicator
	{
		Map<String, Integer> predicate;
		private String model;

		Predicator(int n, String model)
		{
			this.model = model;
			predicate = new HashMap<>();
			predicate.put("custom_model_data", n);
		}
	}

	private List<Predicator> overrides = new LinkedList<>();

	AbilityItemModelDataStructure(String baseTexture, String parent)
	{
		this.parent = "minecraft:item/" + parent;
		this.textures = new Texture(baseTexture);
	}

	public AbilityItemModelDataStructure(String baseTexture)
	{
		this.parent = "minecraft:item/handheld";
		this.textures = new Texture(baseTexture);
	}

	void addTexture(ItemModelGenerator itemModelGenerator, IWAbstractRuneAbility ability, String path, String parent)
	{
		if (pathCache == null) pathCache = new HashMap<>();
		String newPath = "interestingworld:item/" + path;
		String s = "{\"parent\":\"minecraft:item/" + parent + "\",\"textures\": {\"layer0\":\"" + newPath + "\"}}";
		itemModelGenerator.writer.accept(Identifier.of(newPath), () -> JsonParser.parseString(s));
		pathCache.put(ability.toolIndex, newPath);
	}

	public void addTexture(ItemModelGenerator itemModelGenerator, IWAbstractRuneAbility ability, String path)
	{
		if (pathCache == null) pathCache = new HashMap<>();
		String newPath = "interestingworld:item/" + path;
		String s = "{\"parent\":\"minecraft:item/generated\",\"textures\": {\"layer0\":\"" + newPath + "\"}}";
		itemModelGenerator.writer.accept(Identifier.of(newPath), () -> JsonParser.parseString(s));
		pathCache.put(ability.toolIndex, newPath);
	}

	public void build()
	{
		if (pathCache != null)
		{
			pathCache.forEach((n, p) -> {
				overrides.add(new Predicator(n, p));
			});
			pathCache = null;
		}
	}
}
