package org.yang.interestingworld.datagen.itemmodel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

import java.util.TreeMap;

import static org.yang.interestingworld.util.Base.iwlogger;

public class LocatedItemModelProvider implements ItemModelProvider
{

	Identifier item;
	Model model;
	String texture_directory = null;
	TreeMap<Integer, Identifier> predictors = new TreeMap<>();

	public LocatedItemModelProvider(Item item, Model model)
	{
		this.item = ModelIds.getItemModelId(item);
		this.model = model;
	}

	public LocatedItemModelProvider(Identifier item, Model model)
	{
		this.item = item;
		this.model = model;
	}

	public LocatedItemModelProvider setTextureAItemTexture(Item item)
	{
		var id = Registries.ITEM.getId(item);
		var s = id.getNamespace();
		if (s == null) s = "minecraft";
		texture_directory = s + ":item/" + id.getPath();
		return this;
	}

	public LocatedItemModelProvider setTextureAsItemTexture(Item item, String directory)
	{
		if (!directory.endsWith("/")) directory += "/";
		var id = Registries.ITEM.getId(item);
		var s = id.getNamespace();
		if (s == null) s = "minecraft";
		texture_directory = s + ":item/" + directory + id.getPath();
		return this;
	}

	public LocatedItemModelProvider setTexture(String namespace, String path)
	{
		texture_directory = namespace + ":" + path;
		return this;
	}

	/**
	 * 使用 ModelIds 获取用于 target 的 id
	 */
	public LocatedItemModelProvider addPredictor(int score, Identifier target)
	{
		predictors.put(score, target);
		return this;
	}

	public LocatedItemModelProvider addPredictor(int score, ItemModelProvider target)
	{
		predictors.put(score, target.getModelId());
		return this;
	}

	public LocatedItemModelProvider addPredictorAndPush(int score, ItemModelProvider target)
	{
		var id = target.getModelId();
		predictors.put(score, id);
		for (var m : ItemModelPool.providers)
		{
			if (m.getModelId() == id) return this;
		}
		ItemModelPool.addModel(target);
		return this;
	}

	@Override
	public void use(ItemModelGenerator generator)
	{
		if (texture_directory == null) texture_directory = item.toString();
		JsonObject jsonObject = new JsonObject();
		var parent = model.getParent();
		if (parent == null)
		{
			iwlogger.info("LocatedItemModelProvider 使用的模型 " + model + " 意外的没有 parent");
			return;
		}
		jsonObject.addProperty("parent", parent.toString());
		JsonObject jsonObject1 = new JsonObject();
		jsonObject1.addProperty("layer0", texture_directory);
		jsonObject.add("textures", jsonObject1);
		if (predictors.isEmpty())
		{
			generator.writer.accept(item, () -> jsonObject);
			return;
		}
		JsonArray ar = new JsonArray();
		for (var entry : predictors.entrySet())
		{
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("custom_model_data", entry.getKey());
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.add("predicate", jsonObject2);
			jsonObject3.addProperty("model", entry.getValue().toString());
			ar.add(jsonObject3);
		}
		jsonObject.add("overrides", ar);
		generator.writer.accept(item, () -> jsonObject);
	}

	@Override
	public Identifier getModelId()
	{
		return item;
	}
}
