package org.yang.interestingworld.item.item_builder;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.util.Base;
import org.yang.interestingworld.util.Server;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.BASE_ATTACK_SPEED_MODIFIER_ID;
import static org.yang.interestingworld.IWItemGroups.TOOLS_GROUP;

public class EnergyToolItemBuilder
{
	public interface EnergyToolItemConstructor
	{
		EnergyToolItem apply(ToolMaterial material, Item.Settings settings,
							 Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments);
	}

	private final EnergyToolItemConstructor constructor;
	private final String id;
	private final ToolMaterial material;
	private float maxEnergy = 0;
	private float regenRate = 0;
	private double attackDamageAdding = 0;
	private double attackSpeedAdding = 0;
	private double entityInteractionRangeAdding = 0;
	private boolean fireresistence = false;
	private RegistryKey<ItemGroup> itemGroupBelong = TOOLS_GROUP;
	private Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments = null;

	private final Identifier BASE_ENTITY_INTERACTION_RANGE = Identifier.of(Base.MOD_ID,
			"base_entity_interaction_range");

	public EnergyToolItemBuilder(EnergyToolItemConstructor constructor, String id, ToolMaterial material)
	{
		this.constructor = constructor;
		this.id = id;
		this.material = material;
	}

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

	public EnergyToolItemBuilder addDefaultEnchant(Server.LoadOnceRegistryEntry<Enchantment> enchantEntry, int level)
	{
		if (defaultEnchantments == null) defaultEnchantments = new HashMap<>();
		defaultEnchantments.put(enchantEntry, level);
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

	public EnergyToolItemBuilder addToItemGroup(RegistryKey<ItemGroup> itemGroup)
	{
		itemGroupBelong = itemGroup;
		return this;
	}

	public EnergyToolItem build()
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
		if (entityInteractionRangeAdding != 0) builder = builder.add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
				new EntityAttributeModifier(BASE_ENTITY_INTERACTION_RANGE, entityInteractionRangeAdding,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		settings = settings.attributeModifiers(builder.build());
		EnergyToolItem ret = constructor.apply(material, settings, defaultEnchantments);
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		Registry.register(Registries.ITEM, itemID, ret);
		if (itemGroupBelong != null)
		{
			IWItemGroups.addItemToGroup((context, entries) -> {
				var wrapperOp = context.lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT);
				wrapperOp.ifPresent(wrapper -> entries.add(ret.getEnchantedDefaultItemStackFromClient(wrapper)));
			}, itemGroupBelong);
		}
		return ret;
	}
}