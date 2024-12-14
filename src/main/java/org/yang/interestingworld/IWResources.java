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
import org.yang.interestingworld.resource.SequencedEnchantResourceReloadListener;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.yang.interestingworld.util.Base.MOD_ID;
import static org.yang.interestingworld.util.Base.iwlogger;

public class IWResources
{

	public static class RuneItemValue
	{
		public static Map<Item, Integer> RuneItemValue = null;

		private static class RuneItemValueDataStructure
		{
			Map<String, Integer> values = null;
		}

		private static class Listener implements SimpleSynchronousResourceReloadListener
		{
			private static final String BEGINPATH = "iwdata/runeitemvalue";

			@Override
			public void reload(ResourceManager manager)
			{
				RuneItemValue = new LinkedHashMap<>();
				var RuneValueResource = manager.findResources(BEGINPATH,
						(Identifier path) -> path.toString().endsWith(".json"));
				RuneValueResource.forEach((identifier, resource) -> {
					if (Objects.equals(identifier.getPath(), BEGINPATH + "/runeitemvalue.json"))
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
										iwlogger.info("RuneItemValue: Add item " + t);
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
				return Identifier.of(MOD_ID, "runeitemvalue");
			}
		}

		public static void checkResource()
		{
			StringBuilder builder = new StringBuilder();
			if (RuneItemValue == null)
			{
				builder.append('\n');
				builder.append("价值数据未加载, 数据指针为 null");
				iwlogger.info(builder.toString());
				return;
			}
			builder.append('\n');
			builder.append("价值数据有如下 %s 项:".formatted(RuneItemValue.size())).append('\n');
			for (var i : RuneItemValue.entrySet())
			{
				var it = i.getKey();
				var value = i.getValue();
				builder.append(Registries.ITEM.getId(it));
				builder.append(" 价值 ").append(value).append('\n');
			}
			iwlogger.info(builder.toString());
		}
	}

	public static void initialize()
	{
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RuneItemValue.Listener());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new SequencedEnchantResourceReloadListener());
	}
}
