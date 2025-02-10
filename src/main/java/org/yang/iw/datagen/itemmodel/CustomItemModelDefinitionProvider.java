package org.yang.iw.datagen.itemmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.itemmodel.server.ItemModelDefinition;

import static org.yang.iw.util.Base.iwlogger;

public class CustomItemModelDefinitionProvider implements ItemModelProvider
{
	Item item;

	ItemModelDefinition model;

	public CustomItemModelDefinitionProvider(Item item, ItemModelDefinition model)
	{
		this.item = item;
		this.model = model;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void use(ItemModelGenerator generator)
	{
		iwlogger.warn("output accept " + getModelId());
		generator.output.accept(item, model.toUnbaked());
	}

	@Override
	public Identifier getModelId()
	{
		Identifier identifier = Registries.ITEM.getId(item);
		return identifier.withPrefixedPath("item/");
	}
}
