package org.yang.iw;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.KEPair;
import org.yang.iw.api.register.ServerDependLoader;
import org.yang.iw.datagen.tag.DamageTypeTagPool;

import java.util.ArrayList;

import static org.yang.iw.util.Base.MOD_ID;

@ServerDependLoader
public class IWDamageTypes
{
	private static ArrayList<DamageTypeKEPair> LIST = new ArrayList<>();

	public static class DamageTypeKEPair extends KEPair<DamageType>
	{
		public DamageTypeKEPair(RegistryKey<DamageType> key)
		{
			super(key);
		}

		private void load(Registry<DamageType> registry)
		{
			this.entry = registry.getOptional(this.key).orElse(null);
		}
	}

	public static final DamageTypeKEPair ENERGY_MELEE = damageType("energe_melee", DamageTypeTags.BYPASSES_COOLDOWN,
			DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.PANIC_CAUSES,
			DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS);
	public static final DamageTypeKEPair ENERGY_EXPLODE = damageType("energe_explode",
			DamageTypeTags.BYPASSES_COOLDOWN,
			DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.PANIC_CAUSES,
			DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS, DamageTypeTags.BYPASSES_ARMOR,
			DamageTypeTags.BYPASSES_WOLF_ARMOR, DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.NO_KNOCKBACK,
			DamageTypeTags.IS_EXPLOSION);
	public static final DamageTypeKEPair BLOOD_EFFECT = damageType("blood_effect", DamageTypeTags.BYPASSES_COOLDOWN,
			DamageTypeTags.AVOIDS_GUARDIAN_THORNS, DamageTypeTags.PANIC_CAUSES, DamageTypeTags.NO_KNOCKBACK,
			DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_WOLF_ARMOR);

	public static DamageTypeKEPair damageType(String id, TagKey<DamageType>... tags)
	{
		var ret = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, id));
		for (TagKey<DamageType> tag : tags)
		{
			DamageTypeTagPool.add(tag, ret);
		}
		var r = new DamageTypeKEPair(ret);
		LIST.add(r);
		return r;
	}

	public static void boostrap(MinecraftServer server)
	{
		var manager = server.getRegistryManager();
		var op = manager.getOptional(RegistryKeys.DAMAGE_TYPE);
		if (op.isEmpty()) throw new RuntimeException("DAMAGE_TYPE Registry does not exist on current server.");
		var registry = op.get();
		LIST.forEach(l -> l.load(registry));
	}
}
