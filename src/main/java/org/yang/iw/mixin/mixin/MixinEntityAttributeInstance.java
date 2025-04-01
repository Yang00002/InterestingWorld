package org.yang.iw.mixin.mixin;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.api.util.AttributeValueDetail;
import org.yang.iw.mixin.mixin_interface.InterfaceEntityAttributeInstance;

import java.util.Collection;

@Mixin(EntityAttributeInstance.class)
public abstract class MixinEntityAttributeInstance implements InterfaceEntityAttributeInstance
{
	@Shadow
	protected abstract Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation);

	@Shadow
	public abstract double getBaseValue();

	@Shadow
	@Final
	private RegistryEntry<EntityAttribute> type;
	@Shadow
	private boolean dirty;
	@Shadow
	private double value;

	@Shadow
	protected abstract double computeValue();

	@Unique
	private AttributeValueDetail valueDetail = AttributeValueDetail.DEFAULT;

	@Inject(method = "computeValue", at = @At("HEAD"), cancellable = true)
	private void modifyComputeValue(CallbackInfoReturnable<Double> cir)
	{
		double d = getBaseValue();
		double m1 = 0;
		for (EntityAttributeModifier entityAttributeModifier : getModifiersByOperation(
				EntityAttributeModifier.Operation.ADD_VALUE))
		{
			m1 += entityAttributeModifier.value();
		}
		double m2 = 1.0;
		for (EntityAttributeModifier entityAttributeModifier : getModifiersByOperation(
				EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE))
		{
			m2 += entityAttributeModifier.value();
		}
		double m3 = 1.0;
		for (EntityAttributeModifier entityAttributeModifier : getModifiersByOperation(
				EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
		{
			m3 *= 1.0 + entityAttributeModifier.value();
		}
		valueDetail = new AttributeValueDetail((float) (d + m1), (float) m2, (float) m3);
		cir.setReturnValue(type.value().clamp((d + m1) * m2 * m3));
	}

	public AttributeValueDetail interestingWorld$getValueDetail()
	{
		if (dirty)
		{
			value = computeValue();
			dirty = false;
		}
		return valueDetail;
	}
}
