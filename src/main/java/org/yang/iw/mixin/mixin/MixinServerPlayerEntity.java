package org.yang.iw.mixin.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.mixin.mixin_interface.InterfaceServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements InterfaceServerPlayerEntity
{
	@Shadow
	public abstract ServerWorld getServerWorld();

	@Unique
	private final IWServerPlayerData dataManager = new IWServerPlayerData((ServerPlayerEntity) (Object) this);

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile)
	{
		super(world, pos, yaw, gameProfile);
	}

	@Inject(method = "playerTick", at = @At(value = "TAIL"))
	public void onTick(CallbackInfo ci)
	{
		dataManager.tick((ServerPlayerEntity) (Object) this);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
	public void readPlayerEnergyData(NbtCompound nbt, CallbackInfo ci)
	{
		dataManager.readNbt(nbt);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
	public void writePlayerEnergyData(NbtCompound nbt, CallbackInfo ci)
	{
		dataManager.writeNbt(nbt);
	}

	@Inject(method = "copyFrom", at = @At(value = "TAIL"))
	public void copyPlayerEnergyData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci)
	{
		boolean keepInventory =
				getServerWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator();
		dataManager.copyPlayerData(oldPlayer.interestingWorld$getIWServerPlayerData(), alive, keepInventory);
	}

	@Override
	@Unique
	public IWServerPlayerData interestingWorld$getIWServerPlayerData()
	{
		return dataManager;
	}

}
