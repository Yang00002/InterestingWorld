package org.yang.iw.datagen.itemmodel;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;

public class ItemModelIdBuilder
{
	String namespace;
	String value;
	String directory = "item/";

	private ItemModelIdBuilder()
	{
	}

	public static ItemModelIdBuilder of(Item item)
	{
		ItemModelIdBuilder constructer = new ItemModelIdBuilder();
		Identifier id = Registries.ITEM.getId(item);
		constructer.namespace = id.getNamespace();
		if (constructer.namespace == null) constructer.namespace = "minecraft";
		constructer.value = id.getPath();
		return constructer;
	}

	public static ItemModelIdBuilder of(Identifier id)
	{
		ItemModelIdBuilder constructer = new ItemModelIdBuilder();
		constructer.namespace = id.getNamespace();
		if (constructer.namespace == null) constructer.namespace = "minecraft";
		constructer.value = id.getPath();
		return constructer;
	}

	public static ItemModelIdBuilder ofIW(String name)
	{
		ItemModelIdBuilder constructer = new ItemModelIdBuilder();
		constructer.namespace = Base.MOD_ID;
		constructer.value = name;
		return constructer;
	}

	public ItemModelIdBuilder addPrefix(String string)
	{
		value = string + value;
		return this;
	}

	public ItemModelIdBuilder addSuffix(String string)
	{
		value += string;
		return this;
	}

	public ItemModelIdBuilder addDirectory(String dir)
	{
		if (!dir.endsWith("/")) dir += "/";
		directory += dir;
		return this;
	}

	public Identifier build()
	{
		return Identifier.of(namespace, directory + value);
	}
}
