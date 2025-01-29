package org.yang.interestingworld.item.item_builder;

import net.minecraft.data.client.Model;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.datagen.itemmodel.CommonItemModelProvider;
import org.yang.interestingworld.datagen.itemmodel.ItemModelProvider;
import org.yang.interestingworld.datagen.itemmodel.ItemModelPool;
import org.yang.interestingworld.item.heart.common.CommonHeart;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;
import org.yang.interestingworld.util.Base;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;

import java.util.function.Function;

import static org.yang.interestingworld.util.IWRuneAbilityUtil.getAbility;

public class AbilityHeartItemBuilder
{
	private final Function<Item.Settings, CommonHeart> constructor;
	private final String id;
	private RegistryKey<ItemGroup> itemGroupBelong = null;
	private Function<Item, ItemModelProvider> modelProvider = null;

	public AbilityHeartItemBuilder setModel(Function<Item, ItemModelProvider> provider)
	{
		modelProvider = provider;
		return this;
	}

	public AbilityHeartItemBuilder setCommonModel(Model model)
	{
		modelProvider = item -> new CommonItemModelProvider(item, model);
		return this;
	}

	public AbilityHeartItemBuilder(Function<Item.Settings, CommonHeart> constructor, String id)
	{
		this.constructor = constructor;
		this.id = id;
	}

	public AbilityHeartItemBuilder addToItemGroup(RegistryKey<ItemGroup> itemGroup)
	{
		itemGroupBelong = itemGroup;
		return this;
	}

	public CommonHeart build()
	{
		Item.Settings settings = new Item.Settings();
		CommonHeart ret = constructor.apply(settings);
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		Registry.register(Registries.ITEM, itemID, ret);
		if (itemGroupBelong != null)
		{
			for (var idx : ret.getAbilitiesSupport())
			{
				var ability = getAbility(idx);
				if (ability != IWRuneAbilities.DEFAULT_ABILITY)
				{
					IWItemGroups.addItemToGroup((context, entries) -> {
						var wrapperOp = context.lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT);
						wrapperOp.ifPresent(wrapper -> {
							var stack = ret.getDefaultStack(ability.level());
							if (ret.setAbility(stack, ability))
							{
								stack.set(IWComponents.HEART_FLAG,
										HeartDataFlag.copyFromItemStack(stack).setMaterialLevel(ability.level()));
								entries.add(stack);
							}
						});
					}, itemGroupBelong);
				}
			}
		}
		if (modelProvider != null) ItemModelPool.addModel(modelProvider.apply(ret));
		return ret;
	}
}
