package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.yang.interestingworld.*;
import org.yang.interestingworld.rune.ability.InfiniteCurseAbility;
import org.yang.interestingworld.rune.ability.InfiniteSlashingAbility;
import org.yang.interestingworld.rune.ability.SlashingAbility;
import org.yang.interestingworld.rune.ability.SweetCurseAbility;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.yang.interestingworld.IWUtil.TextStyle.numberToString;
import static org.yang.interestingworld.datagen.IWDataGen.generator;

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
		translationBuilder.add("enchantment.level.11", "XI");
		translationBuilder.add("enchantment.level.12", "XII");
		translationBuilder.add("enchantment.level.13", "XIII");
		translationBuilder.add("enchantment.level.14", "XIV");
		translationBuilder.add("enchantment.level.15", "XV");
		translationBuilder.add("enchantment.level.16", "XVI");
		translationBuilder.add("enchantment.level.17", "XVII");
		translationBuilder.add("enchantment.level.18", "XVIII");
		translationBuilder.add("enchantment.level.19", "XIX");
		translationBuilder.add("enchantment.level.20", "XX");
		translationBuilder.add("enchantment.level.21", "XXI");
		translationBuilder.add("enchantment.level.22", "XXII");
		translationBuilder.add("enchantment.level.23", "XXIII");
		translationBuilder.add("enchantment.level.24", "XXIV");
		translationBuilder.add(IWItems.BLAZEROD, "烈焰棒子");
		translationBuilder.add(IWItems.STICK, "棍子");
		translationBuilder.add(IWItems.STONE_SWORD, "石剑");
		translationBuilder.add(IWItems.IRON_SWORD, "铁剑");
		translationBuilder.add(IWItems.GOLDEN_SWORD, "金剑");
		translationBuilder.add(IWItems.DIAMOND_SWORD, "钻石剑");
		translationBuilder.add(IWItems.NETHERITE_SWORD, "下界合金剑");
		translationBuilder.add(IWItems.EMPTY_RUNE, "空白符文");
		translationBuilder.add(IWItems.ENCHANTMENT_RUNE, "附魔符文");
		translationBuilder.add("abilityrune.suffix", "符文");
		translationBuilder.add(IWItemGroups.TOOLS_GROUP, "IW: 工具");
		translationBuilder.add(IWItemGroups.RUNES_GROUP, "IW：符文");
		translationBuilder.add(IWItemGroups.BLOCKS_GROUP, "IW：方块");
		translationBuilder.add(IWEffects.BLOOD.value(), "流血");
		translationBuilder.add(IWEffects.COOLDOWN.value(), "受击");
		translationBuilder.add(IWEffects.HURTING.value(), "易伤");
		translationBuilder.add("forgingblock.title.all", "修复、附魔、升级和能力修改");
		translationBuilder.add("forgingblock.title.rune_enchant", "符文附魔");
		translationBuilder.add("forgingblock.text.rune_enchant.2", "放入工具以指示附魔种类");
		translationBuilder.add("forgingblock.title.enchant", "附魔");
		translationBuilder.add("forgingblock.text.level", "你还不能使用该符文");
		translationBuilder.add("forgingblock.text.enchant.3xpl", "附魔花费：");
		translationBuilder.add("forgingblock.text.enchant.3xpr", "%s / %s");
		translationBuilder.add("forgingblock.text.enchant.4", "没有可用的附魔");
		translationBuilder.add("forgingblock.text.enchant.5", "放入工具以进行附魔");
		translationBuilder.add("forgingblock.title.repair", "修复");
		translationBuilder.add("forgingblock.text.repair.6", "需要原料以进行修复");
		translationBuilder.add("forgingblock.text.repair.7l", "修复花费：");
		translationBuilder.add("forgingblock.text.repair.7r", "%s / %s 材料 %s");
		translationBuilder.add("forgingblock.title.abilityadd", "能力附加");
		translationBuilder.add("forgingblock.text.abilityadd.9", "能力和工具不兼容");
		translationBuilder.add("forgingblock.text.abilityadd.10", "工具已经存在能力");
		translationBuilder.add("forgingblock.text.abilityadd.11", "放入工具以附加能力");
		translationBuilder.add("forgingblock.title.abilityremove", "能力提取");
		translationBuilder.add("forgingblock.text.abilityremove.12", "工具损坏概率：%s%%");
		translationBuilder.add("forgingblock.text.abilityremove.13", "没有可提取的能力");
		translationBuilder.add("forgingblock.text.abilityremove.14", "放入工具以提取能力");
		translationBuilder.add("enchantment.interestingworld.fast_hit", "迅捷打击");
		translationBuilder.add("enchantment.interestingworld.balance", "均衡");
		translationBuilder.add("enchantment.interestingworld.plentiful", "充盈");
		translationBuilder.add("enchantment.interestingworld.rune_boost", "符文强化");
		translationBuilder.add("enchantment.interestingworld.energy_efficiency", "聚能");
		translationBuilder.add("enchantment.interestingworld.lucky", "幸运");
		translationBuilder.add("enchantment.interestingworld.fast_attack", "快速挥舞");
		translationBuilder.add("tooltip.energy.count", "储存能量");
		translationBuilder.add("sweeping_ability_title", "横扫");
		translationBuilder.add("sweeping_ability_detail", "使武器可以横扫，每次横扫消耗1点能量。");
		translationBuilder.add("slashing_ability_title", "斩击");
		translationBuilder.add("slashing_ability_detail", "右键消耗" + numberToString(SlashingAbility.EnergyCosume) +
														  "点能量充能，充能后的第一次横扫攻击额外对前方一定范围内所有敌人造成" +
														  numberToString(SlashingAbility.AbilityDamage) +
														  "点伤害，并使它们流血" +
														  numberToString((float) SlashingAbility.EffectDuration / 20) +
														  "秒。流血的敌人每秒损失1点生命值。本技能冷却时间为" +
														  numberToString((float) SlashingAbility.AbilityDuration / 20) +
														  "s。");
		translationBuilder.add("infiniteslashing_ability_title", "无限斩击");
		translationBuilder.add("infiniteslashing_ability_detail",
				"你的武器不再消耗能量。横扫攻击将额外对前方大范围内所有敌人造成" +
				numberToString(InfiniteSlashingAbility.AbilityDamage) + "点伤害，并使它们流血" +
				numberToString((float) InfiniteSlashingAbility.EffectDuration / 20) + "秒。流血的敌人每秒损失" +
				numberToString(InfiniteSlashingAbility.EffectAmplipier + 1) + "点生命值。本技能冷却时间为" +
				numberToString((float) InfiniteSlashingAbility.AbilityDuration / 20) + "s。");
		translationBuilder.add("sweetcurse_ability_title", "甜蜜诅咒");
		translationBuilder.add("sweetcurse_ability_detail",
				"右键消耗" + numberToString(SweetCurseAbility.EnergyCosume) + "点能量使你周围的敌人受到" +
				numberToString((float) SweetCurseAbility.EffectDuration / 20) +
				"s甜蜜诅咒，受到诅咒的敌人将缓慢恢复生命，但其受到的伤害将增加" +
				numberToString(SweetCurseAbility.HurtingAmplifier + 1) + "0%。本技能冷却时间为" +
				numberToString((float) SweetCurseAbility.AbilityDuration / 20) + "s。");
		translationBuilder.add("infinitecurse_ability_title", "无限诅咒");
		translationBuilder.add("infinitecurse_ability_detail",
				"你的武器不再消耗能量。右键对周围大范围敌人施加永久诅咒, 受到诅咒的敌人将更容易受伤, 且其收到的伤害将增加" +
				numberToString(InfiniteCurseAbility.HurtingAmplifier + 1) + "0%。本技能冷却时间为" +
				numberToString((float) InfiniteCurseAbility.AbilityDuration / 20) + "s。");
		translationBuilder.add("banedabilitydetail", "你还不能使用此能力。");
		translationBuilder.add("tooltip.rune.needlevel", "需求等级");
		translationBuilder.add(IWBlocks.FORGING_BLOCK, "符文锻造台");
		translationBuilder.add("tooltip.defaultenchantment", "自带：");
		translationBuilder.add("sound.interestingworld.abilitybar_full", "能量充满");
		translationBuilder.add("sound.interestingworld.double_sweep", "斩击");
		translationBuilder.add("iw.worldlevel_set_success", "世界等级已设置为 %s。");
		translationBuilder.add("iw.worldlevel_set_fail", "无法将世界等级设置为 %s，只能设置为一个非负数。");
		translationBuilder.add(IWEntities.DUMMY, "测试假人");
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
