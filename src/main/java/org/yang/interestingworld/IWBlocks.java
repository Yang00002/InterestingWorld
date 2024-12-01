package org.yang.interestingworld;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.block.forgingblock.ForgingBlock;
import org.yang.interestingworld.util.Base;

import static org.yang.interestingworld.IWItemGroups.*;

public class IWBlocks
{
	public static Block FORGING_BLOCK = null;

	private static Block createBlock(Block block, String id)
	{
		Identifier ide = Identifier.of(Base.MOD_ID, id);
		Registry.register(Registries.BLOCK, ide, block);
		Registry.register(Registries.ITEM, ide, new BlockItem(block, new Item.Settings()));
		return block;
	}

	public static void initialize()
	{
		FORGING_BLOCK = createBlock(new ForgingBlock(AbstractBlock.Settings.create()), "forging_block");
	}

	public static void addBlockItemToItemGroupWhenEnterWorld()
	{
		addItemToGroup(CommonItemInitializer.getInstance(FORGING_BLOCK.asItem()), BLOCKS_GROUP);
	}
}
