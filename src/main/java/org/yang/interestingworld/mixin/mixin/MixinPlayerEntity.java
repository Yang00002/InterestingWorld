package org.yang.interestingworld.mixin.mixin;

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
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.entity.player.IWServerPlayerData;
import org.yang.interestingworld.mixin.mixin_interface.InterfaceServerPlayerEntity;
import org.yang.interestingworld.rune_ability.AbstractRuneAbility;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import static org.yang.interestingworld.util.IWRuneAbilityUtil.getAbility;


@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements InterfaceServerPlayerEntity
{
	@Shadow
	public abstract @NotNull ItemStack getWeaponStack();

	@Shadow
	public abstract void addEnchantedHitParticles(Entity target);

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
		if (instance instanceof ServerPlayerEntity player && before.contains(IWComponents.DATA_FLAGS))
		{
			IWServerPlayerData manager = player.getIWServerPlayerData();
			AbstractRuneAbility ab = getAbility(before);
			if (EnergyToolDataFlag.getFromItemStack(before).canSweep())
			{
				if (ab.canWork()) ab.atSweeping(before, instance);
				manager.sweeping = true;
				return Items.DIAMOND_SWORD.getDefaultStack();
			}
			manager.sweeping = false;
		}
		return before;
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;" +
																   "onAttacking(Lnet/minecraft/entity/Entity;)V"))
	public void setHitParticle(Entity target, CallbackInfo ci, @Local(ordinal = 1) float g)
	{
		if (g <= 0.0F)
		{
			var at = getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
			if (at != null && at.getModifiers().stream()
					.anyMatch(IWUtil.Components.MutableAttributeContainer::modifierAddByEnchantment))
			{
				addEnchantedHitParticles(target);
			}
		}
	}
}
