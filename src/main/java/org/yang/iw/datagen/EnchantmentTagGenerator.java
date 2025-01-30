package org.yang.iw.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import org.yang.iw.enchant.IWEnchantments;

import java.util.concurrent.CompletableFuture;

public class EnchantmentTagGenerator extends FabricTagProvider<Enchantment>
{
	public EnchantmentTagGenerator(FabricDataOutput output,
								   CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
	{
		super(output, RegistryKeys.ENCHANTMENT, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
	{
		getOrCreateTagBuilder(EnchantmentTags.IN_ENCHANTING_TABLE).addOptional(IWEnchantments.BALANCE)
				.addOptional(IWEnchantments.PLENTIFUL).addOptional(IWEnchantments.RUNE_BOOST)
				.addOptional(IWEnchantments.ENERGY_EFFICIENCY).addOptional(IWEnchantments.LUCKY);
		getOrCreateTagBuilder(EnchantmentTags.TRADEABLE).addOptional(IWEnchantments.BALANCE)
				.addOptional(IWEnchantments.PLENTIFUL).addOptional(IWEnchantments.RUNE_BOOST)
				.addOptional(IWEnchantments.ENERGY_EFFICIENCY).addOptional(IWEnchantments.LUCKY);
	}
}
