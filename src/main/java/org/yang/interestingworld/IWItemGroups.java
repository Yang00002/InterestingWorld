package org.yang.interestingworld;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.block.IWBlocks;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;
import org.yang.interestingworld.util.IWRuneAbilityUtil;

import java.util.*;
import java.util.function.BiConsumer;

import static org.yang.interestingworld.util.Base.MOD_ID;
import static org.yang.interestingworld.util.Base.iwlogger;

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

	private static ItemStack getToolDisplay()
	{
		return IWItems.NETHERITE_SWORD.getDefaultStack();
	}

	private static ItemStack getRuneDisplay()
	{
		ItemStack it = IWItems.ABILITY_RUNE.getDefaultStack();
		IWRuneAbilityUtil.setAbility(it, IWRuneAbilities.SLASHING_ABILITY);
		return it;
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

	private static void initializeItemGroup(RegistryKey<ItemGroup> groupRegistryKey, String id, ItemStack delegateItem)
	{
		Registry.register(Registries.ITEM_GROUP, groupRegistryKey,
				FabricItemGroup.builder().icon(() -> delegateItem).displayName(Text.translatable(id))
						.entries(((displayContext, entries) -> applyData(groupRegistryKey, displayContext, entries)))
						.build());
	}


	public static final RegistryKey<ItemGroup> TOOLS_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "tools_group"));

	public static final RegistryKey<ItemGroup> RUNES_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "runes_group"));

	public static final RegistryKey<ItemGroup> BLOCKS_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "blocks_group"));

	public static void initialize()
	{
		initializeItemGroup(BLOCKS_GROUP, "itemGroup.blocks_group", IWBlocks.FORGING_BLOCK.asItem().getDefaultStack());
		initializeItemGroup(RUNES_GROUP, "itemGroup.runes_group", getRuneDisplay());
		initializeItemGroup(TOOLS_GROUP, "itemGroup.tools_group", getToolDisplay());
		data = Collections.unmodifiableMap(data);
	}
}
