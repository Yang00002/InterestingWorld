package org.yang.iw.mixin.mixin_interface;

import net.minecraft.util.Identifier;

public interface InterfaceModel
{
	default Identifier getParent()
	{
		return null;
	}
}
