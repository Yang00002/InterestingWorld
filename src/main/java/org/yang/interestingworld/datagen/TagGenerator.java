package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWDamageTypes;

import java.util.concurrent.CompletableFuture;

public class TagGenerator extends FabricTagProvider<DamageType>
{
    public static final TagKey<DamageType> BYPASS_COOLDOWN = TagKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of("minecraft:bypasses_cooldown"));
    public static final TagKey<DamageType> AVOID_GUARDIAN_THORNS = TagKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of("minecraft:avoids_guardian_thorns"));
    public static final TagKey<DamageType> NO_KNOCKBACK = TagKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of("minecraft:no_knockback"));
    public static final TagKey<DamageType> PANIC_CAUSES = TagKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of("minecraft:panic_causes"));
    public static final TagKey<DamageType> BYPASS_ARMOR = TagKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of("minecraft:bypasses_armor"));

    public static final TagKey<DamageType> BYPASS_WOLF_ARMOR = TagKey.of(RegistryKeys.DAMAGE_TYPE,
            Identifier.of("minecraft:bypasses_wolf_armor"));

    TagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
    {
        getOrCreateTagBuilder(BYPASS_COOLDOWN).addOptional(IWDamageTypes.ENERGEE_MELEE);
        getOrCreateTagBuilder(AVOID_GUARDIAN_THORNS).addOptional(IWDamageTypes.ENERGEE_MELEE);
        getOrCreateTagBuilder(PANIC_CAUSES).addOptional(IWDamageTypes.ENERGEE_MELEE);
        getOrCreateTagBuilder(BYPASS_COOLDOWN).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(AVOID_GUARDIAN_THORNS).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(NO_KNOCKBACK).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(PANIC_CAUSES).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(BYPASS_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
        getOrCreateTagBuilder(BYPASS_WOLF_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
    }
}
