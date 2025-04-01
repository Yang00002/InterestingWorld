package org.yang.iw;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.datagen.tag.DamageTypeTagPool;
import org.yang.iw.util.Base;
import org.yang.iw.util.KeyTagInclude;

@RegistryCollector
public class IWDamageTypeTags
{

	private static TagKey<DamageType> createTag(String id)
	{
		return TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Base.MOD_ID, id));
	}

	private static TagKey<DamageType> createTag(String id, KeyTagInclude<DamageType> includes)
	{
		var ret = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Base.MOD_ID, id));
		includes.map(item -> DamageTypeTagPool.add(ret, item), tag -> DamageTypeTagPool.add(ret, tag));
		return ret;
	}

	public static TagKey<DamageType> FIRE_EXPLODE = createTag("fire_explode",
			new KeyTagInclude<DamageType>().add(DamageTypeTags.IS_FIRE).add(DamageTypeTags.IS_EXPLOSION));
}
