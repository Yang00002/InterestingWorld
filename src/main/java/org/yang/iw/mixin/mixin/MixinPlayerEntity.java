package org.yang.iw.mixin.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.mixin.mixin_interface.InterfaceServerPlayerEntity;


@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements InterfaceServerPlayerEntity
{
	@Shadow
	public abstract @NotNull ItemStack getWeaponStack();

	@Shadow
	public abstract void addEnchantedHitParticles(Entity target);

	@Shadow
	public abstract void tick();

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
	private ItemStack handleSweeping(PlayerEntity instance, Hand hand)
	{
		ItemStack before = instance.getWeaponStack();
		if (instance instanceof ServerPlayerEntity && EnergyToolDataFlag.fromItemStack(before).canSweep())
			return Items.DIAMOND_SWORD.getDefaultStack();
		return before;
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;" +
																   "getNonSpectatingEntities(Ljava/lang/Class;" +
																   "Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
	public void handleSweepingCondition(Entity target, CallbackInfo ci)
	{
		PlayerEntity player = (PlayerEntity) (Object) this;
		if (player instanceof ServerPlayerEntity serverPlayer)
		{
			ItemStack itemStack = this.getWeaponStack();
			this.interestingWorld$getIWServerPlayerData().sweeping = true;
			AbstractAbility ab = itemStack.interestingWorld$getAbility().ability();
			if (ab.canWork()) ab.atSweeping(itemStack, serverPlayer);
		}
	}
}
