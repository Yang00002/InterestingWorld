package org.yang.iw;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.iw.ability.IWAbilities;
import org.yang.iw.api.register.AfterInitializeExecutor;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.block.IWBlocks;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.item.IWItems;
import org.yang.iw.upgrade.IWUpgrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.yang.iw.util.Base.MOD_ID;
import static org.yang.iw.util.Base.iwlogger;

@AfterInitializeExecutor
@DataGenSupplier
@IndependentRegister
public class IWItemGroups
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
	}

	private static Map<RegistryKey<ItemGroup>, List<BiConsumer<ItemGroup.DisplayContext, ItemGroup.Entries>>> data =
			new HashMap<>();
	private static boolean locked = false;

	public static void addItemToGroup(BiConsumer<ItemGroup.DisplayContext, ItemGroup.Entries> itemInGroupInitializer,
									  RegistryKey<ItemGroup> groupRegistryKey)
	{
		if (locked) iwlogger.fatal("ItemGroup Locked! Don't add itemStack to it!");
		List<BiConsumer<ItemGroup.DisplayContext, ItemGroup.Entries>> l;
		if (data.containsKey(groupRegistryKey))
		{
			l = data.get(groupRegistryKey);
		}
		else l = new ArrayList<>();
		l.add(itemInGroupInitializer);
		data.put(groupRegistryKey, l);
	}

	private static void applyData(RegistryKey<ItemGroup> groupRegistryKey, ItemGroup.DisplayContext displayContext,
								  ItemGroup.Entries entries)
	{
		iwlogger.info("adding itemGroup " + groupRegistryKey.getValue().toUnderscoreSeparatedString());
		if (data.containsKey(groupRegistryKey))
		{
			var list = data.get(groupRegistryKey);
			for (var i : list)
				i.accept(displayContext, entries);
		}
	}

	private static void initializeItemGroup(RegistryKey<ItemGroup> groupRegistryKey, ItemStack delegateItem)
	{
		Registry.register(Registries.ITEM_GROUP, groupRegistryKey, FabricItemGroup.builder().icon(() -> delegateItem)
				.displayName(Text.translatable(groupRegistryKey.getValue().toTranslationKey()))
				.entries(((displayContext, entries) -> applyData(groupRegistryKey, displayContext, entries))).build());
	}

	private static RegistryKey<ItemGroup> itemGroup(String id, String translation,
													Supplier<ItemStack> delegateItemStackGetter)
	{
		var registryKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, id));
		Registry.register(Registries.ITEM_GROUP, registryKey, FabricItemGroup.builder().icon(delegateItemStackGetter)
				.displayName(Text.translatable(registryKey.getValue().toTranslationKey()))
				.entries(((displayContext, entries) -> applyData(registryKey, displayContext, entries))).build());
		TranslationPool.addString("%s.%s".formatted(MOD_ID, id), translation);
		return registryKey;
	}

	/**
	 * lambda 不能被替换为方法引用
	 */
	public static final RegistryKey<ItemGroup> TOOLS_GROUP = itemGroup("tools_group", "IW: 工具",
			() -> IWItems.NETHERITE_SWORD.getDefaultStack());
	public static final RegistryKey<ItemGroup> RUNES_GROUP = itemGroup("runes_group", "IW：能力", () -> {
		var s = IWItems.BLOOD_HEART.getDefaultStack();
		IWItems.BLOOD_HEART.setAbility(s, IWAbilities.EVISCERATE_ABILITY);
		return s;
	});
	public static final RegistryKey<ItemGroup> UPGRADE_GROUP = itemGroup("upgrades_group", "IW：升级",
			() -> Registries.ITEM.get(
							Identifier.of(MOD_ID, IWUpgrades.getItemIdOfUpgrade(IWUpgrades.SWEEPING3_UPGRADE)))
					.getDefaultStack());
	public static final RegistryKey<ItemGroup> INGREDIENTS_GROUP = itemGroup("ingredients_group", "IW：材料",
			() -> IWItems.HEART.getDefaultStack());
	public static final RegistryKey<ItemGroup> BLOCKS_GROUP = itemGroup("blocks_group", "IW：方块",
			() -> IWBlocks.FORGING_BLOCK.asItem().getDefaultStack());


	public static void initialize()
	{
	}

	static
	{
		LoadTime.setLoaded(IWItemGroups.class);
	}

	public static void afterInitialize()
	{
		LoadTime.assertTime(LoadTime.Type.AFTER_INITIALIZE);
		locked = true;
	}

}
