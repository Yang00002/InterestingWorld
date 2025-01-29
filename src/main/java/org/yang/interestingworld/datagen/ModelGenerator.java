package org.yang.interestingworld.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Item;
import org.yang.interestingworld.block.IWBlocks;
import org.yang.interestingworld.datagen.datastructure.AbilityItemModelDataStructure;
import org.yang.interestingworld.datagen.itemmodel.ItemModelPool;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;

public class ModelGenerator extends FabricModelProvider
{
	public ModelGenerator(FabricDataOutput output)
	{
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
	{
		blockStateModelGenerator.registerParented(Blocks.SMITHING_TABLE, IWBlocks.FORGING_BLOCK);
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
			ar.addTexture(itemModelGenerator, IWRuneAbilities.SLASHING_ABILITY, Materials[i] + "_sword_slashing");
			ar.addTexture(itemModelGenerator, IWRuneAbilities.SWEETCURSE_ABILITY, Materials[i] + "_sword_sweetcurse");
			ar.addTexture(itemModelGenerator, IWRuneAbilities.INFINITECURSE_ABILITY,
					Materials[i] + "_sword_infinitecurse");
			ar.build();
			JsonElement j = new Gson().toJsonTree(ar);
			itemModelGenerator.writer.accept(ModelIds.getItemModelId(items[i]), () -> j);
		}
	}

	private static void buildStickModel(ItemModelGenerator itemModelGenerator)
	{
		AbilityItemModelDataStructure ar = new AbilityItemModelDataStructure("minecraft:stick");
		ar.addTexture(itemModelGenerator, IWRuneAbilities.SLASHING_ABILITY, "stick_slashing");
		ar.addTexture(itemModelGenerator, IWRuneAbilities.SWEETCURSE_ABILITY, "stick_sweetcurse");
		ar.addTexture(itemModelGenerator, IWRuneAbilities.INFINITECURSE_ABILITY, "stick_infinitecurse");
		ar.build();
		JsonElement j = new Gson().toJsonTree(ar);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.STICK), () -> j);
	}

	private static void buildBlazeRodModel(ItemModelGenerator itemModelGenerator)
	{
		AbilityItemModelDataStructure ar = new AbilityItemModelDataStructure("minecraft:blaze_rod");
		ar.addTexture(itemModelGenerator, IWRuneAbilities.SLASHING_ABILITY, "blazerod_slashing");
		ar.addTexture(itemModelGenerator, IWRuneAbilities.SWEETCURSE_ABILITY, "blazerod_sweetcurse");
		ar.addTexture(itemModelGenerator, IWRuneAbilities.INFINITECURSE_ABILITY, "blazerod_infinitecurse");
		ar.build();
		JsonElement j = new Gson().toJsonTree(ar);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.BLAZEROD), () -> j);
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator)
	{
		ItemModelPool.generatePool(itemModelGenerator);
		//buildSwordModel(itemModelGenerator);
		//buildStickModel(itemModelGenerator);
		//buildBlazeRodModel(itemModelGenerator);
	}
}
