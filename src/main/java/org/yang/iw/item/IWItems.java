package org.yang.iw.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import org.yang.iw.IWItemGroups;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.ability.IWAbilities;
import org.yang.iw.api.register.DependRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.boost.IWBoosts;
import org.yang.iw.boost.pool.TableBoostPool;
import org.yang.iw.component.IWComponents;
import org.yang.iw.component.ToolMaterialComponent;
import org.yang.iw.datagen.itemmodel.CustomItemModelDefinitionProvider;
import org.yang.iw.datagen.itemmodel.ItemModelProvider;
import org.yang.iw.datagen.itemmodel.server.ItemModelDefinition;
import org.yang.iw.datagen.itemmodel.server.ModelParents;
import org.yang.iw.datagen.itemmodel.server.RawItemModel;
import org.yang.iw.item.forge_template.SwordForgeTemplateItem;
import org.yang.iw.item.heart.BaseHeart;
import org.yang.iw.item.heart.BloodHeart;
import org.yang.iw.item.heart.CherryHeart;
import org.yang.iw.item.heart.TinkerHeart;
import org.yang.iw.item.item_builder.AbilityHeartItemBuilder;
import org.yang.iw.item.item_builder.CommonItemBuilder;
import org.yang.iw.item.item_builder.EnergyToolItemBuilder;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.item.tool.sword.EnergySwordItem;
import org.yang.iw.tool.material.IWToolMaterials;
import org.yang.iw.tool.material.ToolMaterial;
import org.yang.iw.util.Base;
import org.yang.iw.util.style.Color;

import java.util.*;
import java.util.function.Function;

import static org.yang.iw.util.Base.iwlogger;

