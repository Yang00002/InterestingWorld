package org.yang.interestingworld.item;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.datagen.itemmodel.ItemModelIdBuilder;
import org.yang.interestingworld.datagen.itemmodel.ItemModelProvider;
import org.yang.interestingworld.datagen.itemmodel.LocatedItemModelProvider;
import org.yang.interestingworld.enchant.IWEnchantments;
import org.yang.interestingworld.item.heart.base.*;
import org.yang.interestingworld.item.heart.common.BloodHeart;
import org.yang.interestingworld.item.heart.common.CherryHeart;
import org.yang.interestingworld.item.heart.common.TinkerHeart;
import org.yang.interestingworld.item.item_builder.AbilityHeartItemBuilder;
import org.yang.interestingworld.item.item_builder.CommonItemBuilder;
import org.yang.interestingworld.item.item_builder.EnergyToolItemBuilder;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.item.tool.sword.EnergySwordItem;
import org.yang.interestingworld.rune_ability.AbstractRuneAbility;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IWItems
{
	public static final Item STICK = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_stick",
			ToolMaterials.WOOD).setBaseAttackDamage(3).setEnergy(5).setRegen(0.1f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.STICK)).build();
	public static final Item BLAZEROD = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_blazerod",
			ToolMaterials.STONE).setBaseAttackDamage(4).setEnergy(10).setRegen(0.15f)
			.addDefaultEnchant(IWEnchantments.FIRE_ASPECT_entry, 1)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.BLAZE_ROD)).build();
	public static final Item STONE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "stone_sword",
			ToolMaterials.STONE).setBaseAttackDamage(5).setBaseAttackSpeed(1.6).setEnergy(10).setRegen(0.15f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.STONE_SWORD)).build();
	public static final Item IRON_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "iron_sword",
			ToolMaterials.IRON).setBaseAttackDamage(6).setBaseAttackSpeed(1.6).setEnergy(15).setRegen(0.2f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.IRON_SWORD)).build();
	public static final Item GOLDEN_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "golden_sword",
			ToolMaterials.GOLD).setBaseAttackDamage(4).setBaseAttackSpeed(2.0).setEnergy(10).setRegen(1.0f)
			.addDefaultEnchant(IWEnchantments.FAST_HIT_entry, 1)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.GOLDEN_SWORD)).build();
	public static final Item DIAMOND_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "diamond_sword",
			ToolMaterials.DIAMOND).setBaseAttackDamage(7).setBaseAttackSpeed(1.6).setEnergy(20).setRegen(0.25f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.DIAMOND_SWORD)).build();
	public static final Item NETHERITE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "netherite_sword",
			ToolMaterials.NETHERITE).setBaseAttackDamage(8).setBaseAttackSpeed(1.6).setEnergy(25).setRegen(0.3f)
			.setFireResistence().setModel(getModelForEnergyTool(Models.HANDHELD, Items.NETHERITE_SWORD)).build();
	public static final Item HEART = new CommonItemBuilder(Item::new, "heart").addToItemGroup(
			IWItemGroups.IngredientGroup).setCommonModel(Models.GENERATED).build();
	public static final CopperHeartItem COPPER_HEART = (CopperHeartItem) new CommonItemBuilder(CopperHeartItem::new,
			"copper_heart").addToItemGroup(IWItemGroups.IngredientGroup).setModel(
			(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
					.addPredictorAndPush(1,
							new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
									Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))).build();
	public static final IronHeartItem IRON_HEART = (IronHeartItem) new CommonItemBuilder(IronHeartItem::new,
			"iron_heart").addToItemGroup(IWItemGroups.IngredientGroup).setModel(
			(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
					.addPredictorAndPush(1,
							new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
									Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))).build();
	public static final GoldHeartItem GOLD_HEART = (GoldHeartItem) new CommonItemBuilder(GoldHeartItem::new,
			"gold_heart").addToItemGroup(IWItemGroups.IngredientGroup).setModel(
			(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
					.addPredictorAndPush(1,
							new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
									Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))).build();
	public static final DiamondHeartItem DIAMOND_HEART =
			(DiamondHeartItem) new CommonItemBuilder(DiamondHeartItem::new,
			"diamond_heart").addToItemGroup(IWItemGroups.IngredientGroup).setModel(
			(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
					.addPredictorAndPush(1,
							new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
									Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))).build();
	public static final NetheriteHeartItem NETHERITE_HEART = (NetheriteHeartItem) new CommonItemBuilder(
			NetheriteHeartItem::new, "netherite_heart").addToItemGroup(IWItemGroups.IngredientGroup).setModel(
			(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
					.addPredictorAndPush(1,
							new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
									Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))).build();
	public static final EnderiteHeartItem ENDERITE_HEART = (EnderiteHeartItem) new CommonItemBuilder(
			EnderiteHeartItem::new, "enderite_heart").addToItemGroup(IWItemGroups.IngredientGroup).setModel(
			(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
					.addPredictorAndPush(1,
							new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
									Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))).build();
	public static final VoidalloyHeartItem VOIDALLOY_HEART = (VoidalloyHeartItem) new CommonItemBuilder(
			VoidalloyHeartItem::new, "voidalloy_heart").addToItemGroup(IWItemGroups.IngredientGroup).setModel(
			(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
					.addPredictorAndPush(1,
							new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
									Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))).build();
	public static final TinkerHeart TINKER_HEART = (TinkerHeart) new AbilityHeartItemBuilder(TinkerHeart::new,
			"tinker_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(Models.GENERATED).build();
	public static final CherryHeart CHERRY_HEART = (CherryHeart) new AbilityHeartItemBuilder(CherryHeart::new,
			"cherry_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(Models.GENERATED).build();
	public static final BloodHeart BLOOD_HEART = (BloodHeart) new AbilityHeartItemBuilder(BloodHeart::new,
			"blood_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(Models.GENERATED).build();

	private static Map<Short, AbstractRuneAbility> toolModelMap = null;

	private static Function<Item, ItemModelProvider> getModelForEnergyTool(Model model, Item baseTexture)
	{
		if (toolModelMap == null)
		{
			toolModelMap = new HashMap<>();
			putToolModelMap(IWRuneAbilities.SLASHING_ABILITY);
			putToolModelMap(IWRuneAbilities.SWEETCURSE_ABILITY);
			putToolModelMap(IWRuneAbilities.INFINITECURSE_ABILITY);
			IWRuneAbilities.forEach(ability -> {
				if (!toolModelMap.containsKey(ability.toolIndex)) putToolModelMap(ability);
			});
		}
		return item -> {
			LocatedItemModelProvider provider = new LocatedItemModelProvider(item, model);
			provider.setTextureAItemTexture(baseTexture);
			for (var i : toolModelMap.entrySet())
			{
				var k = i.getKey();
				var v = i.getValue();
				Identifier item2 = ItemModelIdBuilder.ofIW(v.id()).addDirectory(Registries.ITEM.getId(item).getPath())
						.build();
				LocatedItemModelProvider predictorModel = new LocatedItemModelProvider(item2, model);
				predictorModel.setTexture(item2.getNamespace(), item2.getPath());
				provider.addPredictorAndPush(k, predictorModel);
			}
			return provider;
		};
	}

	private static Function<Item, ItemModelProvider> getModelForEnergyTool(Model model)
	{
		if (toolModelMap == null)
		{
			toolModelMap = new HashMap<>();
			putToolModelMap(IWRuneAbilities.SLASHING_ABILITY);
			putToolModelMap(IWRuneAbilities.SWEETCURSE_ABILITY);
			putToolModelMap(IWRuneAbilities.INFINITECURSE_ABILITY);
			IWRuneAbilities.forEach(ability -> {
				if (!toolModelMap.containsKey(ability.toolIndex)) putToolModelMap(ability);
			});
		}
		return item -> {
			LocatedItemModelProvider provider = new LocatedItemModelProvider(item, model);
			provider.setTextureAItemTexture(item);
			for (var i : toolModelMap.entrySet())
			{
				var k = i.getKey();
				var v = i.getValue();
				Identifier item2 = ItemModelIdBuilder.ofIW(v.id()).addDirectory(Registries.ITEM.getId(item).getPath())
						.build();
				LocatedItemModelProvider predictorModel = new LocatedItemModelProvider(item2, model);
				predictorModel.setTexture(item2.getNamespace(), item2.getPath());
				provider.addPredictorAndPush(k, predictorModel);
			}
			return provider;
		};
	}

	private static void putToolModelMap(AbstractRuneAbility ability)
	{
		toolModelMap.put(ability.toolIndex, ability);
	}

	public static void initialize()
	{
		toolModelMap = null;
	}

}
