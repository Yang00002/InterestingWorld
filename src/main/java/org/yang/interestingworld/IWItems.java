package org.yang.interestingworld;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.item.rune.AbilityRuneItem;
import org.yang.interestingworld.item.rune.EnchantmentRuneItem;
import org.yang.interestingworld.item.rune.RuneItem;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.item.tool.sword.EnergySword;
import org.yang.interestingworld.util.Base;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.BASE_ATTACK_SPEED_MODIFIER_ID;
import static org.yang.interestingworld.IWItemGroups.*;

public class IWItems
{
	public static Item STICK = null;
	public static Item BLAZEROD = null;
	public static Item STONE_SWORD = null;
	public static Item IRON_SWORD = null;
	public static Item GOLDEN_SWORD = null;
	public static Item DIAMOND_SWORD = null;
	public static Item NETHERITE_SWORD = null;
	public static Item EMPTY_RUNE = null;
	public static Item COMMON_ABILITY_RUNE = null;
	public static Item ENCHANTMENT_RUNE = null;

	public static void initialize()
	{
		STICK = EnergyToolItemBuilder.getInstance().setBaseAttackDamage(3).setEnergy(5).setRegen(0.1f)
				.build("energy_stick", ToolMaterials.WOOD, EnergyToolItem::new);
		BLAZEROD = EnergyToolItemBuilder.getInstance().setBaseAttackDamage(4).setEnergy(10).setRegen(0.15f)
				.build("energy_blazerod", ToolMaterials.STONE, EnergyToolItem::new);
		STONE_SWORD = EnergyToolItemBuilder.getInstance().setBaseAttackDamage(5).setBaseAttackSpeed(1.6).setEnergy(10)
				.setRegen(0.15f).build("stone_sword", ToolMaterials.STONE, EnergySword::new);
		IRON_SWORD = EnergyToolItemBuilder.getInstance().setBaseAttackDamage(6).setBaseAttackSpeed(1.6).setEnergy(15)
				.setRegen(0.2f).build("iron_sword", ToolMaterials.IRON, EnergySword::new);
		GOLDEN_SWORD = EnergyToolItemBuilder.getInstance().setBaseAttackDamage(4).setBaseAttackSpeed(2.0).setEnergy(10)
				.setRegen(1.0f).build("golden_sword", ToolMaterials.GOLD, EnergySword::new);
		DIAMOND_SWORD =
				EnergyToolItemBuilder.getInstance().setBaseAttackDamage(7).setBaseAttackSpeed(1.6).setEnergy(20)
				.setRegen(0.25f).build("diamond_sword", ToolMaterials.DIAMOND, EnergySword::new);
		NETHERITE_SWORD = EnergyToolItemBuilder.getInstance().setBaseAttackDamage(8).setBaseAttackSpeed(1.6)
				.setEnergy(25).setRegen(0.3f).setFireResistence()
				.build("netherite_sword", ToolMaterials.NETHERITE, EnergySword::new);
		EMPTY_RUNE = createCommonItem("rune", RuneItem::new);
		COMMON_ABILITY_RUNE = createCommonItem("ability_rune", AbilityRuneItem::new);
		ENCHANTMENT_RUNE = createCommonItem("enchantment_rune", EnchantmentRuneItem::new);
	}

	public static void addItemToItemGroupWhenEnterWorld()
	{
		addItemToGroup(EnergyToolInitializer.getInstance(STICK), TOOLS_GROUP);
		addItemToGroup(EnergyToolInitializer.getInstance(BLAZEROD).addDefaultEnchantment(Enchantments.FIRE_ASPECT, 1),
				TOOLS_GROUP);
		addItemToGroup(EnergyToolInitializer.getInstance(STONE_SWORD), TOOLS_GROUP);
		addItemToGroup(EnergyToolInitializer.getInstance(IRON_SWORD), TOOLS_GROUP);
		addItemToGroup(
				EnergyToolInitializer.getInstance(GOLDEN_SWORD).addDefaultEnchantment(IWEnchantments.FAST_HIT, 1),
				TOOLS_GROUP);
		addItemToGroup(EnergyToolInitializer.getInstance(DIAMOND_SWORD), TOOLS_GROUP);
		addItemToGroup(EnergyToolInitializer.getInstance(NETHERITE_SWORD), TOOLS_GROUP);
		addItemToGroup(CommonItemInitializer.getInstance(EMPTY_RUNE), RUNES_GROUP);
	}

	public static Item createCommonItem(String id, Function<Item.Settings, Item> itemGetter)
	{
		Item item = itemGetter.apply(new Item.Settings());
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		Registry.register(Registries.ITEM, itemID, item);
		return item;
	}


	public static class EnergyToolItemBuilder
	{
		private float maxEnergy = 0;
		private float regenRate = 0;
		private double attackDamageAdding = 0;
		private double attackSpeedAdding = 0;
		private double entityInteractionRangeAdding = 0;
		public boolean fireresistence = false;

		private final Identifier BASE_ENTITY_INTERACTION_RANGE = Identifier.of(Base.MOD_ID,
				"base_entity_interaction_range");

		public EnergyToolItemBuilder setEnergy(float max)
		{
			maxEnergy = max;
			return this;
		}

		public EnergyToolItemBuilder setRegen(float regenRateParameter)
		{
			regenRate = regenRateParameter;
			return this;
		}

		public EnergyToolItemBuilder setFireResistence()
		{
			fireresistence = true;
			return this;
		}

		public EnergyToolItemBuilder setBaseAttackDamage(double attackDamage)
		{
			attackDamageAdding = attackDamage - 1;
			return this;
		}

		public EnergyToolItemBuilder setBaseAttackSpeed(double attackSpeed)
		{
			attackSpeedAdding = attackSpeed - 4.0;
			return this;
		}

		public EnergyToolItemBuilder setEntityInteractionRangeAdding(double interactionrangeadding)
		{
			entityInteractionRangeAdding = interactionrangeadding;
			return this;
		}

		public static EnergyToolItemBuilder getInstance()
		{
			return new EnergyToolItemBuilder();
		}

		public <T extends EnergyToolItem> T build(String id, ToolMaterial material, BiFunction<ToolMaterial,
				Item.Settings, T> function)
		{
			Item.Settings settings = new Item.Settings();
			if (maxEnergy > 0)
			{
				settings = settings.component(IWComponents.MAX_ENERGY, maxEnergy)
						.component(IWComponents.CURRENT_ENERGY, maxEnergy);
			}
			if (regenRate > 0)
			{
				settings = settings.component(IWComponents.ENERGY_REGEN_RATE, regenRate);
			}
			if (fireresistence) settings = settings.fireproof();
			var builder = AttributeModifiersComponent.builder();
			builder = builder.add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
					new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, attackDamageAdding,
							EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
			builder = builder.add(EntityAttributes.GENERIC_ATTACK_SPEED,
					new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeedAdding,
							EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
			if (entityInteractionRangeAdding != 0)
				builder = builder.add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
						new EntityAttributeModifier(BASE_ENTITY_INTERACTION_RANGE, entityInteractionRangeAdding,
								EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
			settings = settings.attributeModifiers(builder.build());
			T ret = function.apply(material, settings);
			Identifier itemID = Identifier.of(Base.MOD_ID, id);
			Registry.register(Registries.ITEM, itemID, ret);
			return ret;
		}
	}

}
