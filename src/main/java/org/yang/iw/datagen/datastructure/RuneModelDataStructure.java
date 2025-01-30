package org.yang.iw.datagen.datastructure;

import com.google.gson.JsonParser;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.util.Identifier;
import org.yang.iw.rune_ability.AbstractRuneAbility;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RuneModelDataStructure
{
	private static Map<Short, String> pathCache = null;

	private final String parent = "minecraft:item/generated";

	private static class Texture
	{
		private final String layer0 = "iw:item/rune";
	}

	private final Texture textures = new Texture();

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

	public RuneModelDataStructure()
	{
	}

	public void addTexture(ItemModelGenerator itemModelGenerator, AbstractRuneAbility ability, String path)
	{
		if (pathCache == null) pathCache = new HashMap<>();
		String newPath = "iw:item/" + path;
		String s = "{\"parent\":\"minecraft:item/generated\",\"textures\": {\"layer0\":\"" + newPath + "\"}}";
		itemModelGenerator.writer.accept(Identifier.of(newPath), () -> JsonParser.parseString(s));
		pathCache.put(ability.runeIndex, newPath);
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
