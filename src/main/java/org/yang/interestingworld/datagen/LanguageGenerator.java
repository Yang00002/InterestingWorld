package org.yang.interestingworld.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.block.IWBlocks;
import org.yang.interestingworld.effect.IWEffects;
import org.yang.interestingworld.entity.IWEntities;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.rune_ability.*;
import org.yang.interestingworld.rune_upgrade.HeavyUpgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping2Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping3Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping4Upgrade;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.yang.interestingworld.datagen.IWDataGen.generator;
import static org.yang.interestingworld.util.style.TextStyle.numberToString;

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
		translationBuilder.add("iw.key.category", "InterestingWorld 按键绑定");
		translationBuilder.add("iw.key.ability_oc", "开关主手武器能力");
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
		translationBuilder.add(IWItems.HEART, "心");
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
		translationBuilder.add(IWEffects.INFINITECURSE.value(), "无限诅咒");
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
		translationBuilder.add("forgingblock.title.upgrade", "升级");
		translationBuilder.add("forgingblock.text.upgrade.17", "缺少原料");
		translationBuilder.add("forgingblock.text.upgrade.18", "升级和工具不兼容");
		translationBuilder.add("forgingblock.text.upgrade.19", "不能重复升级");
		translationBuilder.add("forgingblock.text.upgrade.20", "放入工具以进行升级");
		translationBuilder.add("enchantment.interestingworld.fast_hit", "迅捷打击");
		translationBuilder.add("enchantment.interestingworld.balance", "均衡");
		translationBuilder.add("enchantment.interestingworld.plentiful", "充盈");
		translationBuilder.add("enchantment.interestingworld.rune_boost", "符文强化");
		translationBuilder.add("enchantment.interestingworld.energy_efficiency", "聚能");
		translationBuilder.add("enchantment.interestingworld.lucky", "幸运");
		translationBuilder.add("enchantment.interestingworld.fast_attack", "快速挥舞");
		translationBuilder.add("tooltip.energy.count", "储存能量");
		translationBuilder.add("sweeping_upgrade_title", "横扫");
		translationBuilder.add("heavy_upgrade_title", "重");
		translationBuilder.add("sweeping_upgrade_detail", "使武器可以横扫。");
		translationBuilder.add("sweeping2_upgrade_detail",
				"使武器可以横扫，并增加" + numberToString(Sweeping2Upgrade.SWEEP_RATIO) + "横扫伤害比率。");
		translationBuilder.add("sweeping3_upgrade_detail",
				"使武器可以横扫，并增加" + numberToString(Sweeping3Upgrade.SWEEP_RATIO) + "横扫伤害比率。");
		translationBuilder.add("sweeping4_upgrade_detail",
				"使武器可以横扫，并增加" + numberToString(Sweeping4Upgrade.SWEEP_RATIO) + "横扫伤害比率。");
		translationBuilder.add("heavy_upgrade_detail",
				"增加" + numberToString(HeavyUpgrade.BASIC_DAMAGE_ADD * 100) + "%基础攻击伤害, 减少" +
				numberToString(HeavyUpgrade.BASIC_SPEED_DOWN * 100) + "%基础攻击速度。");
		translationBuilder.add("attribute.modifier.iwp.0", "+%s %s");
		translationBuilder.add("attribute.modifier.iwp.1", "+%s%% %s");
		translationBuilder.add("attribute.modifier.iwp.2", "x%s %s");
		translationBuilder.add("attribute.modifier.iwt.0", "-%s %s");
		translationBuilder.add("attribute.modifier.iwt.1", "-%s%% %s");
		translationBuilder.add("attribute.modifier.iwt.2", "x%s %s");
		translationBuilder.add("slashing_ability_title", "斩击");
		translationBuilder.add("slashing_ability_detail",
				"横扫攻击消耗" + numberToString(SlashingAbility.EnergyCosume) + "点能量对前方一定范围内所有敌人造成" +
				numberToString(SlashingAbility.AbilityDamage) + "点伤害，并使它们流血" +
				numberToString((float) SlashingAbility.EffectDuration / 20) + "秒。流血的敌人每秒损失1点生命值。");
		translationBuilder.add("repeatslashing_ability_title", "连斩");
		translationBuilder.add("repeatslashing_ability_detail",
				"横扫攻击消耗" + numberToString(RepeatSlashingAbility.EnergyConsume) + "点对前方敌人造成" +
				numberToString(RepeatSlashingAbility.FirstAttackDamage) +
				"点伤害，并强化后两次横扫。第一次横扫对同样范围敌人造成" +
				numberToString(RepeatSlashingAbility.SecondAttackDamage) + "点伤害，第二次横扫对更大范围敌人造成" +
				numberToString(RepeatSlashingAbility.ThirdAttackDamage) +
				"伤害，并击退它们。如果短时间内没有将两次攻击全部打出，将会返还一部分的能量值。");
		translationBuilder.add("boostrepeatslashing_ability_title", "强化连斩");
		translationBuilder.add("boostrepeatslashing_ability_detail",
				"横扫攻击消耗" + numberToString(BoostRepeatSlashingAbility.EnergyConsume) + "点对前方敌人造成" +
				numberToString(BoostRepeatSlashingAbility.FirstAttackDamage) +
				"点伤害，并强化后两次攻击。第一次攻击对同样范围敌人造成" +
				numberToString(BoostRepeatSlashingAbility.SecondAttackDamage) + "点伤害，第二次攻击对更大范围敌人造成" +
				numberToString(BoostRepeatSlashingAbility.ThirdAttackDamage) +
				"伤害，并击退它们。如果短时间内没有将两次攻击全部打出，将会返还一部分的能量值。");
		translationBuilder.add("infiniteslashing_ability_title", "无限斩击");
		translationBuilder.add("infiniteslashing_ability_detail",
				"横扫攻击将对前方大范围内所有敌人造成" + numberToString(InfiniteSlashingAbility.AbilityDamage) +
				"点伤害，并使它们流血" + numberToString((float) InfiniteSlashingAbility.EffectDuration / 20) +
				"秒。流血的敌人每秒损失" + numberToString(InfiniteSlashingAbility.EffectAmplifier + 1) + "点生命值。");
		translationBuilder.add("sweetcurse_ability_title", "甜蜜诅咒");
		translationBuilder.add("sweetcurse_ability_detail",
				"右键消耗" + numberToString(SweetCurseAbility.EnergyConsume) + "点能量使你周围的敌人受到" +
				numberToString((float) SweetCurseAbility.EffectDuration / 20) +
				"s甜蜜诅咒，受到诅咒的敌人将缓慢恢复生命，但其受到的伤害将增加" +
				numberToString(SweetCurseAbility.HurtingAmplifier + 1) + "0%。");
		translationBuilder.add("infinitecurse_ability_title", "无限诅咒");
		translationBuilder.add("infinitecurse_ability_detail",
				"右键对周围大范围敌人施加永久诅咒并击退它们。受到诅咒的敌人将更容易受伤，且其收到的伤害将增加" +
				numberToString(InfiniteCurseAbility.HurtingAmplifier + 1) + "0%。如果敌人在被施加诅咒后" +
				numberToString((float) InfiniteCurseAbility.ExplodeTick / 20) + "秒内未受到伤害，则它会爆炸，并受到" +
				numberToString(InfiniteCurseAbility.ExplodeDamage) + "伤害。");
		translationBuilder.add("eviscerate_ability_title", "剔骨");
		translationBuilder.add("eviscerate_ability_detail",
				"横扫攻击消耗" + numberToString(EviscerateAbility.EnergyConsume) + "点能量使前方小范围敌人流血" +
				numberToString((float) EviscerateAbility.EffectDuration / 20) +
				"秒，并加剧已经流血敌人的流血效果。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果" +
				numberToString(EviscerateAbility.PerLevelAbilityDamage) + "点。");
		translationBuilder.add("infiniteeviscerate_ability_title", "无限剔骨");
		translationBuilder.add("infiniteeviscerate_ability_detail", "横扫攻击使前方一定范围敌人流血" + numberToString(
				(float) InfiniteEviscerateAbility.EffectDuration / 20) +
																	"秒，并加剧已经流血敌人的流血效果。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果" +
																	numberToString(
																			InfiniteEviscerateAbility.PerLevelAbilityDamage) +
																	"点。");
		translationBuilder.add("upgrade.need.txet", "需求：");
		translationBuilder.add("banedabilitydetail", "你还不能使用此能力。");
		translationBuilder.add("banedupgradedetail", "你还不能使用此升级。");
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
