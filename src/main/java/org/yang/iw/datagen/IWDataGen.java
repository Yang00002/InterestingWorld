package org.yang.iw.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.yang.iw.api.java.ClassLoader;
import org.yang.iw.datagen.blockmodel.BlockModelPool;
import org.yang.iw.datagen.itemmodel.ItemModelPool;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.datagen.tag.DamageTypeTagPool;
import org.yang.iw.datagen.tag.ItemTagPool;

import static org.yang.iw.util.Base.iwlogger;

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
		pack.addProvider(EntityTypeTagGenerator::new);
		pack.addProvider(ToolMaterialTagGenerator::new);
		pack.addProvider(BoostTagGenerator::new);
		initialize();
	}

	public void initialize()
	{
		ClassLoader.registerMixinPackage("org.yang.iw.client.mixin.mixin");
		ClassLoader.registerMixinPackage("org.yang.iw.mixin.mixin");
		if (!ClassLoader.loadPackage("org.yang.iw"))
		{
			iwlogger.info("Some classes load fail. DataGen may won't generate all the data");
		}
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