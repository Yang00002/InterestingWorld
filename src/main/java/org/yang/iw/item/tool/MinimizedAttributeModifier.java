package org.yang.iw.item.tool;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.iw.util.constants.Numbers;

import java.util.List;

/**
 * (base + add + add')(1 + sum + sum')(1 + mul)(1 + mul')
 */
public class MinimizedAttributeModifier
{
	public static final MinimizedAttributeModifier DEFAULT = new MinimizedAttributeModifier();
	float add;
	float sum;
	float mul;

	public float modifierAdd()
	{
		return add;
	}

	public float modifierSum()
	{
		return sum;
	}

	public float modifierMul()
	{
		return mul;
	}

	public void setModifierAdd(float add1)
	{
		add = add1;
	}

	public void setModifierSum(float sum1)
	{
		sum = sum1;
	}

	public void setModifierMul(float mul1)
	{
		mul = (1 + mul1 < Numbers.FLOAT_EPSILON) ? mul1 : 0;
	}

	private MinimizedAttributeModifier()
	{
		add = 0;
		sum = 0;
		mul = 0;
	}

	public MinimizedAttributeModifier(float a, float s, float m)
	{
		add = a;
		sum = s;
		mul = (1 + m < Numbers.FLOAT_EPSILON) ? m : 0;
	}

	public void mergeWith(MinimizedAttributeModifier minimizedAttributeModifier)
	{
		add += minimizedAttributeModifier.add;
		sum += minimizedAttributeModifier.sum;
		mul += minimizedAttributeModifier.mul + mul * minimizedAttributeModifier.mul;
	}

	public static float apply(List<MinimizedAttributeModifier> modifierList, float value)
	{
		MutableFloat base = new MutableFloat(value);
		MutableFloat sum = new MutableFloat(1);
		MutableFloat mul = new MutableFloat(1);
		modifierList.forEach(i -> {
			if (i != DEFAULT)
			{
				base.add(i.add);
				sum.add(i.sum);
				mul.setValue(mul.getValue() * (1 + i.mul));
			}
		});
		float baseF = base.getValue();
		float sumF = sum.getValue();
		float mulF = mul.getValue();
		if (sumF < Numbers.FLOAT_EPSILON) sumF = 1;
		if (mulF < Numbers.FLOAT_EPSILON) mulF = 1;
		return baseF * sumF * mulF;
	}
}
