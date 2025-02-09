package org.yang.iw.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
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
import org.yang.iw.util.IWUtil;
import org.yang.iw.mixin.mixin_interface.InterfaceServerPlayerEntity;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.util.toolflag.EnergyToolDataFlag;

import static org.yang.iw.util.IWRuneAbilityUtil.getAbility;


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
		if (instance instanceof ServerPlayerEntity && EnergyToolDataFlag.getFromItemStack(before).canSweep())
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
			this.getIWServerPlayerData().sweeping = true;
			AbstractRuneAbility ab = getAbility(itemStack);
			if (ab.canWork()) ab.atSweeping(itemStack, serverPlayer);
		}
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;" +
																   "onAttacking(Lnet/minecraft/entity/Entity;)V"))
	public void setHitParticle(Entity target, CallbackInfo ci, @Local(ordinal = 1) float g)
	{
		if (g <= 0.0F)
		{
			var at = getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
			if (at != null && at.getModifiers().stream()
					.anyMatch(IWUtil.Components.MutableAttributeContainer::modifierAddByEnchantment))
			{
				addEnchantedHitParticles(target);
			}
		}
	}
}
