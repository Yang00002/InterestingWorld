package org.yang.iw;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.iw.block.IWBlocks;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.item.IWItems;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.rune_upgrade.IWRuneUpgrades;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.yang.iw.util.Base.MOD_ID;
import static org.yang.iw.util.Base.iwlogger;

public class IWItemGroups
{
	private static Map<RegistryKey<ItemGroup>, List<BiConsumer<ItemGroup.DisplayContext, ItemGroup.Entries>>> data =
			new HashMap<>();


	public static void addItemToGroup(BiConsumer<ItemGroup.DisplayContext, ItemGroup.Entries> itemInGroupInitializer,
									  RegistryKey<ItemGroup> groupRegistryKey)
	{
		if (data == null)
		{
			data = new HashMap<>();
		}
		List<BiConsumer<ItemGroup.DisplayContext, ItemGroup.Entries>> l;
		if (data.containsKey(groupRegistryKey))
		{
			l = data.get(groupRegistryKey);
		}
		else l = new ArrayList<>();
		l.add(itemInGroupInitializer);
		data.put(groupRegistryKey, l);
	}

	private static ItemStack getAbilityDisplay()
	{
		var stack = IWItems.BLOOD_HEART.getDefaultStack();
		IWItems.BLOOD_HEART.setAbility(stack, IWRuneAbilities.EVISCERATE_ABILITY);
		return stack;
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

	private static Map<RegistryKey<ItemGroup>, Supplier<ItemStack>> itemGroups = new LinkedHashMap<>();

	private static RegistryKey<ItemGroup> itemGroup(String id, String translation,
													Supplier<ItemStack> delegateItemStackGetter)
	{
		var r = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, id));
		itemGroups.put(r, delegateItemStackGetter);
		TranslationPool.addString("%s.%s".formatted(MOD_ID, id), translation);
		return r;
	}

	/**
	 * lambda 不能被替换为方法引用
	 */
	public static final RegistryKey<ItemGroup> TOOLS_GROUP = itemGroup("tools_group", "IW: 工具",
			() -> IWItems.NETHERITE_SWORD.getDefaultStack());
	public static final RegistryKey<ItemGroup> RUNES_GROUP = itemGroup("runes_group", "IW：能力", () -> {
		var s = IWItems.BLOOD_HEART.getDefaultStack();
		IWItems.BLOOD_HEART.setAbility(s, IWRuneAbilities.EVISCERATE_ABILITY);
		return s;
	});
	public static final RegistryKey<ItemGroup> UPGRADE_GROUP = itemGroup("upgrades_group", "IW：升级",
			() -> Registries.ITEM.get(
							Identifier.of(MOD_ID, IWRuneUpgrades.getItemIdOfUpgrade(IWRuneUpgrades.SWEEPING3_UPGRADE)))
					.getDefaultStack());
	public static final RegistryKey<ItemGroup> INGREDIENTS_GROUP = itemGroup("ingredients_group", "IW：材料",
			() -> IWItems.HEART.getDefaultStack());
	public static final RegistryKey<ItemGroup> BLOCKS_GROUP = itemGroup("blocks_group", "IW：方块",
			() -> IWBlocks.FORGING_BLOCK.asItem().getDefaultStack());

	public static void initialize()
	{
		for (var kv : itemGroups.entrySet())
		{
			initializeItemGroup(kv.getKey(), kv.getValue().get());
		}
		itemGroups = null;
		data = Collections.unmodifiableMap(data);
	}
}
