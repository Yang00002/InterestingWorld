package org.yang.interestingworld.item.tool.sword;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.util.Server;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.List;
import java.util.Map;


public class EnergySwordItem extends EnergyToolItem
{

	public EnergySwordItem(ToolMaterial material, Item.Settings settings,
						   Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments)
	{
		super(material, settings.component(DataComponentTypes.TOOL, createToolComponent())
				.component(IWComponents.TOOL_FLAG, EnergyToolDataFlag.copyEmpty().setCanSweep()),
				defaultEnchantments);
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
}
