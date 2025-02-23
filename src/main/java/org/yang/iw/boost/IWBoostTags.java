package org.yang.iw.boost;

import org.yang.iw.api.tag.IntrusiveTag;

public class IWBoostTags
{
	public static final IntrusiveTag<AbstractBoost> SHARPNESS_FAMILY = new IntrusiveTag<AbstractBoost>().addInclude(
			IWBoosts.SHARPNESS).addInclude(IWBoosts.FAST_ATTACK);
}
