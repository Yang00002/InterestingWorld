package org.yang.interestingworld.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.canSweeping;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataAccessor;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataManager;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;

import static org.yang.interestingworld.IWUtil.RuneAbility.getAbility;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements ServerPlayerDataAccessor
{
	@Unique
	private static ItemStack fakestack = null;

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

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
		ItemStack before = instance.getWeaponStack();
		Item it = before.getItem();
		if (instance instanceof ServerPlayerEntity player && canSweeping.class.isAssignableFrom(it.getClass()))
		{
			ServerPlayerDataManager manager = IWUtil.EnergyTool.getPlayerData(player);
			IWAbstractRuneAbility ab = getAbility(before);
			if (ab.canWork())
			{
				if (((canSweeping) it).canSweep(before))
				{
					ab.atSweeping(before, instance);
					manager.sweeping = true;
					return fakestack;
				}
				boolean res = ab.canSweeping(before, instance, manager);
				if (!res) return before;
				ab.atSweeping(before, instance);
				manager.sweeping = true;
				return fakestack;
			}
			if (((canSweeping) it).canSweep(before))
			{
				manager.sweeping = true;
				return fakestack;
			}
			manager.sweeping = false;
		}
		return before;
	}

	@Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
	public void readPlayerEnergyData(NbtCompound nbt, CallbackInfo ci)
	{
		if (fakestack == null) fakestack = Items.DIAMOND_SWORD.getDefaultStack();
	}
}
