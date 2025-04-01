package org.yang.iw.client.mixin.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.item.IWItems;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen
{


	protected MixinHandledScreen(Text title)
	{
		super(title);
	}

	@Shadow
	@Nullable
	protected Slot focusedSlot;

	@Inject(method = "isItemTooltipSticky", at = @At(value = "RETURN"), cancellable = true)
	private void packetSticky(ItemStack item, CallbackInfoReturnable<Boolean> cir)
	{
		cir.setReturnValue(item.getItem() == IWItems.MATERIAL_PACKET || cir.getReturnValue());
	}

	@Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
	private void handleMaterialPacket(double mouseX, double mouseY, double horizontalAmount, double verticalAmount,
									  CallbackInfoReturnable<Boolean> cir)
	{
		if (this.focusedSlot != null && this.focusedSlot.hasStack())
		{
			ItemStack stack = this.focusedSlot.getStack();
			if (!stack.isEmpty())
			{
				if (stack.getItem().mouseScroll(horizontalAmount, verticalAmount, (HandledScreen<?>) (Object) this,
						this.focusedSlot, client)) cir.setReturnValue(true);
			}
		}
	}
}