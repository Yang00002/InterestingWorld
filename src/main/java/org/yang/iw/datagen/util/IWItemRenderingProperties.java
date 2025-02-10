package org.yang.iw.datagen.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.yang.iw.util.Base;

public class IWItemRenderingProperties
{
	public static void bootstrapNumeric(Codecs.IdMapper<Identifier, MapCodec<? extends NumericProperty>> ID_MAPPER)
	{
		ID_MAPPER.put(Identifier.of(Base.MOD_ID, "ability_index"), AbilityToolIndexNumericProperty.CODEC);
	}

	public static void bootstrapBoolean(Codecs.IdMapper<Identifier, MapCodec<? extends BooleanProperty>> ID_MAPPER)
	{
		ID_MAPPER.put(Identifier.of(Base.MOD_ID, "heart_enchant"), HeartEnchantedBooleanProperty.CODEC);
	}
}
