package org.yang.interestingworld;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.yang.interestingworld.IWUtil.Base.iwlogger;

public class IWResources
{
	public static Map<Item, Integer> RuneItemValue = null;

	private static class RuneItemValueDataStructure
	{
		Map<String, Integer> values = null;
	}

	private static class Listener implements SimpleSynchronousResourceReloadListener
	{
		private static final String BEGINPATH = "iwdata";

		@Override
		public void reload(ResourceManager manager)
		{
			RuneItemValue = new HashMap<>();
			var RuneValueResource = manager.findResources(BEGINPATH,
					(Identifier path) -> path.toString().endsWith(".json"));
			RuneValueResource.forEach((identifier, resource) -> {
				if (Objects.equals(identifier.getPath(), BEGINPATH + "/runitemvalue.json"))
				{
					try (InputStream stream = resource.getInputStream())
					{
						Gson g = new Gson();
						String s = new String(stream.readAllBytes());
						RuneItemValueDataStructure m = g.fromJson(s, RuneItemValueDataStructure.class);
						m.values.forEach((key, value) -> {
							if (value > 0)
							{
								Item t = Registries.ITEM.get(Identifier.of(key));
								if (t != Items.AIR)
								{
									iwlogger.info("Add item " + t.toString());
									RuneItemValue.put(t, value);
								}
							}
						});
					} catch (Exception ignored)
					{
					}
				}
			});
		}

		@Override
		public Identifier getFabricId()
		{
			return Identifier.of(IWUtil.Base.MOD_ID, "data");
		}
	}

	public static void initialize()
	{
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new Listener());
	}
}
