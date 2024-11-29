package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import org.yang.interestingworld.IWItems;
import org.yang.interestingworld.IWTags.EnergyToolTypeTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider<Item>
{
	private Item IT = null;

	private TagKey<Item> TAG = null;

	ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
	{
		super(output, RegistryKeys.ITEM, registriesFuture);
	}

	private void tieItem(Item it)
	{
		IT = it;
	}

	private void tieTag(TagKey<Item> tag)
	{
		TAG = tag;
	}

	private void addItemToTag(TagKey<Item> tagKey)
	{
		if (IT != null)
		{
			var k = Registries.ITEM.getKey(IT);
			k.ifPresent(itemRegistryKey -> getOrCreateTagBuilder(tagKey).addOptional(itemRegistryKey));
		}
	}

	private void addItemToTag(Item it)
	{
		if (TAG != null)
		{
			var k = Registries.ITEM.getKey(it);
			k.ifPresent(itemRegistryKey -> getOrCreateTagBuilder(TAG).addOptional(itemRegistryKey));
		}
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
	{
		tieTag(EnergyToolTypeTags.ROD);
		addItemToTag(IWItems.STICK);
		addItemToTag(IWItems.BLAZEROD);
		tieTag(EnergyToolTypeTags.SWORD);
		addItemToTag(IWItems.STONE_SWORD);
		addItemToTag(IWItems.IRON_SWORD);
		addItemToTag(IWItems.GOLDEN_SWORD);
		addItemToTag(IWItems.DIAMOND_SWORD);
		addItemToTag(IWItems.NETHERITE_SWORD);
	}
}
