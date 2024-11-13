package org.yang.interestingworld;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class IWComponents
{
	public static final ComponentType<Float> MAX_ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "me"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static final ComponentType<Float> CURRENT_ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "ce"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static ComponentType<Float> BASE_ENERGY_REGENERATION_COUNT = Registry.register(
			Registries.DATA_COMPONENT_TYPE, Identifier.of(IWUtil.Base.MOD_ID, "berc"),
			ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static ComponentType<Integer> BASE_ENERGY_REGENERATION_NEED = Registry.register(
			Registries.DATA_COMPONENT_TYPE, Identifier.of(IWUtil.Base.MOD_ID, "bern"),
			ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<Boolean> IS_COOLDOWN = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "ic"), ComponentType.<Boolean>builder().codec(Codec.BOOL).build());
	public static ComponentType<Double> DOUBLE_REG_0 = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "dr0"), ComponentType.<Double>builder().codec(Codec.DOUBLE).build());
	public static ComponentType<Integer> ITEM_COLOR = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "ico"), ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<Short> ABILITY_INDEX = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "ai"), ComponentType.<Short>builder().codec(Codec.SHORT).build());
	public static ComponentType<Integer> ABILITY_COLOR_RGB = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "acd"), ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<Boolean> CHARGE_OVER = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "co"), ComponentType.<Boolean>builder().codec(Codec.BOOL).build());
	public static ComponentType<Boolean> HAD_SWEEPING = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "hs"), ComponentType.<Boolean>builder().codec(Codec.BOOL).build());
	public static ComponentType<Short> LEFT_USE_TIME = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "lut"), ComponentType.<Short>builder().codec(Codec.SHORT).build());
	public static ComponentType<ItemEnchantmentsComponent> DEFAULT_ENCHANTMENTS = Registry.register(
			Registries.DATA_COMPONENT_TYPE, Identifier.of(IWUtil.Base.MOD_ID, "de"),
			ComponentType.<ItemEnchantmentsComponent>builder().codec(ItemEnchantmentsComponent.CODEC).build());

	public static void initialize()
	{
	}
}
