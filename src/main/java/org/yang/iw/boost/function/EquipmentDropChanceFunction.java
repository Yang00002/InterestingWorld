package org.yang.iw.boost.function;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;

public abstract class EquipmentDropChanceFunction
{
	public abstract void getEquipmentDropChance(ServerWorld world, LivingEntity attacker,
												DamageSource damageSource, MutableFloat baseEquipmentDropChance);
}
