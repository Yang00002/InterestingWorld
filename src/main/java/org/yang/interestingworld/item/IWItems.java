package org.yang.interestingworld.item;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.enchant.IWEnchantments;
import org.yang.interestingworld.item.item_builder.CommonItemBuilder;
import org.yang.interestingworld.item.item_builder.EnergyToolItemBuilder;
import org.yang.interestingworld.item.rune.AbilityRuneItem;
import org.yang.interestingworld.item.rune.EnchantmentRuneItem;
import org.yang.interestingworld.item.rune.RuneItem;
import org.yang.interestingworld.item.rune.UpgradeRuneItem;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.item.tool.sword.EnergySwordItem;

public class IWItems
{
	public static final Item STICK = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_stick",
			ToolMaterials.WOOD).setBaseAttackDamage(3).setEnergy(5).setRegen(0.1f).build();
	public static final Item BLAZEROD = new EnergyToolItemBuilder(EnergyToolItem::new, "energy_blazerod",
			ToolMaterials.STONE).setBaseAttackDamage(4).setEnergy(10).setRegen(0.15f)
			.addDefaultEnchant(IWEnchantments.FIRE_ASPECT_entry, 1).build();
	public static final Item STONE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "stone_sword",
			ToolMaterials.STONE).setBaseAttackDamage(5).setBaseAttackSpeed(1.6).setEnergy(10).setRegen(0.15f).build();
	public static final Item IRON_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "iron_sword",
			ToolMaterials.IRON).setBaseAttackDamage(6).setBaseAttackSpeed(1.6).setEnergy(15).setRegen(0.2f).build();
	public static final Item GOLDEN_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "golden_sword",
			ToolMaterials.GOLD).setBaseAttackDamage(4).setBaseAttackSpeed(2.0).setEnergy(10).setRegen(1.0f)
			.addDefaultEnchant(IWEnchantments.FAST_HIT_entry, 1).build();
	public static final Item DIAMOND_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "diamond_sword",
			ToolMaterials.DIAMOND).setBaseAttackDamage(7).setBaseAttackSpeed(1.6).setEnergy(20).setRegen(0.25f).build();
	public static final Item NETHERITE_SWORD = new EnergyToolItemBuilder(EnergySwordItem::new, "netherite_sword",
			ToolMaterials.NETHERITE).setBaseAttackDamage(8).setBaseAttackSpeed(1.6).setEnergy(25).setRegen(0.3f)
			.setFireResistence().build();
	public static final Item EMPTY_RUNE = new CommonItemBuilder(RuneItem::new, "rune").addToItemGroup(
			IWItemGroups.RUNES_GROUP).build();
	public static final Item ABILITY_RUNE = new CommonItemBuilder(AbilityRuneItem::new, "ability_rune").build();
	public static final Item UPGRADE_RUNE = new CommonItemBuilder(UpgradeRuneItem::new, "upgrade_rune").build();
	public static final Item ENCHANTMENT_RUNE = new CommonItemBuilder(EnchantmentRuneItem::new,
			"enchantment_rune").build();

	public static final Item HEART = new CommonItemBuilder(Item::new, "heart").addToItemGroup(IWItemGroups.RUNES_GROUP)
			.build();

	public static void initialize()
	{
	}

}
