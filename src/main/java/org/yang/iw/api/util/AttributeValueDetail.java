package org.yang.iw.api.util;

public record AttributeValueDetail(float base, float sum, float mul)
{
	public static final AttributeValueDetail DEFAULT = new AttributeValueDetail(1.0f, 1.0f, 0);
}
