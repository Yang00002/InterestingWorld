package org.yang.iw.util.constants;

public class Numbers
{
	public static boolean floatEqual(float var1, float var2)
	{
		float ep = var1 - var2;
		return ep > -FLOAT_EPSILON && ep < FLOAT_EPSILON;
	}

	public static boolean floatEqual2Ep(float var1, float var2)
	{
		float ep = var1 - var2;
		return ep > -2 * FLOAT_EPSILON && ep < 2 * FLOAT_EPSILON;
	}

	public static final double DOUBLE_EPSILON = 2.2204460492503131e-016;
	public static final double FLOAT_EPSILON = 1.192092896e-07F;
}
