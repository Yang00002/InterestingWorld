package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.yang.interestingworld.IWBlocks;
import org.yang.interestingworld.IWEffects;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.IWItems;
import org.yang.interestingworld.rune.ability.InfiniteSlashingAbility;
import org.yang.interestingworld.rune.ability.SlashingAbility;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.yang.interestingworld.datagen.IWDataGen.generator;

public class LanguageGenerator extends FabricLanguageProvider
{
	String toString(float f)
	{
		String s = String.format("%.1f", f);
		if (s.endsWith(".0")) s = String.format("%.0f", f);
		return s;
	}

	String toString(double f)
	{
		String s = String.format("%.1f", f);
		if (s.endsWith(".0")) s = String.format("%.0f", f);
		return s;
	}

	LanguageGenerator(FabricDataOutput dataGenerator,
					  CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture)
	{
		super(dataGenerator, "zh_cn", completableFuture);
	}

	@Override
	public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup,
									 TranslationBuilder translationBuilder)
	{
		translationBuilder.add(IWItems.BLOOD_SWORD, "血刃");
		translationBuilder.add(IWItems.STICK, "棍子");
		translationBuilder.add(IWItems.STONE_SWORD, "石剑");
		translationBuilder.add(IWItems.IRON_SWORD, "铁剑");
		translationBuilder.add(IWItems.GOLDEN_SWORD, "金剑");
		translationBuilder.add(IWItems.DIAMOND_SWORD, "钻石剑");
		translationBuilder.add(IWItems.NETHERITE_SWORD, "下界合金剑");
		translationBuilder.add(IWItems.EMPTY_RUNE, "空白符文");
		translationBuilder.add("abilityrune.suffix", "符文");
		translationBuilder.add(IWItemGroups.TOOLS_GROUP, "IW: 工具");
		translationBuilder.add(IWItemGroups.RUNES_GROUP, "IW：符文");
		translationBuilder.add(IWItemGroups.BLOCKS_GROUP, "IW：方块");
		translationBuilder.add(IWEffects.BLOOD.value(), "流血");
		translationBuilder.add(IWEffects.COOLDOWN.value(), "受击");
		translationBuilder.add("enchantment.interestingworld.fast_hit", "迅捷打击");
		translationBuilder.add("tooltip.energy.count", "储存能量");
		translationBuilder.add("sweeping_ability_title", "横扫");
		translationBuilder.add("sweeping_ability_detail", "使武器可以横扫，每次横扫消耗1点能量。");
		translationBuilder.add("slashing_ability_title", "斩击");
		translationBuilder.add("slashing_ability_detail", "右键消耗" + toString(SlashingAbility.EnergyCosume) +
														  "点能量充能，充能后的第一次横扫攻击额外对前方一定范围内所有敌人造成" +
														  toString(SlashingAbility.AbilityDamage) +
														  "点伤害，并使它们流血" +
														  toString((float) SlashingAbility.EffectDuration / 20) +
														  "秒。流血的敌人每秒损失1点生命值。");
		translationBuilder.add("infiniteslashing_ability_title", "无限斩击");
		translationBuilder.add("infiniteslashing_ability_detail",
				"你的武器不再消耗能量。横扫攻击将额外对前方大范围内所有敌人造成" +
				toString(InfiniteSlashingAbility.AbilityDamage) + "点伤害，并使它们流血" +
				toString((float) InfiniteSlashingAbility.EffectDuration / 20) + "秒。流血的敌人每秒损失" +
				toString(InfiniteSlashingAbility.EffectAmplipier + 1) + "点生命值。本技能冷却时间为" +
				toString((float) InfiniteSlashingAbility.AbilityDuration / 20) + "s。");
		translationBuilder.add("banedabilitydetail", "你还不能使用此能力。");
		translationBuilder.add("tooltip.rune.needlevel", "需求等级");
		translationBuilder.add(IWBlocks.FORGING_BLOCK, "符文锻造台");
		translationBuilder.add("tooltip.defaultenchantment", "自带：");
		try
		{
			Path existingFilePath = generator.getModContainer().findPath("assets/interestingworld/lang/zh_cn.json")
					.get();
			translationBuilder.add(existingFilePath);
		} catch (Exception ignored)
		{
		}
	}

}
