package org.yang.iw.client.mixin.mixin;

import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.datagen.property.IWItemRenderingProperties;

@Mixin(SelectProperties.class)
public class MixinSelectProperties
{
	@Shadow
	@Final
	public static Codecs.IdMapper<Identifier, SelectProperty.Type<?, ?>> ID_MAPPER;

	@Inject(method = "bootstrap", at = @At("TAIL"))
	private static void injectAtTail(CallbackInfo ci)
	{
		IWItemRenderingProperties.bootstrapSelect(ID_MAPPER);
	}

}
