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
	// x,y 绘画左上端点坐标, u,v 缩放过的材质内开始坐标, width height 绘画窗口大小, texture width texture height 材质大小, 代表缩放
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
	private static final Identifier EXPERIENCE = Identifier.of(IWUtil.Base.MOD_ID,
			"textures/gui/container/forging_block/experience.png");

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
		int i = this.handler.getExperienceCost();
		if (i > 0 && this.client != null && this.client.player != null)
		{
			int t = this.client.player.totalExperience;
			int j = this.client.player.totalExperience >= i ? 8453920 : 16736352;
			Text text = Text.translatable("forgingblock.repair.cost", i, t);
			int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 20;
			context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
			context.drawTextWithShadow(this.textRenderer, text, k, 69, j);
			int size = Math.min(i / 1500, 10);
			int idx = size % 4 * 12;
			int idy = size / 4 * 12;
			context.drawTexture(EXPERIENCE, this.backgroundWidth - 24, 67, idx, idy, 12, 12, 48, 48);
		}
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

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
	{
		context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 176, 202);
	}

	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{

	}
}

