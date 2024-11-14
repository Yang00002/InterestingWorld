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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.item.tool.canSweeping;
import org.yang.interestingworld.playerdatamanager.PlayerDataManager;
import org.yang.interestingworld.playerdatamanager.PlayerMixinAccessor;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;

import static org.yang.interestingworld.IWUtil.RuneAbility.getAbility;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements PlayerMixinAccessor
{
	@Shadow
	@Final
	public PlayerScreenHandler playerScreenHandler;
	@Shadow
	@Final
	protected static TrackedData<Byte> PLAYER_MODEL_PARTS;
	@Unique
	private IWAbstractRuneAbility WeaponAbility = IWRuneAbilitys.DEFAULT_ABILITY;

	@Shadow
	public abstract @NotNull ItemStack getWeaponStack();

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
			PlayerDataManager manager = IWUtil.EnergyTool.getPlayerData(instance);
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

	@Unique
	private final PlayerDataManager energyManager = new PlayerDataManager();

	@Override
	@Unique
	public PlayerDataManager getDataManager()
	{
		return energyManager;
	}

	@Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
	public void readPlayerEnergyData(NbtCompound nbt, CallbackInfo ci)
	{
		if (fakestack == null) fakestack = Items.DIAMOND_SWORD.getDefaultStack();
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
		if (!getWorld().isClient)
		{
			PlayerEntity player = (PlayerEntity) (Object) this;
			energyManager.update(player);
			ItemStack stack = getWeaponStack();
			if (!stack.isEmpty() && stack.getItem() instanceof EnergyToolItem)
			{
				var ability = getAbility(stack);
				if (!ability.canWork() && WeaponAbility.index != 0)
				{
					WeaponAbility.onLeave(player, energyManager);
					WeaponAbility = IWRuneAbilitys.DEFAULT_ABILITY;
				}
				else if (ability != WeaponAbility)
				{
					WeaponAbility.onLeave(player, energyManager);
					ability.onEnter(player, energyManager);
					WeaponAbility = ability;
				}
			}
			else if (WeaponAbility.index != 0)
			{
				WeaponAbility.onLeave(player, energyManager);
				WeaponAbility = IWRuneAbilitys.DEFAULT_ABILITY;
			}
			WeaponAbility.serverPlayerWeaponTick(player, energyManager, stack);
		}
	}
}
