package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWTags;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagGenerator extends FabricTagProvider<DamageType>
{
	DamageTypeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
	{
		super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
	}
	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
	{
		// 斩击伤害
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(IWDamageTypes.ENERGEE_MELEE);
		getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).addOptional(IWDamageTypes.ENERGEE_MELEE);
		getOrCreateTagBuilder(DamageTypeTags.PANIC_CAUSES).addOptional(IWDamageTypes.ENERGEE_MELEE);
		// 流血效果
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(IWDamageTypes.BLOOD_EFFECT);
		getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).addOptional(IWDamageTypes.BLOOD_EFFECT);
		getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK).addOptional(IWDamageTypes.BLOOD_EFFECT);
		getOrCreateTagBuilder(DamageTypeTags.PANIC_CAUSES).addOptional(IWDamageTypes.BLOOD_EFFECT);
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
	}
}
