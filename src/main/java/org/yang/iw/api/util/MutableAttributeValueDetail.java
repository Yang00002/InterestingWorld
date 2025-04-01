package org.yang.iw.api.util;

public class MutableAttributeValueDetail
{
	public float base;
	public float sum;
	public float mul;

	public void clear()
	{
		base = 0;
		sum = 1.0f;
		mul = 1.0f;
	}

	public MutableAttributeValueDetail()
	{
		base = 0;
		sum = 1.0f;
		mul = 1.0f;
	}

	public MutableAttributeValueDetail(float value)
	{
		base = value;
		sum = 1.0f;
		mul = 1.0f;
	}

	public MutableAttributeValueDetail(AttributeValueDetail valueDetail)
	{
		base = valueDetail.base();
		sum = valueDetail.sum();
		mul = valueDetail.mul();
	}

	@Override
	public String toString()
	{
		return "MutableAttributeValueDetail{" + "base=" + base + ", sum=" + sum + ", mul=" + mul + '}';
	}

	public float value()
	{
		return base * sum * mul;
	}

	public AttributeValueDetail valueDetail()
	{
		return new AttributeValueDetail(base, sum, mul);
	}
}
