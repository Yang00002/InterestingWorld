package org.yang.interestingworld;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class IWComponents
{
	public static ComponentType<Integer> ITEM_COLOR = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "ico"), ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<Short> ABILITY_INDEX = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "ai"), ComponentType.<Short>builder().codec(Codec.SHORT).build());
	public static ComponentType<Integer> ABILITY_COLOR_RGB = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(IWUtil.Base.MOD_ID, "acd"), ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<ItemEnchantmentsComponent> DEFAULT_ENCHANTMENTS = Registry.register(
			Registries.DATA_COMPONENT_TYPE, Identifier.of(IWUtil.Base.MOD_ID, "de"),
			ComponentType.<ItemEnchantmentsComponent>builder().codec(ItemEnchantmentsComponent.CODEC).build());

	public static void initialize()
	{
	}
}
