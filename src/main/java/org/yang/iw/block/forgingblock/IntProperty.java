package org.yang.iw.block.forgingblock;

import net.minecraft.screen.Property;

import java.util.function.Consumer;

// Property 传输时实际上只保留了 16 位
public class IntProperty
{
	Property propertyH = Property.create();
	Property propertyL = Property.create();

	public int get()
	{
		return ((propertyH.get() & 0xFFFF) << 16) | (propertyL.get() & 0xFFFF);
	}

	public void set(int value)
	{
		propertyH.set(value >> 16);
		propertyL.set(value & 0XFFFF);
	}

	public void asProperties(Consumer<Property> consumer)
	{
		consumer.accept(propertyH);
		consumer.accept(propertyL);
	}
}
