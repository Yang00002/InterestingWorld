package org.yang.iw.datagen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.yang.iw.datagen.language.TranslationPool;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.yang.iw.datagen.IWDataGen.generator;

public class LanguageGenerator extends FabricLanguageProvider
{


	LanguageGenerator(FabricDataOutput dataGenerator,
					  CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture)
	{
		super(dataGenerator, "zh_cn", completableFuture);
	}

	@Override
	public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup,
									 TranslationBuilder translationBuilder)
	{
		TranslationPool.generatePool(translationBuilder);
		translationBuilder.add("enchantment.level.11", "XI");
		translationBuilder.add("enchantment.level.12", "XII");
		for (int i = 13; i < 25; i++)
			translationBuilder.add("enchantment.level.%s".formatted(i), String.valueOf(i));
		translationBuilder.add("attribute.modifier.iwp.0", "+%s %s");
		translationBuilder.add("attribute.modifier.iwp.1", "+%s%% %s");
		translationBuilder.add("attribute.modifier.iwp.2", "x%s %s");
		translationBuilder.add("attribute.modifier.iwt.0", "-%s %s");
		translationBuilder.add("attribute.modifier.iwt.1", "-%s%% %s");
		translationBuilder.add("attribute.modifier.iwt.2", "x%s %s");
		translationBuilder.add("sound.iw.abilitybar_full", "能量充满");
		translationBuilder.add("sound.iw.double_sweep", "斩击");
		try
		{
			Path existingFilePath = generator.getModContainer().findPath("assets/iw/lang/zh_cn.json").get();
			translationBuilder.add(existingFilePath);
		} catch (Exception ignored)
		{
		}
	}

}
