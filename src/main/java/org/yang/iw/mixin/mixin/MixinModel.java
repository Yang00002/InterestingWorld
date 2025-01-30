package org.yang.iw.mixin.mixin;

import net.minecraft.data.client.Model;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.yang.iw.mixin.mixin_interface.InterfaceModel;

import java.util.Optional;

@Mixin(Model.class)
public class MixinModel implements InterfaceModel
{
	@Shadow
	@Final
	private Optional<Identifier> parent;

	public Identifier getParent()
	{
		return parent.orElse(null);
	}
}
