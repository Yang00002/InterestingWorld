package org.yang.interestingworld.item.tool.sword;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.yang.interestingworld.item.tool.EnergyToolItem;

import java.util.List;


public class EnergySword extends EnergyToolItem
{

	public EnergySword(ToolMaterial material, Item.Settings settings)
	{
		super(material, settings.component(DataComponentTypes.TOOL, createToolComponent()));
	}

	private static ToolComponent createToolComponent()
	{
		return new ToolComponent(List.of(ToolComponent.Rule.ofAlwaysDropping(List.of(Blocks.COBWEB), 15.0F),
				ToolComponent.Rule.of(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2);
	}

	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return !miner.isCreative();
	}

	@Override
	public boolean canSweep(ItemStack stack)
	{
		return true;
	}
}
