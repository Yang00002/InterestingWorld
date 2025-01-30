package org.yang.iw.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.yang.iw.block.block_builder.CommonBlockBuilder;
import org.yang.iw.block.forgingblock.ForgingBlock;

public class IWBlocks
{
	public static final Block FORGING_BLOCK = new CommonBlockBuilder(ForgingBlock::new,
			"forging_block").setParentModelForBlockAndItem(Blocks.SMITHING_TABLE).asItem().build();

	public static void initialize()
	{
	}
}
