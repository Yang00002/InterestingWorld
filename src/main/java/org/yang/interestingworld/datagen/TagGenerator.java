package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWDamageTypes;

import java.util.concurrent.CompletableFuture;

public class TagGenerator extends FabricTagProvider<DamageType>
{
    TagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
    {
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(IWDamageTypes.ENERGEE_MELEE);
        getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).addOptional(IWDamageTypes.ENERGEE_MELEE);
        getOrCreateTagBuilder(DamageTypeTags.PANIC_CAUSES).addOptional(IWDamageTypes.ENERGEE_MELEE);
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(DamageTypeTags.PANIC_CAUSES).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
    }
}
