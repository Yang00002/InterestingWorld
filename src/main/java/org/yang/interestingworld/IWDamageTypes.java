package org.yang.interestingworld;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Server;

import static org.yang.interestingworld.util.Base.MOD_ID;

public class IWDamageTypes
{
	public static final RegistryKey<DamageType> ENERGEE_MELEE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
			Identifier.of(MOD_ID, "energe_melee"));
	public static final RegistryKey<DamageType> ENERGEE_EXPLODE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
			Identifier.of(MOD_ID, "energe_explode"));
	public static final RegistryKey<DamageType> BLOOD_EFFECT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
			Identifier.of(MOD_ID, "blood_effect"));

	public static final Server.LoadOnceRegistryEntry<DamageType> ENERGEE_MELEE_entry = damageTypeLoadOnceRegistryEntry(
			ENERGEE_MELEE);
	public static final Server.LoadOnceRegistryEntry<DamageType> ENERGEE_EXPLODE_entry =
			damageTypeLoadOnceRegistryEntry(
			ENERGEE_EXPLODE);
	public static final Server.LoadOnceRegistryEntry<DamageType> BLOOD_EFFECT_entry = damageTypeLoadOnceRegistryEntry(
			BLOOD_EFFECT);

	private static Server.LoadOnceRegistryEntry<DamageType> damageTypeLoadOnceRegistryEntry(RegistryKey<DamageType> damageTypeRegistryKey)
	{
		return Server.getLoadOnceRegistryEntry(damageTypeRegistryKey, RegistryKeys.DAMAGE_TYPE);
	}

	public static void initialize()
	{

	}


}
