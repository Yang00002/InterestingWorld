package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.yang.interestingworld.datagen.itemmodel.ItemModelPool;

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
	}

	public static void clearPools()
	{
		ItemModelPool.clearPool();
	}
}