package org.yang.iw.mixin.mixin_interface;

import org.yang.iw.api.util.AttributeValueDetail;

public interface InterfaceEntityAttributeInstance
{
	default AttributeValueDetail interestingWorld$getValueDetail()
	{
		return AttributeValueDetail.DEFAULT;
	}
}
