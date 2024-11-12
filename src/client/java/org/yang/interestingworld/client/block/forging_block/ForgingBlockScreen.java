package org.yang.interestingworld.client.block.forging_block;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.block.forgingblock.ForgingBlockScreenHandler;

public class ForgingBlockScreen extends HandledScreen<ForgingBlockScreenHandler> implements ScreenHandlerListener
{
	private class ForgingBlockButton extends ClickableWidget
	{
		private static final Identifier ARROW_TEXTURE = Identifier.of(IWUtil.Base.MOD_ID,
				"textures/gui/container/forging_block/arrow.png");

		private static final Identifier HOVER_TEXTURE = Identifier.of(IWUtil.Base.MOD_ID,
				"textures/gui/container/forging_block/hover.png");

		public ForgingBlockButton(int x, int y, int width, int height, Text message)
		{
			super(x, y, width, height, message);
		}

		@Override
		protected boolean clicked(double mouseX, double mouseY)
		{
			if (this.active && this.visible && handler.canTakeOutput())
			{
				if (mouseX < x + 108)
				{
					return mouseX >= x + 94 && mouseY >= y + 36 && mouseY <= y + 40;
				}
				else
				{
					return mouseY >= mouseX + y - x - 79 && mouseY <= y + x + 155 - mouseX;
				}
			}
			return false;
		}

		@Override
		protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
		{
			if (handler.canTakeOutput())
			{
				if (clicked(mouseX, mouseY))
				{
					context.drawTexture(HOVER_TEXTURE, x + 94, y + 30, 0, 0, 24, 17, 24, 17);
				}
				else context.drawTexture(ARROW_TEXTURE, x + 94, y + 30, 0, 0, 24, 17, 24, 17);
			}
		}

		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder)
		{

		}

		@Override
		public void onClick(double mouseX, double mouseY)
		{
			if (client != null)
			{
				if (client.interactionManager != null)
				{
					client.interactionManager.clickButton(handler.syncId, 0);
				}
			}
		}
	}

	private static final Identifier TEXTURE = Identifier.of(IWUtil.Base.MOD_ID,
			"textures/gui/container/forging_block/main.png");

	private final PlayerEntity player;

	public ForgingBlockScreen(ForgingBlockScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		this.player = inventory.player;
		this.titleX = 60;
		this.backgroundHeight = 202;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	public void resize(MinecraftClient client, int width, int height)
	{
		this.init(client, width, height);
	}

	protected void drawForeground(DrawContext context, int mouseX, int mouseY)
	{
		super.drawForeground(context, mouseX, mouseY);
		/*
		int i = this.handler.getLevelCost();
		if (i > 0)
		{
			int j = 8453920;
			Object text;
			if (i >= 40 && !this.client.player.getAbilities().creativeMode)
			{
				text = TOO_EXPENSIVE_TEXT;
				j = 16736352;
			}
			else if (!((ForgingBlockScreenHandler) this.handler).getSlot(2).hasStack())
			{
				text = null;
			}
			else
			{
				text = Text.translatable("container.repair.cost", new Object[]{i});
				if (!((ForgingBlockScreenHandler) this.handler).getSlot(2).canTakeItems(this.player))
				{
					j = 16736352;
				}
			}

			if (text != null)
			{
				int k = this.backgroundWidth - 8 - this.textRenderer.getWidth((StringVisitable) text) - 2;
				int l = 69;
				context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
				context.drawTextWithShadow(this.textRenderer, (Text) text, k, 69, j);
			}
		}
*/
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void init()
	{
		super.init();
		this.handler.addListener(this);
		this.addDrawableChild(new ForgingBlockButton(this.x + 94, this.y + 30, 24, 17, Text.literal("button")));
	}

	@Override
	public void removed()
	{
		super.removed();
		this.handler.removeListener(this);
	}

	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 176, 202);
		/*
		context.drawGuiTexture(this.handler.getSlot(0).hasStack() ? TEXT_FIELD_TEXTURE
																  :
							   TEXT_FIELD_DISABLED_TEXTURE,
				this.x + 59, this.y + 20, 110, 16);

		 */
	}

	protected void drawInvalidRecipeArrow(DrawContext context, int x, int y)
	{
		//	if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) &&
		//		!this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack())
		//	{
		//		context.drawGuiTexture(ERROR_TEXTURE, x + 99, y + 45, 28, 21);
		//	}
	}

	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{

	}
}

