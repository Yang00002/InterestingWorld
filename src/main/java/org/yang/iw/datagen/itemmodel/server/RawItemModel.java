package org.yang.iw.datagen.itemmodel.server;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.itemmodel.ItemModelPool;
import org.yang.iw.datagen.itemmodel.ItemModelProvider;
import org.yang.iw.datagen.itemmodel.ModelIdProvider;

import static org.yang.iw.util.Base.iwlogger;

public abstract class RawItemModel implements ItemModelProvider
{
	public RawItemModel upload()
	{
		ItemModelPool.addModel(() -> this);
		return this;
	}

	public static SimpleRawItemModel simple(Item item, ModelParents parent)
	{
		return new SimpleRawItemModel(item, parent);
	}

	public static SimpleRawItemModel simple(Identifier item, ModelParents parent)
	{
		return new SimpleRawItemModel(item, parent);
	}

	public static SimpleRawItemModel simple(String namespace, String itemId, ModelParents parent)
	{
		return new SimpleRawItemModel(namespace, itemId, parent);
	}

	public static LimitRawItemModel of(Item item)
	{
		return new LimitRawItemModel(item);
	}

	public static LimitRawItemModel of(Identifier item)
	{
		return new LimitRawItemModel(item);
	}

	public static LimitRawItemModel of(String namespace, String itemId)
	{
		return new LimitRawItemModel(namespace, itemId);
	}

	public static class SimpleRawItemModel extends RawItemModel
	{
		ModelParents parent;
		String namespace;
		String id;

		SimpleRawItemModel(Item item, ModelParents parent)
		{
			this.parent = parent;
			var identifier = Registries.ITEM.getId(item);
			namespace = identifier.getNamespace();
			if (namespace.isEmpty()) namespace = "minecraft";
			id = identifier.getPath();
		}

		SimpleRawItemModel(Identifier item, ModelParents parent)
		{
			this.parent = parent;
			namespace = item.getNamespace();
			if (namespace.isEmpty()) namespace = "minecraft";
			id = item.getPath();
		}

		SimpleRawItemModel(String namespace, String itemId, ModelParents parent)
		{
			this.parent = parent;
			this.namespace = namespace;
			id = itemId;
		}

		public SimpleRawItemModel setIdFormat(String format)
		{
			id = format.formatted(id);
			return this;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void use(ItemModelGenerator generator)
		{
			JsonObject outputModelObject = new JsonObject();
			var parent = this.parent.toClientModel().getParent();
			if (parent == null)
			{
				iwlogger.info("模型" + this.parent + " 意外的没有 parent");
				return;
			}
			outputModelObject.addProperty("parent", parent.toString());
			JsonObject jsonObject1 = new JsonObject();
			jsonObject1.addProperty("layer0", namespace + ":item/" + id);
			outputModelObject.add("textures", jsonObject1);
			generator.modelCollector.accept(Identifier.of(namespace, "item/" + id), () -> outputModelObject);
		}

		@Override
		public Identifier getModelId()
		{
			return Identifier.of(namespace, "item/" + id);
		}

	}

	public static class LimitRawItemModel implements ModelIdProvider
	{
		String namespace;
		String id;

		LimitRawItemModel(Item item)
		{
			var identifier = Registries.ITEM.getId(item);
			namespace = identifier.getNamespace();
			if (namespace.isEmpty()) namespace = "minecraft";
			id = identifier.getPath();
		}

		LimitRawItemModel(Identifier item)
		{
			namespace = item.getNamespace();
			if (namespace.isEmpty()) namespace = "minecraft";
			id = item.getPath();
		}

		LimitRawItemModel(String namespace, String itemId)
		{
			this.namespace = namespace;
			id = itemId;
		}

		public LimitRawItemModel setIdFormat(String format)
		{
			id = format.formatted(id);
			return this;
		}

		@Override
		public Identifier getModelId()
		{
			return Identifier.of(namespace, "item/" + id);
		}
	}
}
