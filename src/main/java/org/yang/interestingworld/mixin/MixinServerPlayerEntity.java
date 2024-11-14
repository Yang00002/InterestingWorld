package org.yang.interestingworld.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.playerdatamanager.PlayerMixinAccessor;
import org.yang.interestingworld.playerdatamanager.PlayerDataManager;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity
{
	@Inject(method = "playerTick", at = @At(value = "TAIL"))
	public void onTick(CallbackInfo ci)
	{
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		PlayerDataManager manager = ((PlayerMixinAccessor) this).getDataManager();
		manager.syncWithClient(player);
	}
}
