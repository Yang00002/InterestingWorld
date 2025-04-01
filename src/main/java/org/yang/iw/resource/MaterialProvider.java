package org.yang.iw.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistries;
import org.yang.iw.component.IWComponents;
import org.yang.iw.component.MaterialPacketComponent;
import org.yang.iw.component.RepairPacketComponent;
import org.yang.iw.item.IWItems;
import org.yang.iw.tool.material.ToolMaterial;
import org.yang.iw.util.ConsoleStringBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import static org.yang.iw.util.Base.MOD_ID;
import static org.yang.iw.util.Base.iwlogger;

public class MaterialProvider implements SimpleSynchronousResourceReloadListener
{
	static final Map<Item, MaterialVEntry> materialMap = new Object2ObjectOpenHashMap<>();

	@Override
	public Identifier getFabricId()
	{
		return Identifier.of(MOD_ID, "material_provider");
	}

	private void handleResource(Identifier identifier, Resource resource)
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream())))
		{
			JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
			object.asMap().forEach((str, element) -> {
				ToolMaterial toolMaterial = IWRegistries.TOOL_MATERIAL.get(Identifier.tryParse(str));
				JsonObject object1 = element.getAsJsonObject();
				object1.asMap().forEach((str1, element1) -> {
					Item item = Registries.ITEM.get(Identifier.tryParse(str1));
					if (item == Items.AIR) return;
					int value = element1.getAsInt();
					if (value > 0) materialMap.put(item, new MaterialVEntry(toolMaterial, value));
				});
			});
		} catch (Exception ignored)
		{
		}
	}

	public static MaterialVEntry get(ItemStack itemStack)
	{
		var item = itemStack.getItem();
		if (item == IWItems.MATERIAL_PACKET)
			return itemStack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).provideMaterial();
		return materialMap.getOrDefault(itemStack.getItem(), null);
	}

	public String toString()
	{
		ConsoleStringBuilder builder = new ConsoleStringBuilder();
		builder.newLine();
		builder.append("MaterialProvider of %s entries:".formatted(materialMap.size()));
		builder.tab();
		materialMap.forEach((item, pair) -> {
			builder.newLine();
			builder.append(Registries.ITEM.getId(item).toString());
			builder.append(" ");
			var id = IWRegistries.TOOL_MATERIAL.getId(pair.material());
			builder.append(id != null ? id.toString() : "");
			builder.append(" ");
			builder.append(String.valueOf(pair.value()));
		});
		return builder.toString();
	}

	@Override
	public void reload(ResourceManager manager)
	{
		materialMap.clear();
		manager.findResources("material_provider.json", path -> path.getNamespace().compareTo(MOD_ID) == 0 &&
																path.getPath().compareTo("material_provider.json") == 0)
				.forEach(this::handleResource);
		iwlogger.info(toString());
	}
}
