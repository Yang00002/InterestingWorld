package org.yang.interestingworld;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.EnergyToolDataFlag;

import static org.yang.interestingworld.util.Base.MOD_ID;

public class IWComponents
{
	public static final ComponentType<Float> MAX_ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "me"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static final ComponentType<Float> CURRENT_ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "ce"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static ComponentType<Float> ENERGY_REGEN_RATE = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "err"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static ComponentType<Integer> ITEM_COLOR = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "ico"), ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<Short> ABILITY_INDEX = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "ai"), ComponentType.<Short>builder().codec(Codec.SHORT).build());
	public static ComponentType<Integer> ABILITY_COLOR_RGB = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "acd"), ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<ItemEnchantmentsComponent> DEFAULT_ENCHANTMENTS = Registry.register(
			Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "de"),
			ComponentType.<ItemEnchantmentsComponent>builder().codec(ItemEnchantmentsComponent.CODEC).build());
	public static ComponentType<EnergyToolDataFlag> DATA_FLAGS = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "df"),
			ComponentType.<EnergyToolDataFlag>builder().codec(EnergyToolDataFlag.CODEC).build());
	public static ComponentType<Integer> ENCHANT_VALUE = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "ev"), ComponentType.<Integer>builder().codec(Codec.INT).build());
	public static ComponentType<Text> UPGRADE_TEXT = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "ut"), ComponentType.<Text>builder().codec(TextCodecs.STRINGIFIED_CODEC)
					.packetCodec(TextCodecs.REGISTRY_PACKET_CODEC).cache().build());

	public static void initialize()
	{
	}
}
