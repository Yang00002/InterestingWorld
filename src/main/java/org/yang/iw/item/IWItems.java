package org.yang.iw.item;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.iw.IWItemGroups;
import org.yang.iw.datagen.itemmodel.ItemModelIdBuilder;
import org.yang.iw.datagen.itemmodel.ItemModelProvider;
import org.yang.iw.datagen.itemmodel.LocatedItemModelProvider;
import org.yang.iw.enchant.IWEnchantments;
import org.yang.iw.item.heart.base.*;
import org.yang.iw.item.heart.common.BloodHeart;
import org.yang.iw.item.heart.common.CherryHeart;
import org.yang.iw.item.heart.common.TinkerHeart;
import org.yang.iw.item.item_builder.AbilityHeartItemBuilder;
import org.yang.iw.item.item_builder.CommonItemBuilder;
import org.yang.iw.item.item_builder.EnergyToolItemBuilder;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.item.tool.sword.EnergySwordItem;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.rune_ability.IWRuneAbilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IWItems
{
	public static final Item STICK = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_stick",
			ToolMaterials.WOOD).setBaseAttackDamage(3).setEnergy(5).setRegen(0.1f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.STICK)).setTranslation("棍子").build();
	public static final Item BLAZEROD = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_blazerod",
			ToolMaterials.STONE).setBaseAttackDamage(4).setEnergy(10).setRegen(0.15f)
			.addDefaultEnchant(IWEnchantments.ENTRY_FIRE_ASPECT, 1)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.BLAZE_ROD)).setTranslation("烈焰棒子").build();
	public static final Item STONE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "stone_sword",
			ToolMaterials.STONE).setBaseAttackDamage(5).setBaseAttackSpeed(1.6).setEnergy(10).setRegen(0.15f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.STONE_SWORD)).setTranslation("石剑").build();
	public static final Item IRON_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "iron_sword",
			ToolMaterials.IRON).setBaseAttackDamage(6).setBaseAttackSpeed(1.6).setEnergy(15).setRegen(0.2f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.IRON_SWORD)).setTranslation("铁剑").build();
	public static final Item GOLDEN_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "golden_sword",
			ToolMaterials.GOLD).setBaseAttackDamage(4).setBaseAttackSpeed(2.0).setEnergy(10).setRegen(1.0f)
			.addDefaultEnchant(IWEnchantments.ENTRY_FAST_HIT, 1)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.GOLDEN_SWORD)).setTranslation("金剑").build();
	public static final Item DIAMOND_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "diamond_sword",
			ToolMaterials.DIAMOND).setBaseAttackDamage(7).setBaseAttackSpeed(1.6).setEnergy(20).setRegen(0.25f)
			.setModel(getModelForEnergyTool(Models.HANDHELD, Items.DIAMOND_SWORD)).setTranslation("钻石剑").build();
	public static final Item NETHERITE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "netherite_sword",
			ToolMaterials.NETHERITE).setBaseAttackDamage(8).setBaseAttackSpeed(1.6).setEnergy(25).setRegen(0.3f)
			.setFireResistence().setModel(getModelForEnergyTool(Models.HANDHELD, Items.NETHERITE_SWORD))
			.setTranslation("下界合金剑").build();
	public static final Item HEART = new CommonItemBuilder(Item::new, "heart").addToItemGroup(
			IWItemGroups.INGREDIENTS_GROUP).setCommonModel(Models.GENERATED).setTranslation("心").build();
	public static final CopperHeartItem COPPER_HEART = (CopperHeartItem) new CommonItemBuilder(CopperHeartItem::new,
			"copper_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
							.addPredictorAndPush(1,
									new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
											Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart")))
			.setTranslation("铜心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final IronHeartItem IRON_HEART = (IronHeartItem) new CommonItemBuilder(IronHeartItem::new,
			"iron_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
							.addPredictorAndPush(1,
									new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
											Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart")))
			.setTranslation("铁心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final GoldHeartItem GOLD_HEART = (GoldHeartItem) new CommonItemBuilder(GoldHeartItem::new,
			"gold_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
							.addPredictorAndPush(1,
									new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
											Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart")))
			.setTranslation("金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final DiamondHeartItem DIAMOND_HEART =
			(DiamondHeartItem) new CommonItemBuilder(DiamondHeartItem::new,
			"diamond_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
							.addPredictorAndPush(1,
									new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
											Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart")))
			.setTranslation("钻石心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final NetheriteHeartItem NETHERITE_HEART = (NetheriteHeartItem) new CommonItemBuilder(
			NetheriteHeartItem::new, "netherite_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
							.addPredictorAndPush(1,
									new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
											Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart")))
			.setTranslation("下界合金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final EnderiteHeartItem ENDERITE_HEART = (EnderiteHeartItem) new CommonItemBuilder(
			EnderiteHeartItem::new, "enderite_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
							.addPredictorAndPush(1,
									new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
											Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart")))
			.setTranslation("末影合金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final VoidalloyHeartItem VOIDALLOY_HEART = (VoidalloyHeartItem) new CommonItemBuilder(
			VoidalloyHeartItem::new, "voidalloy_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					(it) -> new LocatedItemModelProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
							.addPredictorAndPush(1,
									new LocatedItemModelProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
											Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart")))
			.setTranslation("虚空合金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final TinkerHeart TINKER_HEART = (TinkerHeart) new AbilityHeartItemBuilder(TinkerHeart::new,
			"tinker_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(Models.GENERATED)
			.setTranslation("工匠之心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final CherryHeart CHERRY_HEART = (CherryHeart) new AbilityHeartItemBuilder(CherryHeart::new,
			"cherry_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(Models.GENERATED)
			.setTranslation("樱之心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final BloodHeart BLOOD_HEART = (BloodHeart) new AbilityHeartItemBuilder(BloodHeart::new,
			"blood_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(Models.GENERATED)
			.setTranslation("血之心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();

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
