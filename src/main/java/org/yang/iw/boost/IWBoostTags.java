package org.yang.iw.boost;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.datagen.tag.BoostTagPool;
import org.yang.iw.util.Base;
import org.yang.iw.api.tag.TagInclude;

public class IWBoostTags
{
	private static TagKey<AbstractBoost> createTag(String id)
	{
		return TagKey.of(IWRegistryKeys.BOOST, Identifier.of(Base.MOD_ID, id));
	}

	private static TagKey<AbstractBoost> createTag(String id, TagInclude<AbstractBoost> includes)
	{
		var ret = TagKey.of(IWRegistryKeys.BOOST, Identifier.of(Base.MOD_ID, id));
		includes.map(item -> BoostTagPool.add(ret, item), tag -> BoostTagPool.add(ret, tag));
		return ret;
	}

	public static final TagKey<AbstractBoost> SHARPNESS_FAMILY = createTag("sharpness_family",
			new TagInclude<AbstractBoost>().add(() -> IWBoosts.SHARPNESS).add(() -> IWBoosts.FAST_ATTACK)
					.add(() -> IWBoosts.SMITE).add(() -> IWBoosts.BANE_OF_ARTHROPODS)
					.add(() -> IWBoosts.BIG_NOSE_LOVER));
}
