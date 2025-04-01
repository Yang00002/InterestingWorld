package org.yang.iw.boost.function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.yang.iw.api.util.MutableAttributeValueDetail;

public abstract class ModifyDamageFunction
{
	public abstract void modifyDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
									  MutableAttributeValueDetail baseDamage);
}
