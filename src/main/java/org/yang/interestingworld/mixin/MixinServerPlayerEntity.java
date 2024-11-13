package org.yang.interestingworld.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.playerenergymanager.PlayerEnergyAccessor;
import org.yang.interestingworld.playerenergymanager.PlayerEnergyManager;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity
{
	@Inject(method = "playerTick", at = @At(value = "TAIL"))
	public void onTick(CallbackInfo ci)
	{
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		PlayerEnergyManager manager = ((PlayerEnergyAccessor) this).getEnergyManager();
		manager.syncWithClient(player);
	}
}
