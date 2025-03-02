package org.yang.iw.mixin.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.boost.function.LeveledSignalFunction;

import java.util.LinkedList;
import java.util.List;

@Mixin(ExperienceOrbEntity.class)
public class MixinExperienceOrbEntity
{
	@Inject(method = "repairPlayerGears", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
	public void addMending(ServerPlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir)
	{
		List<Pair<ItemStack, Integer>> list = new LinkedList<>();
		for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES)
		{
			ItemStack itemStack = player.getEquippedStack(equipmentSlot);
			if (!itemStack.isEmpty() && itemStack.getDamage() > 0)
			{
				int level = itemStack.interestingWorld$getBoosts()
						.signalValue(LeveledSignalFunction.Signal.MENDING, itemStack, equipmentSlot);
				if (level > 0) list.add(new Pair<>(itemStack, level));
			}
		}
		int size = list.size();
		if (size == 0) return;
		while (amount > size)
		{
			var per = amount / size;
			var iterator = list.iterator();
			while (iterator.hasNext())
			{
				var n = iterator.next();
				var itemStack = n.getLeft();
				var level = n.getRight();
				int damage = itemStack.getDamage();
				int repair = Math.min(damage, level * per);
				itemStack.setDamage(damage - repair);
				int cost = (repair + level - 1) / level;
				amount -= cost;
				if (repair >= damage)
				{
					iterator.remove();
					size--;
					if (size <= 0) break;
				}
			}
		}
		var random = player.getRandom();
		while (size > 0 && amount > 0)
		{
			int rd = random.nextInt(size);
			var iterator = list.iterator();
			while (iterator.hasNext())
			{
				var n = iterator.next();
				if (rd > 0)
				{
					rd--;
					continue;
				}
				var itemStack = n.getLeft();
				var level = n.getRight();
				int damage = itemStack.getDamage();
				int repair = Math.min(damage, level);
				itemStack.setDamage(damage - repair);
				amount--;
				if (repair >= damage)
				{
					iterator.remove();
					size--;
				}
				break;
			}
		}
		cir.setReturnValue(amount);
	}
}
