package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import org.yang.interestingworld.IWDamageTypes;

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
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(IWDamageTypes.ENERGEE_MELEE)
				.addOptional(IWDamageTypes.BLOOD_EFFECT).addOptional(IWDamageTypes.ENERGEE_EXPLODE)
				.addOptional(DamageTypes.ON_FIRE);
		getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).addOptional(IWDamageTypes.ENERGEE_MELEE)
				.addOptional(IWDamageTypes.BLOOD_EFFECT).addOptional(IWDamageTypes.ENERGEE_EXPLODE);
		getOrCreateTagBuilder(DamageTypeTags.PANIC_CAUSES).addOptional(IWDamageTypes.ENERGEE_MELEE)
				.addOptional(IWDamageTypes.BLOOD_EFFECT).addOptional(IWDamageTypes.ENERGEE_EXPLODE);
		getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK).addOptional(IWDamageTypes.BLOOD_EFFECT)
				.addOptional(IWDamageTypes.ENERGEE_EXPLODE);
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR).addOptional(IWDamageTypes.BLOOD_EFFECT);
		getOrCreateTagBuilder(DamageTypeTags.IS_EXPLOSION).addOptional(IWDamageTypes.ENERGEE_EXPLODE);
		getOrCreateTagBuilder(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).addOptional(IWDamageTypes.ENERGEE_MELEE)
				.addOptional(IWDamageTypes.ENERGEE_EXPLODE);
	}
}
