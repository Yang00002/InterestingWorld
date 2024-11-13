package org.yang.interestingworld;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.item.rune.AbilityRuneItem;
import org.yang.interestingworld.item.rune.RuneItem;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.item.tool.sword.EnergySword;
import org.yang.interestingworld.rune.IWRuneAbilitys;

import java.util.function.Function;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.BASE_ATTACK_SPEED_MODIFIER_ID;
import static org.yang.interestingworld.IWItemGroups.*;

public class IWItems
{
	public static Item STICK = null;
	public static Item BLOOD_SWORD = null;
	public static Item STONE_SWORD = null;
	public static Item IRON_SWORD = null;
	public static Item GOLDEN_SWORD = null;
	public static Item DIAMOND_SWORD = null;
	public static Item NETHERITE_SWORD = null;
	public static Item EMPTY_RUNE = null;
	public static Item COMMON_ABILITY_RUNE = null;

	public static void initialize()
	{
		STICK = EnergyToolItemBuilder.getInstance().setEnergy(5, 0).setRegen(1, 50).setBaseAttackDamage(3)
				.build("energy_stick", EnergyToolItem::new);
		BLOOD_SWORD = EnergyToolItemBuilder.getInstance().setEnergy(10, 0).setRegen(1, 40).setBaseAttackDamage(4.5)
				.setBaseAttackSpeed(1.8).setEntityInteractionRangeAdding(-0.25).build("blood_sword", EnergySword::new);
		STONE_SWORD = EnergyToolItemBuilder.getInstance().setEnergy(10, 0).setRegen(1, 40).setBaseAttackDamage(5)
				.setBaseAttackSpeed(1.6).build("stone_sword", EnergySword::new);
		IRON_SWORD = EnergyToolItemBuilder.getInstance().setEnergy(20, 0).setRegen(1, 30).setBaseAttackDamage(6)
				.setBaseAttackSpeed(1.6).build("iron_sword", EnergySword::new);
		GOLDEN_SWORD = EnergyToolItemBuilder.getInstance().setEnergy(5, 0).setRegen(1, 5).setBaseAttackDamage(4)
				.setBaseAttackSpeed(2.0).build("golden_sword", EnergySword::new);
		DIAMOND_SWORD = EnergyToolItemBuilder.getInstance().setEnergy(30, 0).setRegen(1, 25).setBaseAttackDamage(7)
				.setBaseAttackSpeed(1.6).build("diamond_sword", EnergySword::new);
		NETHERITE_SWORD = EnergyToolItemBuilder.getInstance().setEnergy(40, 0).setRegen(1, 20).setBaseAttackDamage(8)
				.setBaseAttackSpeed(1.6).setFireResistence().build("netherite_sword", EnergySword::new);
		EMPTY_RUNE = createCommonItem("rune", RuneItem::new);
		COMMON_ABILITY_RUNE = createCommonItem("ability_rune", AbilityRuneItem::new);


		addItemToGroup(EnergyToolInitializer.getInstance(STICK).addEnchantment(Enchantments.SHARPNESS, 1)
				.setAbility(IWRuneAbilitys.SWEEP_ABILITY), TOOLS_GROUP);
		addItemToGroup(EnergyToolInitializer.getInstance(BLOOD_SWORD).addEnchantment(Enchantments.UNBREAKING, 5)
				.setAbility(IWRuneAbilitys.SLASHING_ABILITY), TOOLS_GROUP);
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
		Identifier itemID = Identifier.of(IWUtil.Base.MOD_ID, id);
		Registry.register(Registries.ITEM, itemID, item);
		return item;
	}


	public static class EnergyToolItemBuilder
	{
		private float maxEnergy = 0;
		private float currentEnergy = 0;
		private float regenCount = 0;
		private int regenNeed = 0;
		private double attackDamageAdding = 0;
		private double attackSpeedAdding = 0;
		private double entityInteractionRangeAdding = 0;
		public boolean fireresistence = false;

		private final Identifier BASE_ENTITY_INTERACTION_RANGE = Identifier.of(IWUtil.Base.MOD_ID,
				"base_entity_interaction_range");

		public EnergyToolItemBuilder setFireResistence()
		{
			fireresistence = true;
			return this;
		}

		public EnergyToolItemBuilder setEnergy(float max, float current)
		{
			maxEnergy = max;
			currentEnergy = current;
			return this;
		}

		public EnergyToolItemBuilder setRegen(float count, int need)
		{
			regenCount = count;
			regenNeed = need;
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

		public <T extends EnergyToolItem> T build(String id, Function<Item.Settings, T> function)
		{
			Item.Settings settings = new Item.Settings();
			if (maxEnergy > 0)
			{
				if (currentEnergy <= maxEnergy && currentEnergy >= 0)
					settings = settings.component(IWComponents.MAX_ENERGY, maxEnergy)
							.component(IWComponents.CURRENT_ENERGY, currentEnergy);
				else settings = settings.component(IWComponents.MAX_ENERGY, maxEnergy)
						.component(IWComponents.CURRENT_ENERGY, 0f);
			}
			if (regenCount > 0 && regenNeed > 0)
			{
				settings = settings.component(IWComponents.BASE_ENERGY_REGENERATION_COUNT, regenCount)
						.component(IWComponents.BASE_ENERGY_REGENERATION_NEED, regenNeed);
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
			T ret = function.apply(settings);
			Identifier itemID = Identifier.of(IWUtil.Base.MOD_ID, id);
			Registry.register(Registries.ITEM, itemID, ret);
			return ret;
		}
	}

}
