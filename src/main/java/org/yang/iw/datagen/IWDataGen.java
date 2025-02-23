package org.yang.iw.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.yang.iw.IWEnchantments;
import org.yang.iw.block.forgingblock.ForgingBlockState;
import org.yang.iw.datagen.blockmodel.BlockModelPool;
import org.yang.iw.datagen.itemmodel.ItemModelPool;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.datagen.tag.DamageTypeTagPool;
import org.yang.iw.datagen.tag.ItemTagPool;
import org.yang.iw.item.IWItemTags;

public class IWDataGen implements DataGeneratorEntrypoint
{
	public static FabricDataGenerator generator;

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
	{
		generator = fabricDataGenerator;
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(LanguageGenerator::new);
		pack.addProvider(DamageTypeTagGenerator::new);
		pack.addProvider(EnchantmentTagGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		initialize();
	}

	public void initialize()
	{
		IWEnchantments.dataGenInitialize();
		IWItemTags.dataGenInitialize();
		ForgingBlockState.dataGenInitialize();
	}

	public static void clearPools()
	{
		ItemModelPool.clearPool();
		BlockModelPool.clearPool();
		TranslationPool.clearPool();
		ItemTagPool.clearPool();
		DamageTypeTagPool.clearPool();
	}
}