package org.yang.iw;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.tag.DamageTypeTagPool;
import org.yang.iw.util.Server;

import static org.yang.iw.util.Base.MOD_ID;

public class IWDamageTypes
{
	public static final RegistryKey<DamageType> ENERGEE_MELEE = damageType("energe_melee",
			DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.PANIC_CAUSES,
			DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS);
	public static final RegistryKey<DamageType> ENERGEE_EXPLODE = damageType("energe_explode",
			DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.PANIC_CAUSES,
			DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS, DamageTypeTags.BYPASSES_ARMOR,
			DamageTypeTags.BYPASSES_WOLF_ARMOR, DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.NO_KNOCKBACK,
			DamageTypeTags.IS_EXPLOSION);
	public static final RegistryKey<DamageType> BLOOD_EFFECT = damageType("blood_effect",
			DamageTypeTags.BYPASSES_COOLDOWN, DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.PANIC_CAUSES,
			DamageTypeTags.NO_KNOCKBACK, DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_WOLF_ARMOR);

	public static RegistryKey<DamageType> damageType(String id, TagKey<DamageType>... tags)
	{
		var ret = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, id));
		for (TagKey<DamageType> tag : tags)
		{
			DamageTypeTagPool.add(tag, ret);
		}
		return ret;
	}

	public static final Server.LoadOnceRegistryEntry<DamageType> ENERGEE_MELEE_ENTRY = damageTypeLoadOnceRegistryEntry(
			ENERGEE_MELEE);
	public static final Server.LoadOnceRegistryEntry<DamageType> ENERGEE_EXPLODE_ENTRY =
			damageTypeLoadOnceRegistryEntry(
			ENERGEE_EXPLODE);
	public static final Server.LoadOnceRegistryEntry<DamageType> BLOOD_EFFECT_ENTRY = damageTypeLoadOnceRegistryEntry(
			BLOOD_EFFECT);

	private static Server.LoadOnceRegistryEntry<DamageType> damageTypeLoadOnceRegistryEntry(RegistryKey<DamageType> damageTypeRegistryKey)
	{
		return Server.getLoadOnceRegistryEntry(damageTypeRegistryKey, RegistryKeys.DAMAGE_TYPE);
	}

	public static void initialize()
	{

	}


}
