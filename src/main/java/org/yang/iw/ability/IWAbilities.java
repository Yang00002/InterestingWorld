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
import java.util.function.Supplier;

import static org.yang.iw.util.style.TextStyle.*;

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
			() -> "横扫攻击消耗%s能量对前方[%s格,%s°]敌人造成%s伤害，并击退它们[%s]，冷却%ss。".formatted(
					numberToString(SlashingAbility.EnergyConsume), numberToString(SlashingAbility.AttackMaxLength),
					cosineTo2Angle(SlashingAbility.AttackMaxAngleCosine), numberToString(SlashingAbility.AttackDamage),
					numberToString(SlashingAbility.KnockbackDistance), tickToSecond(SlashingAbility.CooldownTicks)));
	public static final AbstractAbility REPEATSLASHING_ABILITY = register(
			new RepeatSlashingAbility(SLASHING_ABILITY.tag), "连斩",
			() -> ("横扫攻击消耗%s能量对前方[%s格,%s°]敌人造成%s伤害，并强化后两次横扫。第一次对前方[%s格,%s°]敌人造成%s伤害，第二次对更大范围[%s格," +
				   "%s°]敌人造成%s伤害，并并击退它们[%s]。如果短时间内[%ss;%ss]没有将两次攻击全部打出，将返还一部分能量[%s;%s]。").formatted(
					numberToString(RepeatSlashingAbility.EnergyConsume),
					numberToString(RepeatSlashingAbility.FirstAttackRange),
					cosineTo2Angle(RepeatSlashingAbility.FirstAttackAngleCosine),
					numberToString(RepeatSlashingAbility.FirstAttackDamage),
					numberToString(RepeatSlashingAbility.SecondAttackRange),
					cosineTo2Angle(RepeatSlashingAbility.SecondAttackAngleCosine),
					numberToString(RepeatSlashingAbility.SecondAttackDamage),
					numberToString(RepeatSlashingAbility.ThirdAttackRange),
					cosineTo2Angle(RepeatSlashingAbility.ThirdAttackAngleCosine),
					numberToString(RepeatSlashingAbility.ThirdAttackDamage),
					numberToString(RepeatSlashingAbility.ThirdAttackKnockback),
					tickToSecond(RepeatSlashingAbility.SecondAttackTimeLimit),
					tickToSecond(RepeatSlashingAbility.ThirdAttackTimeLimit),
					numberToString(RepeatSlashingAbility.FirstAttackReturnEnergy),
					numberToString(RepeatSlashingAbility.SecondAttackReturnEnergy)));
	public static final AbstractAbility BOOSTREPEATSLASHING_ABILITY = register(
			new BoostRepeatSlashingAbility(SLASHING_ABILITY.tag), "强化连斩",
			() -> ("横扫攻击消耗%s能量对前方[%s格,%s°]敌人造成%s伤害，并强化后两次攻击。第一次对前方[%s格,%s°]敌人造成%s伤害，第二次对更大范围[%s格," +
				   "%s°]敌人造成%s伤害，并并击退它们[%s]。如果短时间内[%ss;%ss]没有将两次攻击全部打出，将返还一部分能量[%s;%s]。").formatted(
					numberToString(BoostRepeatSlashingAbility.EnergyConsume),
					numberToString(BoostRepeatSlashingAbility.FirstAttackRange),
					cosineTo2Angle(BoostRepeatSlashingAbility.FirstAttackAngleCosine),
					numberToString(BoostRepeatSlashingAbility.FirstAttackDamage),
					numberToString(BoostRepeatSlashingAbility.SecondAttackRange),
					cosineTo2Angle(BoostRepeatSlashingAbility.SecondAttackAngleCosine),
					numberToString(BoostRepeatSlashingAbility.SecondAttackDamage),
					numberToString(BoostRepeatSlashingAbility.ThirdAttackRange),
					cosineTo2Angle(BoostRepeatSlashingAbility.ThirdAttackAngleCosine),
					numberToString(BoostRepeatSlashingAbility.ThirdAttackDamage),
					numberToString(BoostRepeatSlashingAbility.ThirdAttackKnockback),
					tickToSecond(BoostRepeatSlashingAbility.SecondAttackTimeLimit),
					tickToSecond(BoostRepeatSlashingAbility.ThirdAttackTimeLimit),
					numberToString(BoostRepeatSlashingAbility.FirstAttackReturnEnergy),
					numberToString(BoostRepeatSlashingAbility.SecondAttackReturnEnergy)));
	public static final AbstractAbility INFINITESLASHING_ABILITY = register(
			new InfiniteSlashingAbility(SLASHING_ABILITY.tag), "无限斩击",
			() -> ("横扫攻击对前方[%s格,%s°]敌人造成%s伤害，并击退它们[%s]，冷却%ss。每次连续横扫会减少%ss冷却时间，最多减少至%ss。右键充能，充能后%ss内横扫对前方[%s格," +
				   "%s°]敌人造成%s伤害，%s击退；其它攻击对前方[%s格,%s°]敌人造成%s伤害，%s击退。充能结束会产生%ss冷却。").formatted(
					numberToString(InfiniteSlashingAbility.AttackMaxLength),
					cosineTo2Angle(InfiniteSlashingAbility.AttackMaxAngleCosine),
					numberToString(InfiniteSlashingAbility.AbilityDamage),
					numberToString(InfiniteSlashingAbility.KnockbackDistance),
					tickToSecond(InfiniteSlashingAbility.MaxAbilityDuration),
					tickToSecond(InfiniteSlashingAbility.AbilityDurationDownPerHit), tickToSecond(
							InfiniteSlashingAbility.MaxAbilityDuration -
							InfiniteSlashingAbility.MaxChargeStep * InfiniteSlashingAbility.AbilityDurationDownPerHit),
					tickToSecond(InfiniteSlashingAbility.ChargedTime),
					numberToString(InfiniteSlashingAbility.ChargedSweepMaxLength),
					cosineTo2Angle(InfiniteSlashingAbility.ChargedSweepMaxAngleCosine),
					numberToString(InfiniteSlashingAbility.ChargedSweepDamage),
					numberToString(InfiniteSlashingAbility.ChargedSweepKnockback),
					numberToString(InfiniteSlashingAbility.ChargedAttackMaxLength),
					cosineTo2Angle(InfiniteSlashingAbility.ChargedAttackMaxAngleCosine),
					numberToString(InfiniteSlashingAbility.ChargedAttackDamage),
					numberToString(InfiniteSlashingAbility.ChargedAttackKnockback),
					tickToSecond(InfiniteSlashingAbility.ChargeTime)));
	public static final AbstractAbility SWEETCURSE_ABILITY = register(
			new SweetCurseAbility(new IntrusiveTag<AbilityHeart>().addInclude(IWItems.CHERRY_HEART)), "甜蜜诅咒",
			() -> "右键消耗%s能量对你周围%s格的敌人施加%ss甜蜜诅咒。受到诅咒的敌人将缓慢恢复生命[生命恢复%s]，但其受到的伤害增加%s0%%。此能力冷却%ss。".formatted(
					numberToString(SweetCurseAbility.EnergyConsume), numberToString(SweetCurseAbility.AttackMaxLength),
					tickToSecond(SweetCurseAbility.EffectDuration),
					getNumberString(SweetCurseAbility.RegenAmplifier + 1),
					numberToString(SweetCurseAbility.HurtingAmplifier + 1),
					tickToSecond(SweetCurseAbility.AbilityDuration)));
	public static final AbstractAbility INFINITECURSE_ABILITY = register(
			new InfiniteCurseAbility(SWEETCURSE_ABILITY.tag), "无限诅咒",
			() -> ("右键对周围%s格敌人施加永久诅咒并击退它们[%s]。受到诅咒的敌人无敌帧降低%ss，且其受到的伤害增加%s0%%。如果敌人在被施加诅咒后连续%ss秒未受到伤害或再次被诅咒，它会爆炸并受到%s" +
				   "伤害。此能力冷却%ss。").formatted(numberToString(InfiniteCurseAbility.AttackMaxLength),
					numberToString(InfiniteCurseAbility.Knockback),
					tickToSecond(InfiniteCurseAbility.CooldownAmplifier + 1),
					numberToString(InfiniteCurseAbility.HurtingAmplifier + 1),
					tickToSecond(InfiniteCurseAbility.ExplodeTick), numberToString(InfiniteCurseAbility.ExplodeDamage),
					tickToSecond(InfiniteCurseAbility.AbilityDuration)));

	public static final AbstractAbility EVISCERATE_ABILITY = register(
			new EviscerateAbility(new IntrusiveTag<AbilityHeart>().addInclude(IWItems.BLOOD_HEART)), "剔骨",
			() -> ("横扫攻击消耗%s能量使前方[%s格,%s°]敌人流血（已经流血的敌人流血等级加1，最多%s级），并击退它们[%s]。此技能冷却%ss" +
				   "。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果%s点。").formatted(
					numberToString(EviscerateAbility.EnergyConsume), numberToString(EviscerateAbility.AttackMaxLength),
					cosineTo2Angle(EviscerateAbility.AttackMaxAngleCosine), numberToString(EviscerateAbility.LevelCap),
					numberToString(EviscerateAbility.Knockback), tickToSecond(EviscerateAbility.AbilityCooldown),
					numberToString(EviscerateAbility.PerLevelAbilityDamage)));

	public static final AbstractAbility INFINITEEVISCERATE_ABILITY = register(
			new InfiniteEviscerateAbility(EVISCERATE_ABILITY.tag), "无限剔骨",
			() -> ("横扫攻击使前方[%s格,%s°]敌人流血（已经流血的敌人流血等级加1，最多%s级），并击退它们[%s]。此技能冷却%ss" +
				   "。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果%s点。").formatted(
					numberToString(InfiniteEviscerateAbility.AttackMaxLength),
					cosineTo2Angle(InfiniteEviscerateAbility.AttackMaxAngleCosine),
					numberToString(InfiniteEviscerateAbility.LevelCap),
					numberToString(InfiniteEviscerateAbility.Knockback),
					tickToSecond(InfiniteEviscerateAbility.AbilityCooldown),
					numberToString(InfiniteEviscerateAbility.PerLevelAbilityDamage)));

	public static void initialize()
	{
	}

	public static void forEach(Consumer<AbstractAbility> consumer)
	{
		ABILITY_LIST.forEach(consumer);
	}

	private static AbstractAbility register(AbstractAbility ability, String name, Supplier<String> tip)
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
