package org.yang.iw.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.yang.iw.IWItemGroups;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.DependRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.block.block_builder.CommonBlockBuilder;
import org.yang.iw.block.forgingblock.ForgingBlock;

import static org.yang.iw.util.Base.iwlogger;

@DataGenSupplier
@DependRegister(depends = IWItemGroups.class)
public class IWBlocks
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
		LoadTime.assertLoaded(IWItemGroups.class);
	}

	public static final Block FORGING_BLOCK = new CommonBlockBuilder(ForgingBlock::new,
			"forging_block").setParentModelForBlockAndItem(Blocks.SMITHING_TABLE).asItem().setTranslation("符文锻造台")
			.build();


	public static void initialize()
	{
	}
}
