package org.yang.iw.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class IWStatusEffectUtil
{
	private static int keepConsistence(int level0, int time0, int level1, int time1, int consistenceTimeTick)
	{
		int d = time0 / consistenceTimeTick;
		int add = (level0 * time0) / level1;
		int t2 = time1 + add;
		int l = t2 - d;
		if (l < 0) return time1;
		int tl = l % consistenceTimeTick;
		if (tl >= add) return time1;
		else return t2 - tl;
	}

	public static void addHiddenStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect, int durationTick)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, false));
			return;
		}
		int duration = pre.getDuration();
		if (duration == -1) return;
		else if (durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, false));
			return;
		}
		int level = pre.getAmplifier() + 1;
		int nextDuration = (level * duration + durationTick) / level;
		entity.removeStatusEffectVanilla(effect);
		entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, level - 1, false, false));
	}

	public static void addStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect, int durationTick)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0));
			return;
		}
		int duration = pre.getDuration();
		if (duration == -1) return;
		else if (durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0));
			return;
		}
		int level = pre.getAmplifier() + 1;
		int nextDuration = (level * duration + durationTick) / level;
		entity.removeStatusEffectVanilla(effect);
		entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, level - 1));
	}


	public static void addHiddenStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect, int durationTick
			, int amplifier)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
			return;
		}
		int duration = pre.getDuration();
		if (duration == -1 || durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
			return;
		}
		amplifier++;
		int level = pre.getAmplifier() + 1;
		int maxlevel = Math.max(level, amplifier);

		int nextDuration = (level * duration + durationTick * amplifier) / maxlevel;
		entity.removeStatusEffectVanilla(effect);
		entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, maxlevel - 1, false, false));
	}

	public static void addStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect, int durationTick,
									   int amplifier)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier));
			return;
		}
		int duration = pre.getDuration();
		if (duration == -1 || durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
			return;
		}
		amplifier++;
		int level = pre.getAmplifier() + 1;
		int maxlevel = Math.max(level, amplifier);

		int nextDuration = (level * duration + durationTick * amplifier) / maxlevel;
		entity.removeStatusEffectVanilla(effect);
		entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, maxlevel - 1));
	}

	public static void addHiddenStatusEffectWithConsistence(LivingEntity entity, RegistryEntry<StatusEffect> effect,
															int durationTick, int consistenceTimeTick)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, true));
			return;
		}
		int preDuration = pre.getDuration();
		if (preDuration == -1) return;
		else if (durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, true));
			return;
		}
		int prelevel = pre.getAmplifier() + 1;
		entity.removeStatusEffectVanilla(effect);
		if (prelevel > 1)

			entity.addStatusEffect(new StatusEffectInstance(effect,
					keepConsistence(1, durationTick, prelevel, preDuration, consistenceTimeTick), prelevel - 1, false,
					true));
		else entity.addStatusEffect(new StatusEffectInstance(effect,
				keepConsistence(prelevel, preDuration, 1, durationTick, consistenceTimeTick), 0, false, true));
	}

	public static void addStatusEffectWithConsistence(LivingEntity entity, RegistryEntry<StatusEffect> effect,
													  int durationTick, int consistenceTimeTick)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0));
			return;
		}
		int preDuration = pre.getDuration();
		if (preDuration == -1) return;
		else if (durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0));
			return;
		}
		int prelevel = pre.getAmplifier() + 1;
		entity.removeStatusEffectVanilla(effect);
		if (prelevel > 1)

			entity.addStatusEffect(new StatusEffectInstance(effect,
					keepConsistence(1, durationTick, prelevel, preDuration, consistenceTimeTick), prelevel - 1));
		else entity.addStatusEffect(new StatusEffectInstance(effect,
				keepConsistence(prelevel, preDuration, 1, durationTick, consistenceTimeTick), 0));
	}

	public static void addHiddenStatusEffectWithConsistence(LivingEntity entity, RegistryEntry<StatusEffect> effect,
															int durationTick, int amplifier, int consistenceTimeTick)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
			return;
		}
		int preDuration = pre.getDuration();
		if (preDuration == -1 || durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
			return;
		}
		int prelevel = pre.getAmplifier() + 1;
		entity.removeStatusEffectVanilla(effect);
		if (prelevel > amplifier + 1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect,
					keepConsistence(amplifier + 1, durationTick, prelevel, preDuration, consistenceTimeTick),
					prelevel - 1, false, false));
		}
		else entity.addStatusEffect(new StatusEffectInstance(effect,
				keepConsistence(prelevel, preDuration, amplifier + 1, durationTick, consistenceTimeTick), amplifier,
				false, false));

	}

	public static void addStatusEffectWithConsistence(LivingEntity entity, RegistryEntry<StatusEffect> effect,
													  int durationTick, int amplifier, int consistenceTimeTick)
	{
		StatusEffectInstance pre = entity.getStatusEffect(effect);
		if (pre == null)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier));
			return;
		}
		int preDuration = pre.getDuration();
		if (preDuration == -1 || durationTick == -1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier));
			return;
		}
		int prelevel = pre.getAmplifier() + 1;
		entity.removeStatusEffectVanilla(effect);
		if (prelevel > amplifier + 1)
		{
			entity.addStatusEffect(new StatusEffectInstance(effect,
					keepConsistence(amplifier + 1, durationTick, prelevel, preDuration, consistenceTimeTick),
					prelevel - 1));
		}
		else entity.addStatusEffect(new StatusEffectInstance(effect,
				keepConsistence(prelevel, preDuration, amplifier + 1, durationTick, consistenceTimeTick), amplifier));

	}
}
