package org.yang.iw.block.forgingblock;

import net.minecraft.screen.Property;

import java.util.function.Consumer;

public class FloatProperty
{
	Property propertyH = Property.create();
	Property propertyL = Property.create();

	public float get()
	{
		return Float.intBitsToFloat(((propertyH.get() & 0xFFFF) << 16) | (propertyL.get() & 0xFFFF));
	}

	public void set(float value)
	{
		int i = Float.floatToIntBits(value);
		propertyH.set(i >> 16);
		propertyL.set(i & 0XFFFF);
	}

	public void asProperties(Consumer<Property> consumer)
	{
		consumer.accept(propertyH);
		consumer.accept(propertyL);
	}
}
