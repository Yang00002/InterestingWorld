package org.yang.interestingworld;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.yang.interestingworld.IWUtil.EnergyTool.maxEnergy;


public class IWItemGroups
{
	private interface ItemInGroupInitializer
	{
		void construct(ItemGroup.DisplayContext displayContext, ItemGroup.Entries entries);
	}

	public static class EnergyToolInitializer implements ItemInGroupInitializer
	{
		Item item = null;
		Map<RegistryKey<Enchantment>, Integer> enchantments = null;

		Map<RegistryKey<Enchantment>, Integer> default_enchantments = null;
		IWAbstractRuneAbility ability = null;

		private EnergyToolInitializer()
		{

		}

		public EnergyToolInitializer addEnchantment(RegistryKey<Enchantment> ec, int level)
		{
			if (level > 0)
			{
				if (enchantments == null)
				{
					enchantments = new HashMap<>();
				}
				enchantments.put(ec, level);
			}
			return this;
		}

		public EnergyToolInitializer addDefaultEnchantment(RegistryKey<Enchantment> ec, int level)
		{
			if (level > 0)
			{
				if (default_enchantments == null)
				{
					default_enchantments = new HashMap<>();
				}
				default_enchantments.put(ec, level);
			}
			return this;
		}

		public EnergyToolInitializer setAbility(IWAbstractRuneAbility ab)
		{
			ability = ab;
			return this;
		}

		public static EnergyToolInitializer getInstance(Item item)
		{
			EnergyToolInitializer i = new EnergyToolInitializer();
			i.item = item;
			return i;
		}

		@Override
		public void construct(ItemGroup.DisplayContext displayContext, ItemGroup.Entries entries)
		{
			if (item != null && item instanceof EnergyToolItem)
			{
				ItemStack stack = item.getDefaultStack();
				stack.set(IWComponents.CURRENT_ENERGY, stack.getOrDefault(IWComponents.MAX_ENERGY, 0f));
				if (enchantments != null)
				{
					var wrapper = displayContext.lookup().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
					enchantments.forEach((enchantment, level) -> {
						if (level > 0) stack.addEnchantment(wrapper.getOrThrow(enchantment), level);
					});
				}
				if (default_enchantments != null)
				{
					var wrapper = displayContext.lookup().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
					var cp = stack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
					ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(cp);
					default_enchantments.forEach((enchantment, level) -> {
						if (level > 0) builder.add(wrapper.getOrThrow(enchantment), level);
					});
					stack.set(IWComponents.DEFAULT_ENCHANTMENTS, builder.build());
				}
				if (ability != null) IWUtil.RuneAbility.setAbility(stack, ability);
				entries.add(stack);
			}
		}
	}

	public static class CommonItemInitializer implements ItemInGroupInitializer
	{
		Item item = null;

		private CommonItemInitializer()
		{

		}

		public static CommonItemInitializer getInstance(Item item)
		{
			CommonItemInitializer i = new CommonItemInitializer();
			i.item = item;
			return i;
		}

		@Override
		public void construct(ItemGroup.DisplayContext displayContext, ItemGroup.Entries entries)
		{
			if (item != null)
			{
				ItemStack stack = item.getDefaultStack();
				entries.add(stack);
			}
		}
	}

	public static class AbilityRuneItemInitializer implements ItemInGroupInitializer
	{
		IWAbstractRuneAbility ability;

		private AbilityRuneItemInitializer()
		{

		}

		public static AbilityRuneItemInitializer getInstance(IWAbstractRuneAbility ab)
		{
			AbilityRuneItemInitializer i = new AbilityRuneItemInitializer();
			i.ability = ab;
			return i;
		}

		@Override
		public void construct(ItemGroup.DisplayContext displayContext, ItemGroup.Entries entries)
		{
			ItemStack stack = IWItems.COMMON_ABILITY_RUNE.getDefaultStack();
			IWUtil.RuneAbility.setAbility(stack, ability);
			entries.add(stack);
		}
	}

	private static Map<RegistryKey<ItemGroup>, LinkedList<ItemInGroupInitializer>> data = null;

	public static void addItemToGroup(ItemInGroupInitializer itemInGroupInitializer,
									  RegistryKey<ItemGroup> groupRegistryKey)
	{
		if (data == null)
		{
			data = new HashMap<>();
		}
		LinkedList<ItemInGroupInitializer> l;
		if (data.containsKey(groupRegistryKey))
		{
			l = data.get(groupRegistryKey);
		}
		else l = new LinkedList<>();
		l.add(itemInGroupInitializer);
		data.put(groupRegistryKey, l);
	}

	private static ItemStack getToolDisplay()
	{
		ItemStack it = IWItems.BLOOD_SWORD.getDefaultStack();
		it.set(IWComponents.CURRENT_ENERGY, maxEnergy(it));
		return it;
	}

	private static ItemStack getRuneDisplay()
	{
		ItemStack it = IWItems.COMMON_ABILITY_RUNE.getDefaultStack();
		IWUtil.RuneAbility.setAbility(it, IWRuneAbilitys.SWEEP_ABILITY);
		return it;
	}

	private static void applyData(RegistryKey<ItemGroup> groupRegistryKey, ItemGroup.DisplayContext displayContext,
								  ItemGroup.Entries entries)
	{
		if (data != null)
		{
			if (data.containsKey(groupRegistryKey))
			{
				var list = data.get(groupRegistryKey);
				for (var i : list)
					i.construct(displayContext, entries);
			}
			data.remove(groupRegistryKey);
			if (data.isEmpty()) data = null;
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
			Identifier.of(IWUtil.Base.MOD_ID, "tools_group"));

	public static final RegistryKey<ItemGroup> RUNES_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(IWUtil.Base.MOD_ID, "runes_group"));

	public static final RegistryKey<ItemGroup> BLOCKS_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(),
			Identifier.of(IWUtil.Base.MOD_ID, "blocks_group"));

	public static void initialize()
	{
		initializeItemGroup(BLOCKS_GROUP, "itemGroup.blocks_group", IWBlocks.FORGING_BLOCK.asItem().getDefaultStack());
		initializeItemGroup(RUNES_GROUP, "itemGroup.runes_group", getRuneDisplay());
		initializeItemGroup(TOOLS_GROUP, "itemGroup.tools_group", getToolDisplay());
	}
}
