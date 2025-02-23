package org.yang.iw.datagen.property;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.yang.iw.util.Base;

public class IWItemRenderingProperties
{
	public static void bootstrapSelect(Codecs.IdMapper<Identifier, SelectProperty.Type<?, ?>> ID_MAPPER)
	{
		ID_MAPPER.put(Identifier.of(Base.MOD_ID, "ability"), AbilitySelectProperty.TYPE);
	}

	public static void bootstrapNumeric(Codecs.IdMapper<Identifier, MapCodec<? extends NumericProperty>> ID_MAPPER)
	{
	}

	public static void bootstrapBoolean(Codecs.IdMapper<Identifier, MapCodec<? extends BooleanProperty>> ID_MAPPER)
	{
		ID_MAPPER.put(Identifier.of(Base.MOD_ID, "is_boosted"), IsBoostedBooleanProperty.CODEC);
	}
}
