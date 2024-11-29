package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

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
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(IWModelGenerator::new);
    }
}