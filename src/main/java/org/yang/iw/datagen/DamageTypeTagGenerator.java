package org.yang.iw.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import org.yang.iw.datagen.tag.DamageTypeTagPool;

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
		DamageTypeTagPool.generatePool(this::getOrCreateTagBuilder);
		getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(DamageTypes.ON_FIRE);
	}
}
