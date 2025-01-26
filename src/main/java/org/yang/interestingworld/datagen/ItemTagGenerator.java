package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.yang.interestingworld.item.IWItemTags;
import org.yang.interestingworld.item.IWItems;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider<Item>
{

	public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
	{
		super(output, RegistryKeys.ITEM, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
	{
		getOrCreateTagBuilder(IWItemTags.CanEnchantAsPreEnchantHeart).add(IWItems.COPPER_HEART).add(IWItems.IRON_HEART)
				.add(IWItems.GOLD_HEART).add(IWItems.DIAMOND_HEART).add(IWItems.NETHERITE_HEART)
				.add(IWItems.ENDERITE_HEART).add(IWItems.VOIDALLOY_HEART);
	}
}
