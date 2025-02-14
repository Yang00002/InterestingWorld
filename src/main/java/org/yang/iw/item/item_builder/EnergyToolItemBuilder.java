package org.yang.iw.item.item_builder;

import com.mojang.datafixers.util.Either;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWItemGroups;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.itemmodel.ItemModelPool;
import org.yang.iw.datagen.itemmodel.ItemModelProvider;
import org.yang.iw.datagen.itemmodel.SimpleItemModelProvider;
import org.yang.iw.datagen.itemmodel.server.ModelParents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.datagen.tag.ItemTagPool;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.util.Base;
import org.yang.iw.util.Server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.BASE_ATTACK_SPEED_MODIFIER_ID;
import static org.yang.iw.IWItemGroups.TOOLS_GROUP;

public class EnergyToolItemBuilder
{
	public interface EnergyToolItemConstructor
	{
		EnergyToolItem apply(Item.Settings settings,
							 Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments);
	}

	private final EnergyToolItemConstructor constructor;
	private final String id;
	private float maxEnergy = 0;
	private float regenRate = 0;
	private double attackDamageAdding = 0;
	private final int durability;
	private ToolComponent toolComponent = null;
	private double attackSpeedAdding = 0;
	private double entityInteractionRangeAdding = 0;
	private boolean fireresistence = false;
	private RegistryKey<ItemGroup> itemGroupBelong = TOOLS_GROUP;
	private Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments = null;

	private static final Identifier BASE_ENTITY_INTERACTION_RANGE = Identifier.of(Base.MOD_ID,
			"base_entity_interaction_range");

	private Function<Item, ItemModelProvider> modelProvider = null;
	private String translation = null;
	private Either<TagKey<Item>, Item> repairableComponent = null;
	private final Set<TagKey<Item>> tags = new HashSet<>();

	public EnergyToolItemBuilder addTag(TagKey<Item> tag)
	{
		tags.add(tag);
		return this;
	}

	public EnergyToolItemBuilder setToolComponent(ToolComponent component)
	{
		toolComponent = component;
		return this;
	}

	public EnergyToolItemBuilder(EnergyToolItemConstructor constructor, String id, int durability)
	{
		this.constructor = constructor;
		this.id = id;
		this.durability = durability;
	}

	public EnergyToolItemBuilder setEnergy(float max)
	{
		maxEnergy = max;
		return this;
	}

	public EnergyToolItemBuilder setRepairIngredient(TagKey<Item> tagKey)
	{
		repairableComponent = Either.left(tagKey);
		return this;
	}

	public EnergyToolItemBuilder setRepairIngredient(Item item)
	{
		repairableComponent = Either.right(item);
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

	public EnergyToolItemBuilder setModel(Function<Item, ItemModelProvider> provider)
	{
		modelProvider = provider;
		return this;
	}

	public EnergyToolItemBuilder setCommonModel(ModelParents model)
	{
		modelProvider = item -> new SimpleItemModelProvider(item, model);
		return this;
	}

	public EnergyToolItemBuilder setTranslation(String str)
	{
		translation = str;
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
			settings.component(IWComponents.MAX_ENERGY, maxEnergy).component(IWComponents.CURRENT_ENERGY, maxEnergy);
		if (regenRate > 0) settings.component(IWComponents.ENERGY_REGEN_RATE, regenRate);
		if (fireresistence) settings.fireproof();
		if (durability > 0) settings.maxDamage(durability);
		if (toolComponent != null) settings.component(DataComponentTypes.TOOL, toolComponent);
		if (repairableComponent != null) repairableComponent.map(settings::repairable, settings::repairable);
		var builder = AttributeModifiersComponent.builder();
		builder.add(EntityAttributes.ATTACK_DAMAGE,
				new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, attackDamageAdding,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		builder.add(EntityAttributes.ATTACK_SPEED,
				new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeedAdding,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		if (entityInteractionRangeAdding != 0) builder.add(EntityAttributes.ENTITY_INTERACTION_RANGE,
				new EntityAttributeModifier(BASE_ENTITY_INTERACTION_RANGE, entityInteractionRangeAdding,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		settings.attributeModifiers(builder.build());
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, itemID);
		EnergyToolItem ret = (EnergyToolItem) Items.register(registryKey,
				i -> constructor.apply(i, defaultEnchantments), settings);
		if (itemGroupBelong != null)
		{
			IWItemGroups.addItemToGroup((context, entries) -> {
				var wrapperOp = context.lookup().getOptional(RegistryKeys.ENCHANTMENT);
				wrapperOp.ifPresent(wrapper -> entries.add(ret.getEnchantedDefaultItemStackFromClient(wrapper)));
			}, itemGroupBelong);
		}
		if (modelProvider != null) ItemModelPool.addModel(modelProvider.apply(ret));
		if (translation != null) TranslationPool.addItem(ret, translation);
		tags.forEach(tag -> ItemTagPool.add(tag, ret));
		return ret;
	}
}