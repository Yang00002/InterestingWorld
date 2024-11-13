package org.yang.interestingworld.mixin;

import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements Attackable
{
    @Shadow
    protected float lastDamageTaken;
    @Shadow
    public int maxHurtTime;
    @Unique
    private int maxHurtTimeCache = 0;
    @Unique
    private int hurtTimeCache = 0;
    @Unique
    private int timeUntilRegenCache = 0;
    @Unique
    private float lastDamageTakenCache = 0;

    public MixinLivingEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/entity/LimbAnimator;setSpeed(F)V"))
    private void injected(CallbackInfoReturnable<Boolean> cir)
    {
        maxHurtTimeCache = maxHurtTime;
        hurtTimeCache = ((LivingEntity) (Object) this).hurtTime;
        timeUntilRegenCache = timeUntilRegen;
        lastDamageTakenCache = lastDamageTaken;
    }

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"))
    private void injected2(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        if (source.isIn(DamageTypeTags.BYPASSES_COOLDOWN))
        {
            maxHurtTime = maxHurtTimeCache;
            ((LivingEntity) (Object) this).hurtTime = hurtTimeCache;
            timeUntilRegen = timeUntilRegenCache;
            lastDamageTaken = lastDamageTakenCache;
        }
    }

}
