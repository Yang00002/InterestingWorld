package org.yang.iw.rune_ability;

import org.yang.iw.datagen.language.TranslationPool;

import java.util.LinkedList;
import java.util.function.Consumer;

import static org.yang.iw.util.style.TextStyle.numberToString;

public class IWRuneAbilities
{
	private static AbilityRegister abilityRegister = new AbilityRegister();
	private static AbstractRuneAbility[] ABILITY_LIST = null;
	public static final AbstractRuneAbility DEFAULT_ABILITY = abilityRegister.register(new AbstractRuneAbility());
	public static final AbstractRuneAbility SLASHING_ABILITY = abilityRegister.register("斩击",
			"横扫攻击消耗%s点能量对前方敌人造成%s点伤害，该技能冷却%ss。".formatted(
					numberToString(SlashingAbility.EnergyCosume), numberToString(SlashingAbility.AbilityDamage),
					numberToString(SlashingAbility.AbilityDuration / 20f)), new SlashingAbility());
	public static final AbstractRuneAbility INFINITESLASHING_ABILITY = abilityRegister.register("无限斩击",
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
					numberToString(InfiniteSlashingAbility.ChargedAttackDamage)), new InfiniteSlashingAbility());
	public static final AbstractRuneAbility SWEETCURSE_ABILITY = abilityRegister.register("甜蜜诅咒",
			"右键消耗" + numberToString(SweetCurseAbility.EnergyConsume) + "点能量使你周围的敌人受到" +
			numberToString((float) SweetCurseAbility.EffectDuration / 20) +
			"s甜蜜诅咒，受到诅咒的敌人将缓慢恢复生命，但其受到的伤害将增加" +
			numberToString(SweetCurseAbility.HurtingAmplifier + 1) + "0%。", new SweetCurseAbility());
	public static final AbstractRuneAbility INFINITECURSE_ABILITY = abilityRegister.register("无限诅咒",
			"右键对周围大范围敌人施加永久诅咒并击退它们。受到诅咒的敌人将更容易受伤，且其收到的伤害将增加" +
			numberToString(InfiniteCurseAbility.HurtingAmplifier + 1) + "0%。如果敌人在被施加诅咒后" +
			numberToString((float) InfiniteCurseAbility.ExplodeTick / 20) + "秒内未受到伤害，则它会爆炸，并受到" +
			numberToString(InfiniteCurseAbility.ExplodeDamage) + "伤害。", new InfiniteCurseAbility());
	public static final AbstractRuneAbility REPEATSLASHING_ABILITY = abilityRegister.register("连斩",
			"横扫攻击消耗" + numberToString(RepeatSlashingAbility.EnergyConsume) + "点对前方敌人造成" +
			numberToString(RepeatSlashingAbility.FirstAttackDamage) +
			"点伤害，并强化后两次横扫。第一次横扫对同样范围敌人造成" +
			numberToString(RepeatSlashingAbility.SecondAttackDamage) + "点伤害，第二次横扫对更大范围敌人造成" +
			numberToString(RepeatSlashingAbility.ThirdAttackDamage) +
			"伤害，并击退它们。如果短时间内没有将两次攻击全部打出，将会返还一部分的能量值。", new RepeatSlashingAbility());
	public static final AbstractRuneAbility BOOSTREPEATSLASHING_ABILITY = abilityRegister.register("强化连斩",
			"横扫攻击消耗" + numberToString(BoostRepeatSlashingAbility.EnergyConsume) + "点对前方敌人造成" +
			numberToString(BoostRepeatSlashingAbility.FirstAttackDamage) +
			"点伤害，并强化后两次攻击。第一次攻击对同样范围敌人造成" +
			numberToString(BoostRepeatSlashingAbility.SecondAttackDamage) + "点伤害，第二次攻击对更大范围敌人造成" +
			numberToString(BoostRepeatSlashingAbility.ThirdAttackDamage) +
			"伤害，并击退它们。如果短时间内没有将两次攻击全部打出，将会返还一部分的能量值。",
			new BoostRepeatSlashingAbility());
	public static final AbstractRuneAbility EVISCERATE_ABILITY = abilityRegister.register("剔骨",
			"横扫攻击消耗" + numberToString(EviscerateAbility.EnergyConsume) + "点能量使前方小范围敌人流血" +
			numberToString((float) EviscerateAbility.EffectDuration / 20) +
			"秒，并加剧已经流血敌人的流血效果。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果" +
			numberToString(EviscerateAbility.PerLevelAbilityDamage) + "点。", new EviscerateAbility());
	public static final AbstractRuneAbility INFINITEEVISCERATE_ABILITY = abilityRegister.register("无限剔骨",
			"横扫攻击使前方一定范围敌人流血" + numberToString((float) InfiniteEviscerateAbility.EffectDuration / 20) +
			"秒，并加剧已经流血敌人的流血效果。拥有该能力的武器的横扫攻击将对拥有流血的敌人造成额外伤害，每级效果" +
			numberToString(InfiniteEviscerateAbility.PerLevelAbilityDamage) + "点。", new InfiniteEviscerateAbility());
	private static final short ABILITY_COUNT;

	static
	{
		ABILITY_COUNT = abilityRegister.build();
		abilityRegister = null;
		/*
		 for (int i = 1; i < ABILITY_COUNT; i++)
		{
			int finalI = i;
			IWItemGroups.addItemToGroup((context, entries) -> {
				ItemStack it = IWItems.ABILITY_RUNE.getDefaultStack();
				setAbility(it, ABILITY_LIST[finalI]);
				entries.add(it);
			}, IWItemGroups.RUNES_GROUP);
		}
		 */
	}

	private static class AbilityRegister
	{
		private final LinkedList<AbstractRuneAbility> list = new LinkedList<>();
		private short[] runeIds = null;
		private short[] toolIds = null;
		private short runeIdAllocator = 0;
		private short toolIdAllocator = 0;
		private short abilityCount = 0;

		public AbstractRuneAbility register(AbstractRuneAbility ability)
		{
			list.add(ability);
			ability.index = abilityCount;
			abilityCount++;
			return ability;
		}

		public AbstractRuneAbility register(String abilityName, String abilityTip, AbstractRuneAbility ability)
		{
			list.add(ability);
			ability.index = abilityCount;
			abilityCount++;
			TranslationPool.addString("ability.title." + ability.id(), abilityName);
			TranslationPool.addString("ability.tip." + ability.id(), abilityTip);
			return ability;
		}

		public short build()
		{
			ABILITY_LIST = new AbstractRuneAbility[abilityCount];
			runeIds = new short[abilityCount];
			toolIds = new short[abilityCount];
			int idx = 0;
			for (AbstractRuneAbility i : list)
			{
				ABILITY_LIST[idx] = i;
				runeIds[idx] = -1;
				toolIds[idx] = -1;
				idx++;
			}
			for (int i = 0; i < abilityCount; i++)
			{
				searchRune(i);
				searchTool(i);
				ABILITY_LIST[i].runeIndex = runeIds[i];
				ABILITY_LIST[i].toolIndex = toolIds[i];
			}
			return abilityCount;
		}

		public void searchRune(int idx)
		{
			if (runeIds[idx] == -1)
			{
				AbstractRuneAbility ab = ABILITY_LIST[idx].getRuneIndexParent();
				if (ab == null)
				{
					runeIds[idx] = runeIdAllocator;
					runeIdAllocator++;
				}
				else
				{
					int id2 = ab.index;
					if (runeIds[id2] == -1)
					{
						runeIds[idx] = 0;
						searchRune(id2);
					}
					runeIds[idx] = runeIds[id2];
				}
			}
		}

		public void searchTool(int idx)
		{
			if (toolIds[idx] == -1)
			{
				AbstractRuneAbility ab = ABILITY_LIST[idx].getToolIndexParent();
				if (ab == null)
				{
					toolIds[idx] = toolIdAllocator;
					toolIdAllocator++;
				}
				else
				{
					int id2 = ab.index;
					if (toolIds[id2] == -1)
					{
						toolIds[idx] = 0;
						searchTool(id2);
					}
					toolIds[idx] = toolIds[id2];
				}
			}
		}
	}

	public static void initialize()
	{
	}

	public static AbstractRuneAbility getAbilityofIndex(short idx)
	{
		if (idx >= ABILITY_COUNT) return DEFAULT_ABILITY;
		if (idx <= 0) return DEFAULT_ABILITY;
		return ABILITY_LIST[idx];
	}

	public static void forEach(Consumer<AbstractRuneAbility> consumer)
	{
		for (int i = 1; i < ABILITY_COUNT; i++)
		{
			consumer.accept(ABILITY_LIST[i]);
		}
	}
}
