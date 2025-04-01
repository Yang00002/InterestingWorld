package org.yang.iw.boost.function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.yang.iw.api.util.MutableAttributeValueDetail;

public abstract class ModifyKnockbackFunction
{
	public abstract void modifyKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
										 MutableAttributeValueDetail baseKnockback);
}
