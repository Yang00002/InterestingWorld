package org.yang.interestingworld.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.item.tool.canSweeping;
import org.yang.interestingworld.playerenergymanager.PlayerEnergyAccessor;
import org.yang.interestingworld.playerenergymanager.PlayerEnergyManager;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;

import static org.yang.interestingworld.IWUtil.RuneAbility.getAbility;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements PlayerEnergyAccessor
{
	@Shadow
	@Final
	public PlayerScreenHandler playerScreenHandler;
	@Shadow
	@Final
	protected static TrackedData<Byte> PLAYER_MODEL_PARTS;
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

	@Unique
	private final PlayerEnergyManager energyManager = new PlayerEnergyManager();

	@Override
	@Unique
	public float extractEnergy(float amount)
	{
		return energyManager.tryTransferEnergy(amount);
	}

	@Override
	@Unique
	public PlayerEnergyManager getEnergyManager()
	{
		return energyManager;
	}

	@Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
	public void readPlayerEnergyData(NbtCompound nbt, CallbackInfo ci)
	{
		energyManager.readNbt(nbt);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
	public void writePlayerEnergyData(NbtCompound nbt, CallbackInfo ci)
	{
		energyManager.writeNbt(nbt);
	}

	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void onTick(CallbackInfo ci)
	{
		if (!getWorld().isClient) energyManager.update((PlayerEntity) (Object) this);
	}
}
