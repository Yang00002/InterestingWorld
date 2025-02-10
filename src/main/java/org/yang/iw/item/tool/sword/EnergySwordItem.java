package org.yang.iw.item.tool.sword;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.component.IWComponents;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.util.Server;
import org.yang.iw.util.constants.Numbers;

import java.util.List;
import java.util.Map;


public class EnergySwordItem extends EnergyToolItem
{

	public EnergySwordItem(Item.Settings settings,
						   Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments)
	{
		super(settings.component(IWComponents.TOOL_FLAG, EnergyToolDataFlag.builder().setCanSweep().build()),
				defaultEnchantments);
	}


	public static ToolComponent createToolComponent(float speedMultiplier)
	{
		RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);
		if (speedMultiplier * 15.0F < 1.0F + Numbers.FLOAT_EPSILON) return new ToolComponent(
				List.of(ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()),
						1F)), 1.0F, 2);
		if (speedMultiplier * 1.5F < 1.0F + Numbers.FLOAT_EPSILON) return new ToolComponent(
				List.of(ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()),
						15.0F * speedMultiplier)), 1.0F, 2);
		return new ToolComponent(
				List.of(ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()),
								15.0F * speedMultiplier),
						ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.SWORD_EFFICIENT),
								1.5F * speedMultiplier)), 1.0F, 2);
	}

	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return !miner.isCreative();
	}
}
