package org.yang.iw.ability;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.yang.iw.IWItemGroups;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.DependRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.component.AbilityComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.item.IWItems;
import org.yang.iw.item.heart.AbilityHeart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.yang.iw.util.style.TextStyle.numberToString;

@DataGenSupplier
@DependRegister(depends = IWItems.class)
public class IWAbilities
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
		LoadTime.assertLoaded(IWItems.class);
	}

	private static final List<AbstractAbility> ABILITY_LIST = new ArrayList<>();
	public static final AbstractAbility SLASHING_ABILITY = register(
			new SlashingAbility(new IntrusiveTag<AbilityHeart>().addInclude(IWItems.TINKER_HEART)), "斩击",
			"横扫攻击消耗%s点能量对前方敌人造成%s点伤害，该技能冷却%ss。".formatted(
					numberToString(SlashingAbility.EnergyCosume), numberToString(SlashingAbility.AbilityDamage),
					numberToString(SlashingAbility.AbilityDuration / 20f)));
	public static final AbstractAbility REPEATSLASHING_ABILITY = register(
			new RepeatSlashingAbility(SLASHING_ABILITY.tag), "连斩",
			"横扫攻击消耗" + numberToString(RepeatSlashingAbility.EnergyConsume) + "点对前方敌人造成" +
			numberToString(RepeatSlashingAbility.FirstAttackDamage) +
			"点伤害，并强化后两次横扫。第一次横扫对同样范围敌人造成" +
			numberToString(RepeatSlashingAbility.SecondAttackDamage) + "点伤害，第二次横扫对更大范围敌人造成" +
			numberToString(RepeatSlashingAbility.ThirdAttackDamage) +
			"伤害，并击退它们。如果短时间内没有将两次攻击全部打出，将会返还一部分的能量值。");
	public static final AbstractAbility BOOSTREPEATSLASHING_ABILITY = register(
			new BoostRepeatSlashingAbility(SLASHING_ABILITY.tag), "强化连斩",
			"横扫攻击消耗" + numberToString(BoostRepeatSlashingAbility.EnergyConsume) + "点对前方敌人造成" +
			numberToString(BoostRepeatSlashingAbility.FirstAttackDamage) +
			"点伤害，并强化后两次攻击。第一次攻击对同样范围敌人造成" +
			numberToString(BoostRepeatSlashingAbility.SecondAttackDamage) + "点伤害，第二次攻击对更大范围敌人造成" +
			numberToString(BoostRepeatSlashingAbility.ThirdAttackDamage) +
			"伤害，并击退它们。如果短时间内没有将两次攻击全部打出，将会返还一部分的能量值。");
	public static final AbstractAbility INFINITESLASHING_ABILITY = register(
			new InfiniteSlashingAbility(SLASHING_ABILITY.tag), "无限斩击",
			"横扫攻击将对前方敌人造成%s点伤害，该技能冷却%ss。连续横扫会减少冷却时间，%s次后最终减少至%ss。右键%ss蓄力，蓄力后的%ss内横扫攻击伤害增加到%s，且普通攻击也能对前方敌人造成%s伤害。".formatted(
					numberToString(InfiniteSlashingAbility.AbilityDamage),
					numberToString(InfiniteSlashingAbility.MaxAbilityDuration / 20f), numberToString((float) (
							(InfiniteSlashingAbility.MaxAbilityDuration - InfiniteSlashingAbility.MinAbilityDuration +
							 InfiniteSlashingAbility.AbilityDurationDownPerHit - 1) /
							InfiniteSlashingAbility.AbilityDurationDownPerHit)),
					numberToString(InfiniteSlashingAbility.MinAbilityDuration / 20f),
					numberToString(InfiniteSlashingAbility.ChargeTime / 20f),
					numberToString(InfiniteSlashingAbility.ChargedTime / 20f),
					numberToString(InfiniteSlashingAbility.ChargedSweepDamage),
					numberToString(InfiniteSlashingAbility.ChargedAttackDamage)));
	public static final AbstractAbility SWEETCURSE_ABILITY = register(
			new SweetCurseAbility(new IntrusiveTag<AbilityHeart>().addInclude(IWItems.CHERRY_HEART)), "甜蜜诅咒",
			"右键消耗" + numberToString(SweetCurseAbility.EnergyConsume) + "点能量使你周围的敌人受到" +
			numberToString((float) SweetCurseAbility.EffectDuration / 20) +
			"s甜蜜诅咒，受到诅咒的敌人将缓慢恢复生命，但其受到的伤害将增加" +
			numberToString(SweetCurseAbility.HurtingAmplifier + 1) + "0%。");
	public static final AbstractAbility INFINITECURSE_ABILITY = register(
			new InfiniteCurseAbility(SWEETCURSE_ABILITY.tag), "无限诅咒",
			"右键对周围大范围敌人施加永久诅咒并击退它们。受到诅咒的敌人将更容易受伤，且其收到的伤害将增加" +
			numberToString(InfiniteCurseAbility.HurtingAmplifier + 1) + "0%。如果敌人在被施加诅咒后" +
			numberToString((float) InfiniteCurseAbility.ExplodeTick / 20) + "秒内未受到伤害，则它会爆炸，并受到" +
			numberToString(InfiniteCurseAbility.ExplodeDamage) + "伤害。");
	public static final AbstractAbility EVISCERATE_ABILITY = register(
			new EviscerateAbility(new IntrusiveTag<AbilityHeart>().addInclude(IWItems.BLOOD_HEART)), "剔骨",
			"横扫攻击消耗" + numberToString(EviscerateAbility.EnergyConsume) + "点能量使前方小范围敌人流血" +
			numberToString((float) EviscerateAbility.EffectDuration / 20) +
			"秒，并加剧已经流血敌人的流血效果。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果" +
			numberToString(EviscerateAbility.PerLevelAbilityDamage) + "点。");
	public static final AbstractAbility INFINITEEVISCERATE_ABILITY = register(
			new InfiniteEviscerateAbility(EVISCERATE_ABILITY.tag), "无限剔骨",
			"横扫攻击使前方一定范围敌人流血" + numberToString((float) InfiniteEviscerateAbility.EffectDuration / 20) +
			"秒，并加剧已经流血敌人的流血效果。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果" +
			numberToString(InfiniteEviscerateAbility.PerLevelAbilityDamage) + "点。");

	public static void initialize()
	{
	}

	public static void forEach(Consumer<AbstractAbility> consumer)
	{
		ABILITY_LIST.forEach(consumer);
	}

	private static AbstractAbility register(AbstractAbility ability, String name, String tip)
	{
		TranslationPool.addString("ability.title." + ability.id(), name);
		TranslationPool.addString("ability.tip." + ability.id(), tip);
		ability.tag.iterateContent(abstractHeart -> {
			if (!ability.isEmpty())
			{
				IWItemGroups.addItemToGroup((context, entries) -> {
					var stack = abstractHeart.getDefaultStack();
					stack.set(IWComponents.ABILITY, new AbilityComponent(ability));
					entries.add(stack);
				}, IWItemGroups.RUNES_GROUP);
			}
		});
		ABILITY_LIST.add(ability);
		return Registry.register(IWRegistries.ABILITY, RegistryKey.of(IWRegistryKeys.ABILITY, ability.identifier()),
				ability);
	}
}