@DependRegister(depends = {IWItemGroups.class, IWComponents.class})
public class IWItems
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
		LoadTime.assertLoaded(IWItemGroups.class);
		LoadTime.assertLoaded(IWComponents.class);
	}

	public static final Item SWORD = new CommonItemBuilder(
			settings -> new EnergySwordItem(settings, TableBoostPool.POOL_SWORD)
			{
				@Override
				public Text getName(ItemStack stack)
				{
					return stack.getOrDefault(IWComponents.TOOL_MATERIAL, ToolMaterialComponent.DEFAULT)
							.name(getTranslationKey());
				}
			}, "sword").setTranslation("剑").setModel(item -> new CustomItemModelDefinitionProvider(item,
			ItemModelDefinition.compositeTool(IWToolMaterials.buildDefinitionMap("sword/blade"),
					IWToolMaterials.buildDefinitionMap("sword/handle")))).addTag(IWItemTags.SWORD).build();

	public static final Item MATERIAL_PACKET = new CommonItemBuilder(MaterialPacketItem::new,
			"material_packet").setTranslation("材料包").setModel(IWItems::definitionOfMaterialPacket)
			.addToItemGroup(IWItemGroups.TOOLS_GROUP).build();

	public static final Item SWORD_TEMPLATE = new CommonItemBuilder(SwordForgeTemplateItem::new,
			"sword_template").setCommonModel(ModelParents.GENERATED).setTranslation("剑锻造模板")
			.addToItemGroup(IWItemGroups.TOOLS_GROUP).build();
	public static final Item STICK = new EnergyToolItemBuilder(EnergyToolItem::new, "stick", 63).setBoostable(2)
			.setRepair(Items.STICK, 1).setBaseAttack(3, 4).setEnergy(5, 0.1f)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.STICK)).setTranslation("木棍")
			.setTableBoostPool(TableBoostPool.POOL_ROD).build();
	public static final Item BLAZE_ROD = new EnergyToolItemBuilder(EnergyToolItem::new, "blaze_rod", 127).setBoostable(
					5).setRepair(Items.BLAZE_ROD, 2).setBaseAttack(4, 4).setEnergy(10, 0.15f)
			.addDefaultBoost(IWBoosts.FIRE_ASPECT, 1)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.BLAZE_ROD)).setTranslation("烈焰棒")
			.setToolComponent(new ToolComponent(
					List.of(ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()),
							2.0F)), 1.0F, 1)).setTableBoostPool(TableBoostPool.POOL_ROD).build();
	public static final Item WOODEN_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "wooden_sword",
			63).setBoostable(3).setRepair(ItemTags.WOODEN_TOOL_MATERIALS, 2).setBaseAttack(4, 1.6).setEnergy(5, 0.1f)
			.setModel(i -> new CustomItemModelDefinitionProvider(i,
					ItemModelDefinition.of(RawItemModel.of(Items.WOODEN_SWORD)))).setTranslation("木剑")
			.setToolComponent(EnergySwordItem.createToolComponent(0.5f)).setTableBoostPool(TableBoostPool.POOL_SWORD)
			.build();
	public static final Item STONE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "stone_sword",
			127).setBoostable(5).setRepair(ItemTags.STONE_TOOL_MATERIALS, 2).setBaseAttack(5, 1.6).setEnergy(10, 0.15f)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.STONE_SWORD)).setTranslation("石剑")
			.setToolComponent(EnergySwordItem.createToolComponent(0.75f)).setTableBoostPool(TableBoostPool.POOL_SWORD)
			.build();
	public static final Item IRON_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "iron_sword",
			255).setBoostable(8).setRepair(Items.IRON_NUGGET, 18).setBaseAttack(6, 1.6).setEnergy(15, 0.2f)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.IRON_SWORD)).setTranslation("铁剑")
			.setToolComponent(EnergySwordItem.createToolComponent(1.0f)).setTableBoostPool(TableBoostPool.POOL_SWORD)
			.build();
	public static final Item GOLDEN_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "golden_sword",
			31).setBoostable(16).setRepair(Items.GOLD_NUGGET, 18).setBaseAttack(4, 2.0).setEnergy(10, 1.0f)
			.addDefaultBoost(IWBoosts.REPEAT_ATTACK, 1)
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.GOLDEN_SWORD)).setTranslation("金剑")
			.setToolComponent(EnergySwordItem.createToolComponent(2f)).setTableBoostPool(TableBoostPool.POOL_SWORD)
			.build();
	public static final Item DIAMOND_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "diamond_sword",
			1023).setBoostable(15).setRepair(ItemTags.DIAMOND_TOOL_MATERIALS, 2).setBaseAttack(7, 1.6)
			.setEnergy(20, 0.25f).setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.DIAMOND_SWORD))
			.setTranslation("钻石剑").setToolComponent(EnergySwordItem.createToolComponent(1.25f))
			.setTableBoostPool(TableBoostPool.POOL_SWORD).build();
	public static final Item NETHERITE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "netherite_sword",
			2047).setBoostable(20).setRepair(ItemTags.NETHERITE_TOOL_MATERIALS, 1).setBaseAttack(8, 1.6)
			.setEnergy(25, 0.3f).setFireResistence()
			.setModel(getModelForEnergyTool(ModelParents.HANDHELD, Items.NETHERITE_SWORD))
			.setToolComponent(EnergySwordItem.createToolComponent(1.5f)).setTableBoostPool(TableBoostPool.POOL_SWORD)
			.setTranslation("下界合金剑").build();

	public static final Item HEART = new CommonItemBuilder(Item::new, "heart").addToItemGroup(
			IWItemGroups.INGREDIENTS_GROUP).setCommonModel(ModelParents.GENERATED).setTranslation("心").build();

	private static CustomItemModelDefinitionProvider definitionOfTestItem(Item item)
	{
		Map<ToolMaterial, ItemModelDefinition> map0 = new Object2ObjectOpenHashMap<>();
		map0.put(IWToolMaterials.WOOD, ItemModelDefinition.of(RawItemModel.of(Items.WATER_BUCKET)));
		map0.put(IWToolMaterials.STONE, ItemModelDefinition.of(RawItemModel.of(Items.LAVA_BUCKET)));
		Map<ToolMaterial, ItemModelDefinition> map1 = new Object2ObjectOpenHashMap<>();
		map1.put(IWToolMaterials.WOOD, ItemModelDefinition.of(RawItemModel.of(Items.WOODEN_SWORD)));
		map1.put(IWToolMaterials.STONE, ItemModelDefinition.of(RawItemModel.of(Items.STONE_SWORD)));
		return new CustomItemModelDefinitionProvider(item, ItemModelDefinition.composite(
				ItemModelDefinition.toolMaterial(0, ItemModelDefinition.of(RawItemModel.of(Items.BUCKET)), map0),
				ItemModelDefinition.toolMaterial(1, ItemModelDefinition.of(RawItemModel.of(Items.STICK)), map1)));
	}

	private static CustomItemModelDefinitionProvider definitionOfMaterialPacket(Item item)
	{
		Map<ToolMaterial, ItemModelDefinition> map0 = new Object2ObjectOpenHashMap<>();
		Map<ToolMaterial, ItemModelDefinition> map1 = new Object2ObjectOpenHashMap<>();
		IWToolMaterials.forEach(material -> {
			if (material.isOverlay()) map1.put(material, ItemModelDefinition.of(
					RawItemModel.simple(item, ModelParents.GENERATED)
							.setIdFormat("%s/overlay/" + material.identifier().getPath()).upload()));
			else map0.put(material, ItemModelDefinition.of(RawItemModel.simple(item, ModelParents.GENERATED)
					.setIdFormat("%s/" + material.identifier().getPath()).upload()));
		});
		return new CustomItemModelDefinitionProvider(item, ItemModelDefinition.composite(
				ItemModelDefinition.materialPacket(false,
						ItemModelDefinition.of(RawItemModel.simple(item, ModelParents.GENERATED).upload()), map0),
				ItemModelDefinition.materialPacket(true, ItemModelDefinition.empty(), map1)));
	}

	public static final Item TEST_ITEM = new CommonItemBuilder(TestItem::new, "test_item").addToItemGroup(
			IWItemGroups.INGREDIENTS_GROUP).setModel(IWItems::definitionOfTestItem).setTranslation("测试物品").build();

	/**
	 * (it) -> new LocatedItemModelDefinitionProvider(it, Models.GENERATED).setTextureAsItemTexture(it, "baseheart")
	 * .addPredictorAndPush(1,
	 * new LocatedItemModelDefinitionProvider(ItemModelIdBuilder.of(it).addDirectory("enchanted").build(),
	 * Models.GENERATED).setTextureAsItemTexture(it, "enchantedheart"))
	 */
	public static final BaseHeart COPPER_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 1, 3, 10, Color.CopperColorRGB), "copper_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("铜心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BaseHeart IRON_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 2, 6, 14, Color.IronColorRGB), "iron_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("铁心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BaseHeart GOLD_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 3, 12, 22, Color.GoldColorRGB), "gold_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("金心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BaseHeart EMERALD_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 4, 4, 18, Color.EmeraldColorRGB), "emerald_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("绿宝石心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BaseHeart DIAMOND_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 4, 12, 10, Color.DiamondColorRGB), "diamond_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("钻石心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BaseHeart NETHERITE_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 5, 18, 15, Color.NetheriteColorRGB), "netherite_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("下界合金心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BaseHeart ENDERITE_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 6, 12, 8, Color.EnderiteColorRGB), "enderite_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("末影合金心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BaseHeart VOIDALLOY_HEART = (BaseHeart) new CommonItemBuilder(
			settings -> new BaseHeart(settings, 10, 32, 18, Color.VoidalloyColorRGB), "voidalloy_heart").addToItemGroup(
					IWItemGroups.INGREDIENTS_GROUP).setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.isBoosted(ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("enchantedheart/%s").upload()),
							ItemModelDefinition.of(
									RawItemModel.simple(item, ModelParents.HANDHELD).setIdFormat("baseheart/%s").upload()))))
			.setTranslation("虚空合金心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final TinkerHeart TINKER_HEART = (TinkerHeart) new AbilityHeartItemBuilder(TinkerHeart::new,
			"tinker_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setCommonModel(ModelParents.GENERATED)
			.setTranslation("工匠之心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final CherryHeart CHERRY_HEART = (CherryHeart) new AbilityHeartItemBuilder(CherryHeart::new,
			"cherry_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setCommonModel(ModelParents.GENERATED)
			.setTranslation("樱之心").addTag(IWItemTags.IS_BASE_HEART).build();
	public static final BloodHeart BLOOD_HEART = (BloodHeart) new AbilityHeartItemBuilder(BloodHeart::new,
			"blood_heart").addToItemGroup(IWItemGroups.INGREDIENTS_GROUP).setCommonModel(ModelParents.GENERATED)
			.setTranslation("血之心").addTag(IWItemTags.IS_BASE_HEART).build();

	static
	{
		iwlogger.info("load IWItems down");
	}

	private static Set<AbstractAbility> getUniqueModelAbilities()
	{
		Set<AbstractAbility> ret = new HashSet<>();
		IWAbilities.forEach((ability -> {
			if (ability.getToolRenderAbility() == ability) ret.add(ability);
		}));
		return ret;
	}

	private static Function<Item, ItemModelProvider> getModelForEnergyTool(ModelParents model, Item baseTexture)
	{
		return item -> {
			Map<AbstractAbility, ItemModelDefinition> definitionMap = new HashMap<>();
			for (var ab : getUniqueModelAbilities())
			{
				definitionMap.put(ab, ItemModelDefinition.of(RawItemModel.simple(Base.MOD_ID, ab.id(), model)
						.setIdFormat(Registries.ITEM.getId(item).getPath() + "/%s").upload()));
			}
			return new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.ability(ItemModelDefinition.of(RawItemModel.of(baseTexture)), definitionMap));
		};
	}

	private static Function<Item, ItemModelProvider> getModelForEnergyTool(ModelParents model)
	{
		return item -> {
			Map<AbstractAbility, ItemModelDefinition> definitionMap = new HashMap<>();
			for (var ab : getUniqueModelAbilities())
			{
				definitionMap.put(ab, ItemModelDefinition.of(RawItemModel.simple(Base.MOD_ID, ab.id(), model)
						.setIdFormat(Registries.ITEM.getId(item).getPath() + "/%s").upload()));
			}
			return new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.ability(ItemModelDefinition.of(RawItemModel.of(item)), definitionMap));
		};
	}

	private static RegistryEntryList.Named<Block> getEntryListOfBlockTag(TagKey<Block> blockTagKey)
	{
		return Registries.createEntryLookup(Registries.BLOCK).getOrThrow(blockTagKey);
	}


	public static void initialize()
	{
	}

	static
	{
		LoadTime.setLoaded(IWItems.class);
	}
}
