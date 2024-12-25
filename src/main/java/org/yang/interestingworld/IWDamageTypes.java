package org.yang.interestingworld;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static org.yang.interestingworld.util.Base.MOD_ID;

public class IWDamageTypes
{
    public static final RegistryKey<DamageType> ENERGEE_MELEE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of(MOD_ID, "energe_melee"));
    public static final RegistryKey<DamageType> ENERGEE_EXPLODE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of(MOD_ID, "energe_explode"));
    public static final RegistryKey<DamageType> BLOOD_EFFECT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of(MOD_ID, "blood_effect"));
    public static void initialize()
    {

    }


}
