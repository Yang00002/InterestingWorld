package org.yang.iw.block.forgingblock;

import net.minecraft.screen.Property;

public class ForgingBlockStateProperty
{
	Property property = Property.create();

	private static final ForgingBlockState[] values = ForgingBlockState.values();

	public ForgingBlockState get()
	{
		return values[property.get()];
	}

	public void set(ForgingBlockState value)
	{
		property.set(value.ordinal());
	}

	public Property getProperty()
	{
		return property;
	}
}
