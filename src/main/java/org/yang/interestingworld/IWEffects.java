package org.yang.interestingworld;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.effect.BloodEffect;

public class IWEffects
{
    public static final RegistryEntry<StatusEffect> BLOOD = Registry.registerReference(Registries.STATUS_EFFECT,
            Identifier.of(IWUtil.Base.MOD_ID, "blood"), new BloodEffect());

    public static void initialize()
    {
    }
}