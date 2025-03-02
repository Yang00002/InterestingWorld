package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.TargetDamagedFunction;
import org.yang.iw.effect.IWEffects;

public class FireAspectBoost extends AbstractBoost
{

	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.MAINHAND, new TargetDamagedFunction()
		{

			@Override
			public void onTargetDamaged(ItemStack stack, ServerWorld world, LivingEntity target,
										DamageSource damageSource)
			{
				target.setOnFireFor(2 + (level << 1));
				int burningLevel = (level - 1) << 1;
				if (burningLevel > 0) target.addStatusEffect(
						new StatusEffectInstance(IWEffects.BURNING, 10 * (2 + (level << 1)), burningLevel - 1),
						damageSource.getSource());
			}
		}).build();
	}

	FireAspectBoost(Identifier identifier)
	{
		super(identifier);
	}

	public short maxTableLevel()
	{
		return 5;
	}

	public short maxRandomLevel()
	{
		return 10;
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return (135 + 15 * level) * (int) Math.sqrt(level);
	}
}