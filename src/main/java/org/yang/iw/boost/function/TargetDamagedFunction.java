package org.yang.iw.boost.function;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public abstract class TargetDamagedFunction
{
	public abstract void onTargetDamaged(ItemStack stack, ServerWorld world, LivingEntity target, DamageSource damageSource);
}
