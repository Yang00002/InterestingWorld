package org.yang.iw.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import org.yang.iw.IWItemGroups;
import org.yang.iw.datagen.itemmodel.CustomItemModelDefinitionProvider;
import org.yang.iw.datagen.itemmodel.ItemModelProvider;
import org.yang.iw.datagen.itemmodel.server.ItemModelDefinition;
import org.yang.iw.datagen.itemmodel.server.ModelParents;
import org.yang.iw.datagen.itemmodel.server.RawItemModel;
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
import org.yang.iw.util.Base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IWItems
{

	public static final Item STICK = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_stick",
			63).setBaseAttackDamage(3).setEnergy(5).setRegen(0.1f)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.STICK)).setTranslation("棍子").build();
	public static final Item BLAZEROD = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_blazerod",
			127).setBaseAttackDamage(4).setEnergy(10).setRegen(0.15f)
			.addDefaultEnchant(IWEnchantments.ENTRY_FIRE_ASPECT, 1)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.BLAZE_ROD)).setTranslation("烈焰棒子")
			.setToolComponent(new ToolComponent(
					List.of(ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()),
							2.0F)), 1.0F, 1)).build();

	public static final Item WOODEN_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "wooden_sword",
			63).setBaseAttackDamage(4).setBaseAttackSpeed(1.6).setEnergy(5).setRegen(0.1f).setModel(
					i -> new CustomItemModelDefinitionProvider(i,
							ItemModelDefinition.of(RawItemModel.of(Items.WOODEN_SWORD))))
			.setTranslation("木剑").setToolComponent(EnergySwordItem.createToolComponent(0.5f)).build();
	public static final Item STONE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "stone_sword",
			127).setBaseAttackDamage(5).setBaseAttackSpeed(1.6).setEnergy(10).setRegen(0.15f)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.STONE_SWORD)).setTranslation("石剑")
			.setToolComponent(EnergySwordItem.createToolComponent(0.75f)).build();
	public static final Item IRON_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "iron_sword",
			255).setBaseAttackDamage(6).setBaseAttackSpeed(1.6).setEnergy(15).setRegen(0.2f)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.IRON_SWORD)).setTranslation("铁剑")
			.setToolComponent(EnergySwordItem.createToolComponent(1.0f)).build();
	public static final Item GOLDEN_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "golden_sword",
			31).setBaseAttackDamage(4).setBaseAttackSpeed(2.0).setEnergy(10).setRegen(1.0f)
			.addDefaultEnchant(IWEnchantments.ENTRY_FAST_HIT, 1)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.GOLDEN_SWORD)).setTranslation("金剑")
			.setToolComponent(EnergySwordItem.createToolComponent(2f)).build();
	public static final Item DIAMOND_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "diamond_sword",
			1023).setBaseAttackDamage(7).setBaseAttackSpeed(1.6).setEnergy(20).setRegen(0.25f)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.DIAMOND_SWORD)).setTranslation("钻石剑")
			.setToolComponent(EnergySwordItem.createToolComponent(1.25f)).build();
	public static final Item NETHERITE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "netherite_sword",
			2047).setBaseAttackDamage(8).setBaseAttackSpeed(1.6).setEnergy(25).setRegen(0.3f).setFireResistence()
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.NETHERITE_SWORD))
			.setToolComponent(EnergySwordItem.createToolComponent(1.5f)).setTranslation("下界合金剑").build();
	public static final Item HEART = new CommonItemBuilder(Item::new, "heart").addToItemGroup(
			IWItemGroups.INGREDIENTS_GROUP).setCommonModel(ModelParents.GENERATED).setTranslation("心").build();

	/**
	 * (it) -> new LocatedItemModelDefinitionProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
	 * .addPredictorAndPush(1,
	 * new LocatedItemModelDefinitionProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
	 * Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))
	 */
	public static final CopperHeartItem COPPER_HEART = (CopperHeartItem) new CommonItemBuilder(CopperHeartItem::new,
			"copper_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					item -> new CustomItemModelDefinitionProvider(item,
							ItemModelDefinition.heartEnchant(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("铜心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final IronHeartItem IRON_HEART = (IronHeartItem) new CommonItemBuilder(IronHeartItem::new,
			"iron_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					item -> new CustomItemModelDefinitionProvider(item,
							ItemModelDefinition.heartEnchant(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("铁心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final GoldHeartItem GOLD_HEART = (GoldHeartItem) new CommonItemBuilder(GoldHeartItem::new,
			"gold_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					item -> new CustomItemModelDefinitionProvider(item,
							ItemModelDefinition.heartEnchant(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final DiamondHeartItem DIAMOND_HEART =
			(DiamondHeartItem) new CommonItemBuilder(DiamondHeartItem::new,
			"diamond_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					item -> new CustomItemModelDefinitionProvider(item,
							ItemModelDefinition.heartEnchant(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("钻石心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final NetheriteHeartItem NETHERITE_HEART = (NetheriteHeartItem) new CommonItemBuilder(
			NetheriteHeartItem::new, "netherite_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					item -> new CustomItemModelDefinitionProvider(item,
							ItemModelDefinition.heartEnchant(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("下界合金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final EnderiteHeartItem ENDERITE_HEART = (EnderiteHeartItem) new CommonItemBuilder(
			EnderiteHeartItem::new, "enderite_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					item -> new CustomItemModelDefinitionProvider(item,
							ItemModelDefinition.heartEnchant(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("末影合金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final VoidalloyHeartItem VOIDALLOY_HEART = (VoidalloyHeartItem) new CommonItemBuilder(
			VoidalloyHeartItem::new, "voidalloy_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setModel(
					item -> new CustomItemModelDefinitionProvider(item,
							ItemModelDefinition.heartEnchant(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("虚空合金心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final TinkerHeart TINKER_HEART = (TinkerHeart) new AbilityHeartItemBuilder(TinkerHeart::new,
			"tinker_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(ModelParents.GENERATED)
			.setTranslation("工匠之心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final CherryHeart CHERRY_HEART = (CherryHeart) new AbilityHeartItemBuilder(CherryHeart::new,
			"cherry_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(ModelParents.GENERATED)
			.setTranslation("樱之心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();
	public static final BloodHeart BLOOD_HEART = (BloodHeart) new AbilityHeartItemBuilder(BloodHeart::new,
			"blood_heart").addToItemGroup(IWItemGroups.RUNES_GROUP).setCommonModel(ModelParents.GENERATED)
			.setTranslation("血之心").addTag(IWItemTags.CanEnchantAsPreEnchantHeart).build();

	private static Map<Short, AbstractRuneAbility> toolModelMap = null;

	private static Function<Item, ItemModelProvider> getModelForEnergyTool(ModelParents model, Item baseTexture)
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
			Map<Float, ItemModelDefinition> definitionMap = new HashMap<>();
			for (var i : toolModelMap.entrySet())
			{
				var k = i.getKey();
				var v = i.getValue();
				definitionMap.put((float) k, ItemModelDefinition.of(RawItemModel.simple(Base.MOD_ID, v.id(), model)
						.setIdFormat(Registries.ITEM.getId(item).getPath() + "/%s").upload()));
			}
			return new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.abilityIndex(ItemModelDefinition.of(RawItemModel.of(baseTexture)),
							definitionMap));
		};
	}

	private static Function<Item, ItemModelProvider> getModelForEnergyTool(ModelParents model)
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
			Map<Float, ItemModelDefinition> definitionMap = new HashMap<>();
			for (var i : toolModelMap.entrySet())
			{
				var k = i.getKey();
				var v = i.getValue();
				definitionMap.put((float) k, ItemModelDefinition.of(RawItemModel.simple(Base.MOD_ID, v.id(), model)
						.setIdFormat(Registries.ITEM.getId(item).getPath() + "/%s").upload()));
			}
			return new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.abilityIndex(ItemModelDefinition.of(RawItemModel.of(item)), definitionMap));
		};
	}

	private static RegistryEntryList.Named<Block> getEntryListOfBlockTag(TagKey<Block> blockTagKey)
	{
		return Registries.createEntryLookup(Registries.BLOCK).getOrThrow(blockTagKey);
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
