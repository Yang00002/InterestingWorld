package org.yang.interestingworld.block;

import net.minecraft.block.Block;
import org.yang.interestingworld.block.block_builder.CommonBlockBuilder;
import org.yang.interestingworld.block.forgingblock.ForgingBlock;

public class IWBlocks
{
	public static final Block FORGING_BLOCK = new CommonBlockBuilder(ForgingBlock::new, "forging_block").asItem()
			.build();

	public static void initialize()
	{
	}
}
