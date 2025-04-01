package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.yang.iw.api.util.MutableAttributeValueDetail;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.ModifyKnockbackFunction;

public class KnockbackBoost extends AbstractBoost
{

	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.MAINHAND, new ModifyKnockbackFunction()
		{
			@Override
			public void modifyKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
										MutableAttributeValueDetail baseKnockback)
			{
				baseKnockback.base += level;
			}
		}).build();
	}

	KnockbackBoost(Identifier identifier)
	{
		super(identifier);
	}

	public short maxTableLevel()
	{
		return 3;
	}

	public short maxRandomLevel()
	{
		return 5;
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return (135 + 15 * level) * (int) Math.sqrt(level);
	}
}