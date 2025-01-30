package org.yang.iw.mixin.mixin;

import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.yang.iw.mixin.mixin_interface.InterfaceStatusEffect;

@Mixin(StatusEffect.class)
public class MixinStatusEffect implements InterfaceStatusEffect
{

}
