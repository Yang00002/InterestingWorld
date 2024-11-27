package org.yang.interestingworld.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWBlocks;
import org.yang.interestingworld.IWItems;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IWModelGenerator extends FabricModelProvider
{
	public IWModelGenerator(FabricDataOutput output)
	{
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
	{
		blockStateModelGenerator.registerParented(Blocks.SMITHING_TABLE, IWBlocks.FORGING_BLOCK);
	}

	private static Map<Short, String> pathCache = null;

	private static class AbilityRuneModelDataStructure
	{
		private final String parent = "minecraft:item/generated";

		private static class Texture
		{
			private final String layer0 = "interestingworld:item/rune";
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

		AbilityRuneModelDataStructure()
		{
		}

		void addTexture(ItemModelGenerator itemModelGenerator, IWAbstractRuneAbility ability, String path)
		{
			if (pathCache == null) pathCache = new HashMap<>();
			String newPath = "interestingworld:item/" + path;
			String s = "{\"parent\":\"minecraft:item/generated\",\"textures\": {\"layer0\":\"" + newPath + "\"}}";
			itemModelGenerator.writer.accept(Identifier.of(newPath), () -> JsonParser.parseString(s));
			pathCache.put(ability.runeIndex, newPath);
		}

		void build()
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

	private static class AbilityItemModelDataStructure
	{
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

		AbilityItemModelDataStructure(String baseTexture)
		{
			this.parent = "minecraft:item/handheld";
			this.textures = new Texture(baseTexture);
		}

		void addTexture(ItemModelGenerator itemModelGenerator, IWAbstractRuneAbility ability, String path,
						String parent)
		{
			if (pathCache == null) pathCache = new HashMap<>();
			String newPath = "interestingworld:item/" + path;
			String s = "{\"parent\":\"minecraft:item/" + parent + "\",\"textures\": {\"layer0\":\"" + newPath + "\"}}";
			itemModelGenerator.writer.accept(Identifier.of(newPath), () -> JsonParser.parseString(s));
			pathCache.put(ability.toolIndex, newPath);
		}

		void addTexture(ItemModelGenerator itemModelGenerator, IWAbstractRuneAbility ability, String path)
		{
			if (pathCache == null) pathCache = new HashMap<>();
			String newPath = "interestingworld:item/" + path;
			String s = "{\"parent\":\"minecraft:item/generated\",\"textures\": {\"layer0\":\"" + newPath + "\"}}";
			itemModelGenerator.writer.accept(Identifier.of(newPath), () -> JsonParser.parseString(s));
			pathCache.put(ability.toolIndex, newPath);
		}

		void build()
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

	private static void buildAbilityRuneModel(ItemModelGenerator itemModelGenerator)
	{
		AbilityRuneModelDataStructure ar = new AbilityRuneModelDataStructure();
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SLASHING_ABILITY, "slashing_ability_rune");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEEP_ABILITY, "sweeping_ability_rune");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITESLASHING_ABILITY, "infiniteslashing_ability_rune");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEETCURSE_ABILITY, "sweetcurse_ability_rune");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITECURSE_ABILITY, "infinitecurse_ability_rune");

		ar.build();
		JsonElement j = new Gson().toJsonTree(ar);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.COMMON_ABILITY_RUNE), () -> j);

	}

	private static void buildSwordModel(ItemModelGenerator itemModelGenerator)
	{
		String[] Materials = {"stone", "iron", "golden", "diamond", "netherite"};
		Item[] items = {IWItems.STONE_SWORD, IWItems.IRON_SWORD, IWItems.GOLDEN_SWORD, IWItems.DIAMOND_SWORD,
						IWItems.NETHERITE_SWORD};
		int size = Materials.length;
		for (int i = 0; i < size; i++)
		{
			AbilityItemModelDataStructure ar = new AbilityItemModelDataStructure(
					"minecraft:" + Materials[i] + "_sword");
			ar.addTexture(itemModelGenerator, IWRuneAbilitys.SLASHING_ABILITY, Materials[i] + "_sword_slashing");
			ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEETCURSE_ABILITY, Materials[i] + "_sword_sweetcurse");
			ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITECURSE_ABILITY, Materials[i] + "_sword_infinitecurse");
			ar.build();
			JsonElement j = new Gson().toJsonTree(ar);
			itemModelGenerator.writer.accept(ModelIds.getItemModelId(items[i]), () -> j);
		}
	}

	private static void buildStickModel(ItemModelGenerator itemModelGenerator)
	{
		AbilityItemModelDataStructure ar = new AbilityItemModelDataStructure("minecraft:stick");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEEP_ABILITY, "stick_sweeping");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SLASHING_ABILITY, "stick_slashing");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEETCURSE_ABILITY, "stick_sweetcurse");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITECURSE_ABILITY, "stick_infinitecurse");
		ar.build();
		JsonElement j = new Gson().toJsonTree(ar);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.STICK), () -> j);
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator)
	{
		itemModelGenerator.register(IWItems.BLOOD_SWORD, Models.HANDHELD);
		itemModelGenerator.register(IWItems.EMPTY_RUNE, Models.GENERATED);
		buildAbilityRuneModel(itemModelGenerator);
		buildSwordModel(itemModelGenerator);
		buildStickModel(itemModelGenerator);
	}
}
