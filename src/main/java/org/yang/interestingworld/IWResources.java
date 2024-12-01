package org.yang.interestingworld;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

import java.io.InputStream;
import java.util.*;

import static org.yang.interestingworld.util.Base.MOD_ID;
import static org.yang.interestingworld.util.Base.iwlogger;

public class IWResources
{
	private static String splitStrAndGetEnd(String string, String del)
	{
		var s = string.split(del);
		return s[s.length - 1];
	}

	private static String splitStrAndGetFirst(String string, String del)
	{
		var s = string.split(del);
		return s[0];
	}

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
	}

	public static class ToolEnchantmentType
	{
		public static Map<TagKey<Item>, Set<RegistryKey<Enchantment>>> ToolEnchantmentType = null;

		private static class ToolEnchantmentTypeDataStructure
		{
			List<String> values = null;
		}

		private static class Listener implements SimpleSynchronousResourceReloadListener
		{
			private static final String BEGINPATH = "iwdata/toolenchantmenttype";

			@Override
			public void reload(ResourceManager manager)
			{
				Map<String, TagKey<Item>> tags = new LinkedHashMap<>();
				ToolEnchantmentType = new LinkedHashMap<>();
				for (var tag : IWTags.EnergyToolTypeTags.getAll())
				{
					tags.put(splitStrAndGetEnd(tag.id().getPath(), "/"), tag);
				}
				var RuneValueResource = manager.findResources(BEGINPATH,
						(Identifier path) -> path.toString().endsWith(".json"));
				RuneValueResource.forEach((identifier, resource) -> {
					String path = identifier.getPath();
					path = splitStrAndGetEnd(path, "/");
					path = splitStrAndGetFirst(path, "\\.");
					if (tags.containsKey(path))
					{
						try (InputStream stream = resource.getInputStream())
						{
							Gson g = new Gson();
							String s = new String(stream.readAllBytes());
							ToolEnchantmentTypeDataStructure m = g.fromJson(s, ToolEnchantmentTypeDataStructure.class);
							Set<RegistryKey<Enchantment>> ecset = new LinkedHashSet<>();
							String finalPath = path;
							m.values.forEach((value) -> {
								iwlogger.info("ToolEnchantmentType: Add " + value + " for " + finalPath);
								ecset.add(RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(value)));
							});
							if (!ecset.isEmpty())
							{
								ToolEnchantmentType.put(tags.get(path), ecset);
							}
						} catch (Exception ignored)
						{
						}
					}
				});
			}

			@Override
			public Identifier getFabricId()
			{
				return Identifier.of(MOD_ID, "toolenchantmenttype");
			}
		}
	}

	public static class EnchantmentData
	{
		public static class EnchantmentValue
		{
			private int maxAchieveLevel = 1;
			private int maxRandomLevel = 1;
			private int maxAllowLevel = 1;
			private int levelCostParameterA = 1;
			private int levelCostParameterB = 1;
			// C > maxAllowLevel 为反比例
			// 线性 AX+B
			// 反比例 A/(C-X) + B
			private int levelCostParameterC = 0;
			private int conflictCostPunishment = -1;
			private int weight = 1;

			public int getLevelCost(int level)
			{
				if (maxAllowLevel < levelCostParameterC && level <= maxAllowLevel)
					return levelCostParameterA / (levelCostParameterC - level) + levelCostParameterB;
				return levelCostParameterA * level + levelCostParameterB;
			}

			public int getMaxAllowLevel()
			{
				return maxAllowLevel;
			}

			public boolean allowConflict()
			{
				return conflictCostPunishment > -1;
			}

			public int getConflictCostPunishment()
			{
				return conflictCostPunishment;
			}
		}

		public static Map<RegistryKey<Enchantment>, EnchantmentValue> EnchantmentData = null;

		private static class Listener implements SimpleSynchronousResourceReloadListener
		{
			private static final String BEGINPATH = "iwdata/enchantmentdata";

			@Override
			public void reload(ResourceManager manager)
			{
				EnchantmentData = new LinkedHashMap<>();
				int size = BEGINPATH.length();
				var RuneValueResource = manager.findResources(BEGINPATH,
						(Identifier path) -> path.toString().endsWith(".json"));
				RuneValueResource.forEach((identifier, resource) -> {
					String path = identifier.getPath();
					path = path.substring(size + 1);
					String[] paths = path.split("/");
					String namespace = "minecraft";
					String id;
					if (paths.length == 1)
					{
						id = splitStrAndGetFirst(paths[0], "\\.");
					}
					else if (paths.length == 2)
					{
						namespace = paths[0];
						id = splitStrAndGetFirst(paths[paths.length - 1], "\\.");
					}
					else return;
					RegistryKey<Enchantment> registryKey = RegistryKey.of(RegistryKeys.ENCHANTMENT,
							Identifier.of(namespace, id));
					try (InputStream stream = resource.getInputStream())
					{
						Gson g = new Gson();
						String s = new String(stream.readAllBytes());
						EnchantmentValue m = g.fromJson(s, EnchantmentValue.class);
						iwlogger.info("EnchantmentValue: Add enchantment " + registryKey.getValue().toString());
						EnchantmentData.put(registryKey, m);
					} catch (Exception ignored)
					{
					}
				});
			}

			@Override
			public Identifier getFabricId()
			{
				return Identifier.of(MOD_ID, "enchantmentdata");
			}
		}
	}


	public static void initialize()
	{
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RuneItemValue.Listener());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ToolEnchantmentType.Listener());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new EnchantmentData.Listener());
	}
}
