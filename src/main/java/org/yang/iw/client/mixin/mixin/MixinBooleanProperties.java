package org.yang.iw.client.mixin.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.datagen.property.IWItemRenderingProperties;

@Mixin(BooleanProperties.class)
public class MixinBooleanProperties
{
	@Shadow
	@Final
	public static Codecs.IdMapper<Identifier, MapCodec<? extends BooleanProperty>> ID_MAPPER;

	@Inject(method = "bootstrap", at = @At("TAIL"))
	private static void injectAtTail(CallbackInfo ci)
	{
		IWItemRenderingProperties.bootstrapBoolean(ID_MAPPER);
	}
}
