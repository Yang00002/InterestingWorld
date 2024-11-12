package org.yang.interestingworld.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.yang.interestingworld.item.tool.canSweeping;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;

import static org.yang.interestingworld.IWUtil.RuneAbility.getAbility;

@Mixin(PlayerEntity.class)
public abstract class ForSweeping
{
	@Unique
	private static ItemStack fakestack = null;

	@Redirect(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft" +
																									 "/entity/player" +
																									 "/PlayerEntity;" +
																									 "getStackInHand" +
																									 "(Lnet" +
																									 "/minecraft" +
																									 "/util/Hand;)" +
																									 "Lnet" +
																									 "/minecraft" +
																									 "/item" +
																									 "/ItemStack;"))
	private ItemStack injected(PlayerEntity instance, Hand hand)
	{
		ItemStack before = instance.getStackInHand(Hand.MAIN_HAND);
		Item it = before.getItem();
		if (canSweeping.class.isAssignableFrom(it.getClass()))
		{
			IWAbstractRuneAbility ab = getAbility(before);
			if (ab.canWork())
			{
				if (((canSweeping) it).canSweep(before))
				{
					ab.atSweeping(before, instance);
					if (fakestack == null) fakestack = new ItemStack(Items.DIAMOND_SWORD);
					return fakestack;
				}
				boolean res = ab.canSweeping(before, instance);
				if (!res) return before;
				ab.atSweeping(before, instance);
				if (fakestack == null) fakestack = new ItemStack(Items.DIAMOND_SWORD);
				return fakestack;
			}
			if (((canSweeping) it).canSweep(before))
			{
				if (fakestack == null) fakestack = new ItemStack(Items.DIAMOND_SWORD);
				return fakestack;
			}
		}
		return before;
	}
}
