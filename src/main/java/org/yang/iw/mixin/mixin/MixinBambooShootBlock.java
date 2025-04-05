package org.yang.iw.mixin.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BambooShootBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.item.IWItemTags;

@Mixin(BambooShootBlock.class)
public class MixinBambooShootBlock extends Block
{
	public MixinBambooShootBlock(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	@Inject(method = "calcBlockBreakingDelta", at = @At(value = "HEAD"), cancellable = true)
	private void addSwordBreaking(BlockState state, PlayerEntity player, BlockView world, BlockPos pos,
								  CallbackInfoReturnable<Float> cir)
	{
		Item item = player.getMainHandStack().getItem();
		if (item.getRegistryEntry().isIn(IWItemTags.SWORD)) cir.setReturnValue(1.0f);
		else cir.setReturnValue(super.calcBlockBreakingDelta(state, player, world, pos));
	}
}
