package org.yang.iw.client.mixin.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.datagen.property.IWItemRenderingProperties;

@Mixin(NumericProperties.class)
public class MixinNumericProperties
{
	@Shadow
	@Final
	public static Codecs.IdMapper<Identifier, MapCodec<? extends NumericProperty>> ID_MAPPER;

	@Inject(method = "bootstrap", at = @At("TAIL"))
	private static void injectAtTail(CallbackInfo ci)
	{
		IWItemRenderingProperties.bootstrapNumeric(ID_MAPPER);
	}
}
