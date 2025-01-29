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


	public static final RegistryKey<ItemGroup> TOOLS_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "tools_group"));
	public static final RegistryKey<ItemGroup> RUNES_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "runes_group"));
	public static final RegistryKey<ItemGroup> IngredientGroup = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "ingredients"));
	public static final RegistryKey<ItemGroup> BLOCKS_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "blocks_group"));

	public static void initialize()
	{
		initializeItemGroup(BLOCKS_GROUP, IWBlocks.FORGING_BLOCK.asItem().getDefaultStack());
		initializeItemGroup(IngredientGroup, IWItems.HEART.getDefaultStack());
		initializeItemGroup(RUNES_GROUP, getAbilityDisplay());
		initializeItemGroup(TOOLS_GROUP, IWItems.NETHERITE_SWORD.getDefaultStack());
		data = Collections.unmodifiableMap(data);
	}
}
