package org.yang.interestingworld.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import org.yang.interestingworld.IWBlocks;
import org.yang.interestingworld.IWItems;
import org.yang.interestingworld.datagen.datastructure.AbilityItemModelDataStructure;
import org.yang.interestingworld.datagen.datastructure.RuneModelDataStructure;
import org.yang.interestingworld.rune.IWRuneAbilitys;
import org.yang.interestingworld.rune.IWRuneUpgrades;

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

	private static void buildAbilityRuneModel(ItemModelGenerator itemModelGenerator)
	{
		RuneModelDataStructure ar = new RuneModelDataStructure();
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SLASHING_ABILITY, "slashing_ability_rune");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITESLASHING_ABILITY, "infiniteslashing_ability_rune");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEETCURSE_ABILITY, "sweetcurse_ability_rune");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITECURSE_ABILITY, "infinitecurse_ability_rune");

		ar.build();
		JsonElement j = new Gson().toJsonTree(ar);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.COMMON_ABILITY_RUNE), () -> j);

	}

	private static void buildUpgradeRuneModel(ItemModelGenerator itemModelGenerator)
	{
		RuneModelDataStructure ur = new RuneModelDataStructure();
		ur.addTexture(itemModelGenerator, IWRuneUpgrades.SWEEPING_UPGRADE, "sweeping_upgrade_rune");
		ur.addTexture(itemModelGenerator, IWRuneUpgrades.SWEEPING3_UPGRADE, "sweeping3_upgrade_rune");
		ur.addTexture(itemModelGenerator, IWRuneUpgrades.SWEEPING4_UPGRADE, "sweeping4_upgrade_rune");
		ur.addTexture(itemModelGenerator, IWRuneUpgrades.HEAVY_UPGRADE, "heavy_upgrade_rune");
		ur.build();
		JsonElement j = new Gson().toJsonTree(ur);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.COMMON_UPGRADE_RUNE), () -> j);
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
			ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITECURSE_ABILITY,
					Materials[i] + "_sword_infinitecurse");
			ar.build();
			JsonElement j = new Gson().toJsonTree(ar);
			itemModelGenerator.writer.accept(ModelIds.getItemModelId(items[i]), () -> j);
		}
	}

	private static void buildStickModel(ItemModelGenerator itemModelGenerator)
	{
		AbilityItemModelDataStructure ar = new AbilityItemModelDataStructure("minecraft:stick");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SLASHING_ABILITY, "stick_slashing");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEETCURSE_ABILITY, "stick_sweetcurse");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITECURSE_ABILITY, "stick_infinitecurse");
		ar.build();
		JsonElement j = new Gson().toJsonTree(ar);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.STICK), () -> j);
	}

	private static void buildBlazeRodModel(ItemModelGenerator itemModelGenerator)
	{
		AbilityItemModelDataStructure ar = new AbilityItemModelDataStructure("minecraft:blaze_rod");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SLASHING_ABILITY, "blazerod_slashing");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.SWEETCURSE_ABILITY, "blazerod_sweetcurse");
		ar.addTexture(itemModelGenerator, IWRuneAbilitys.INFINITECURSE_ABILITY, "blazerod_infinitecurse");
		ar.build();
		JsonElement j = new Gson().toJsonTree(ar);
		itemModelGenerator.writer.accept(ModelIds.getItemModelId(IWItems.BLAZEROD), () -> j);
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator)
	{
		itemModelGenerator.register(IWItems.EMPTY_RUNE, Models.GENERATED);
		buildAbilityRuneModel(itemModelGenerator);
		buildUpgradeRuneModel(itemModelGenerator);
		buildSwordModel(itemModelGenerator);
		buildStickModel(itemModelGenerator);
		buildBlazeRodModel(itemModelGenerator);
	}
}
