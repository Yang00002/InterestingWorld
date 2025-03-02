package org.yang.iw.boost.function;

import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class ItemDamageFunction
{
	public abstract void getItemDamage(ServerWorld world, MutableInt preDamage);
}
