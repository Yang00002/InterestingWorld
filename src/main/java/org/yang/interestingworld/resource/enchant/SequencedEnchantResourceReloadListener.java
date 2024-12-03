package org.yang.interestingworld.resource.enchant;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

import static org.yang.interestingworld.util.Base.MOD_ID;
import static org.yang.interestingworld.util.Base.iwlogger;
import static org.yang.interestingworld.util.Common.splitStrAndGetEnd;
import static org.yang.interestingworld.util.Common.splitStrAndGetFirst;

public class SequencedEnchantResourceReloadListener implements SimpleSynchronousResourceReloadListener
{

	private static final String ENCHANTGROUP_BEGINPATH = "iwdata/enchantgroup";
	private static final String TOOLGROUPPATH = "iwdata/toolgroup.json";

	private static final String ENCHANTCONFLICT_BEGINPATH = "iwdata/enchantconflict";

	private static final String RUNEENCHANT_BEGINPATH = "iwdata/runeenchant";

	@Override
	public Identifier getFabricId()
	{
		return Identifier.of(MOD_ID, "enchantmentresources");
	}

	@Override
	public void reload(ResourceManager manager)
	{
		handleReload(manager);
	}

	public static void handleReload(ResourceManager manager)
	{
		if (RuneEnchantData.Data != null)
		{
			handleToolGroupData(manager);
			handleEnchantGroupData(manager);
			handleEnchantConflictData(manager);
		}
		else iwlogger.info("Reload: Defer Enchant About data reload because RuneEnchantData hasn't been loaded.");
	}

	public static void handleToolGroupData(ResourceManager manager)
	{
		iwlogger.info("Reload ToolGroupData");
		ToolGroupData.Data = new HashMap<>();
		manager.findResources(TOOLGROUPPATH, (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					if (!Objects.equals(identifier.getPath(), TOOLGROUPPATH)) return;
					try (InputStream stream = resource.getInputStream())
					{
						Gson g = new Gson();
						String s = new String(stream.readAllBytes());
						Type mapType = new TypeToken<Map<String, String>>() {}.getType();
						Map<String, String> m = g.fromJson(s, mapType);
						for (Map.Entry<String, String> entry : m.entrySet())
						{
							Optional<Item> it = Registries.ITEM.getOrEmpty(Identifier.of(entry.getKey()));
							it.ifPresent(item -> ToolGroupData.Data.put(item, entry.getValue()));
						}
					} catch (Exception ignored)
					{
					}
				});
		for (var i : ToolGroupData.Data.entrySet())
		{
			iwlogger.info(
					"ToolGroupData: Item " + Registries.ITEM.getId(i.getKey()) + " is in ToolGroup " + i.getValue());
		}
	}

	public static void handleRuneEnchantData(ResourceManager manager, RegistryWrapper.Impl<Enchantment> wrapperLookup)
	{
		iwlogger.info("Load RuneEnchantData");
		int beginpath_size = RUNEENCHANT_BEGINPATH.length() + 1;
		RuneEnchantData.Data = new HashMap<>();
		manager.findResources(RUNEENCHANT_BEGINPATH, (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					String path = identifier.getPath();
					path = path.substring(beginpath_size);
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
						RuneEnchantData.IO m = g.fromJson(s, RuneEnchantData.IO.class);
						m.validate();
						var entry = wrapperLookup.getOptional(registryKey);
						entry.ifPresent(enchantmentReference -> RuneEnchantData.Data.put(registryKey,
								new RuneEnchantData(m, registryKey, enchantmentReference)));
					} catch (Exception ignored)
					{
					}
				});
		for (var i : RuneEnchantData.Data.entrySet())
		{
			iwlogger.info("RuneEnchantData: Load EnchantmentData of " + i.getKey().getValue().toString() + ", detail" +
						  ":");
			i.getValue().log();
		}
	}

	public static void handleEnchantGroupData(ResourceManager manager)
	{
		iwlogger.info("Reload EnchantGroupData");
		EnchantGroupData.Data = new HashMap<>();
		var availableTags = ToolGroupData.getGroups();
		manager.findResources(ENCHANTGROUP_BEGINPATH, (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					String path = identifier.getPath();
					path = splitStrAndGetEnd(path, "/");
					path = splitStrAndGetFirst(path, "\\.");
					if (availableTags.contains(path))
					{
						try (InputStream stream = resource.getInputStream())
						{
							Gson g = new Gson();
							String s = new String(stream.readAllBytes());
							EnchantGroupData.IO m = g.fromJson(s, EnchantGroupData.IO.class);
							Set<RuneEnchantData> ecset = new HashSet<>();
							String finalPath = path;
							m.values.forEach((value) -> {
								RegistryKey<Enchantment> ec = RegistryKey.of(RegistryKeys.ENCHANTMENT,
										Identifier.tryParse(value));
								RuneEnchantData data = RuneEnchantData.Data.get(ec);
								if (data != null) ecset.add(data);
								else
								{
									iwlogger.warn("ToolEnchantmentType: Don't Add " + value + " for " + finalPath +
												  " because enchantment not exist");
									for (var i : RuneEnchantData.Data.entrySet())
									{
										iwlogger.info(i.getKey().getValue().toString());
										iwlogger.info(i.getValue());
									}
								}
							});
							if (!ecset.isEmpty())
							{
								EnchantGroupData.Data.put(path, ecset);
							}
						} catch (Exception ignored)
						{
						}
					}
				});
		for (var i : EnchantGroupData.Data.entrySet())
		{
			iwlogger.info("EnchantGroupData: Add ToolGroup " + i.getKey() + ", contains:");
			for (var j : i.getValue())
			{
				iwlogger.info(j.getRegistryKey().getValue().toString());
			}
		}
	}

	public static void handleEnchantConflictData(ResourceManager manager)
	{
		iwlogger.info("Reload EnchantConflictData");
		EnchantConflictData.Data = new HashSet<>();
		for (var i : RuneEnchantData.Data.values())
		{
			i.conflictGroups.clear();
		}
		manager.findResources(ENCHANTCONFLICT_BEGINPATH, (Identifier path) -> path.toString().endsWith(".json"))
				.forEach((identifier, resource) -> {
					try (InputStream stream = resource.getInputStream())
					{
						Gson g = new Gson();
						String s = new String(stream.readAllBytes());
						EnchantConflictData.IO m = g.fromJson(s, EnchantConflictData.IO.class);
						m.validate();
						Set<RuneEnchantData> confictEnchantmentSet = new HashSet<>();
						m.enchantments.forEach((value) -> {
							RegistryKey<Enchantment> ec = RegistryKey.of(RegistryKeys.ENCHANTMENT,
									Identifier.of(value));
							RuneEnchantData data = RuneEnchantData.Data.get(ec);
							if (data != null)
							{
								confictEnchantmentSet.add(data);
							}
						});
						if (!confictEnchantmentSet.isEmpty())
						{
							EnchantConflictData.Data.add(new EnchantConflictData(m, confictEnchantmentSet));
						}
					} catch (Exception ignored)
					{
					}
				});
		int idx = 0;
		for (var i : EnchantConflictData.Data)
		{
			iwlogger.info("EnchantConflictData: Add ConflictGroup " + idx + ", contains:");
			for (var j : i.getEnchantSet())
			{
				iwlogger.info(j.getRegistryKey().getValue().toString());
			}
		}
	}
}
