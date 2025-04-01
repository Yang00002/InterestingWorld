package org.yang.iw.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;

import static org.yang.iw.util.Base.MOD_ID;

@IndependentRegister
public class IWComponents
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
	}

	public static final ComponentType<Float> MAX_ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "me"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static final ComponentType<Float> CURRENT_ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "ce"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());
	public static ComponentType<Float> ENERGY_REGEN_RATE = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "err"), ComponentType.<Float>builder().codec(Codec.FLOAT).build());

	public static ComponentType<AbilityComponent> ABILITY = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "ability"), ComponentType.<AbilityComponent>builder().codec(AbilityComponent.CODEC)
					.packetCodec(AbilityComponent.PACKET_CODEC).build());
	public static ComponentType<UpgradeComponent> UPGRADE = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "upgrade"), ComponentType.<UpgradeComponent>builder().codec(UpgradeComponent.CODEC)
					.packetCodec(UpgradeComponent.PACKET_CODEC).build());

	public static ComponentType<BoostComponent> BOOST = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "boost"),
			ComponentType.<BoostComponent>builder().codec(BoostComponent.CODEC).packetCodec(BoostComponent.PACKET_CODEC)
					.build());
	public static ComponentType<BoostableComponent> BOOSTABLE = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "boostable"),
			ComponentType.<BoostableComponent>builder().codec(BoostableComponent.CODEC)
					.packetCodec(BoostableComponent.PACKET_CODEC).build());
	public static ComponentType<ToolMaterialComponent> TOOL_MATERIAL =
			Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "tool_material"),
			ComponentType.<ToolMaterialComponent>builder().codec(ToolMaterialComponent.CODEC)
					.packetCodec(ToolMaterialComponent.PACKET_CODEC).build());
	public static ComponentType<Short> REPAIR_COST = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "repair_cost"), ComponentType.<Short>builder().codec(Codec.SHORT).build());
	public static ComponentType<ToolFlagComponent> TOOL_FLAG = Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "tool_flag"),
			ComponentType.<ToolFlagComponent>builder().codec(ToolFlagComponent.CODEC)
					.packetCodec(ToolFlagComponent.PACKET_CODEC).build());
	public static ComponentType<RepairPacketComponent> REPAIR_PACKET =
			Registry.register(Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "repair_packet"),
			ComponentType.<RepairPacketComponent>builder().codec(RepairPacketComponent.CODEC)
					.packetCodec(RepairPacketComponent.PACKET_CODEC).build());
	public static ComponentType<MaterialPacketComponent> MATERIAL_PACKET = Registry.register(
			Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "material_packet"),
			ComponentType.<MaterialPacketComponent>builder().codec(MaterialPacketComponent.CODEC)
					.packetCodec(MaterialPacketComponent.PACKET_CODEC).build());

	static
	{
		LoadTime.setLoaded(IWComponents.class);
	}

	public static void initialize()
	{
	}
}
