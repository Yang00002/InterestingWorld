package org.yang.iw.client.block.forging_block;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.yang.iw.IWResources;
import org.yang.iw.block.forgingblock.ForgingBlockScreenHandler;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;
import org.yang.iw.util.IWEnchantmentUtil;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

import static org.yang.iw.util.IWEnchantmentUtil.getWorldLevelOfXpCost;

public class ForgingBlockScreen extends HandledScreen<ForgingBlockScreenHandler> implements ScreenHandlerListener
{
	// x,y 绘画左上端点坐标, u,v 缩放过的材质内开始坐标, width height 绘画窗口大小, texture width texture height 材质大小, 代表缩放

	private static final Identifier TEXTURE = Identifier.of(Base.MOD_ID,
			"textures/gui/container/forging_block/main.png");
	private static final Identifier EXPERIENCE = Identifier.of(Base.MOD_ID,
			"textures/gui/container/forging_block/experience.png");

	public ForgingBlockScreen(ForgingBlockScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		this.titleX = 60;
		this.titleY--;
		this.backgroundHeight = 202;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	public void resize(MinecraftClient client, int width, int height)
	{
		this.init(client, width, height);
	}

	private void drawTitle(DrawContext context, Text title)
	{
		context.drawText(this.textRenderer, title, this.backgroundWidth - 8 - this.textRenderer.getWidth(title),
				this.titleY, 4210752, false);
	}

	private class TipDrawer
	{
		private final DrawContext context;
		private final Queue<Text> texts;
		private final Queue<Integer> spacing;
		private final int lSpace;
		private int maxLength;

		TipDrawer(DrawContext drawContext, int lspace, int rspace)
		{
			context = drawContext;
			texts = new LinkedList<>();
			spacing = new LinkedList<>();
			lSpace = lspace;
			maxLength = lSpace + rspace;
		}

		void addXpTexture(int scale, int spaceLeft)
		{
			if (!texts.isEmpty()) maxLength += 10 + spaceLeft;
			else maxLength += 10;
			texts.add(null);
			scale = Math.clamp(scale, 0, 10);
			spacing.add((spaceLeft << 4) | scale);
		}

		void addText(Text text, int spaceLeft, boolean isok)
		{
			if (text != null)
			{
				int len = textRenderer.getWidth(text);
				if (!texts.isEmpty()) maxLength += len + spaceLeft;
				else maxLength += len;
				texts.add(text);
				spacing.add((spaceLeft << 1) | (isok ? 1 : 0));
			}
		}

		void draw()
		{
			context.fill(backgroundWidth - 8 - maxLength, 67, backgroundWidth - 8, 79, 1325400064);
			int begin = backgroundWidth - 8 - maxLength + lSpace;
			int originBegin = begin;
			while (!texts.isEmpty())
			{
				Text text = texts.poll();
				int spaceTag = spacing.isEmpty() ? 0 : spacing.poll();
				if (text == null)
				{
					int space = (begin > originBegin) ? spaceTag >> 4 : 0;
					int size = spaceTag & 0b01111;
					begin += space;
					int idx = size % 4 * 10;
					int idy = size / 4 * 10;
					context.drawTexture(RenderLayer::getGuiTextured, EXPERIENCE, begin, 68, idx, idy, 10, 10, 40, 40);
					begin += 10;
				}
				else
				{
					int space = (begin > originBegin) ? spaceTag >> 1 : 0;
					int color = ((spaceTag & 1) > 0) ? 8453920 : 16736352;
					begin += space;
					context.drawTextWithShadow(textRenderer, text, begin, 69, color);
					begin += textRenderer.getWidth(text);
				}
			}
		}
	}

	protected void drawForeground(DrawContext context, int mouseX, int mouseY)
	{
		context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX,
				this.playerInventoryTitleY, 4210752, false);
		int state = this.handler.getGlobalState();
		switch (state)
		{
			case 0 -> drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ALL_N));
			case 1 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_PREENCHANT_N));
				Text t = handler.getFirstEnchantOut();
				if (t != null)
				{
					TipDrawer drawer = new TipDrawer(context, 2, 2);
					drawer.addText(t, 4, true);
					drawer.draw();
				}
			}
			case 2 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_PREENCHANT_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_PREENCHANT_N), 4, false);
				drawer.draw();
			}
			case 3 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ENCHANT_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				if (!handler.isWorldLevelEnough())
				{
					drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_CANNOT_USE_BECAUSE_LEVEL_N), 4,
							false);
				}
				else
				{
					int xpCost = this.handler.getExperienceCost();
					if (this.client != null && this.client.player != null)
					{
						var player = this.client.player;
						int xpHave = IWEnchantmentUtil.getExperienceFromLevel(player.experienceLevel,
								player.experienceProgress);
						drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ENCHANT_COST_L_N), 4,
								xpHave >= xpCost);
						drawer.addXpTexture(getWorldLevelOfXpCost(xpCost), 0);
						drawer.addText(
								Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ENCHANT_COST_R_N, xpCost, xpHave),
								4, xpHave >= xpCost);
					}
				}
				drawer.draw();
			}
			case 4 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ENCHANT_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ENCHANT_NO_USEFUL_N), 4, false);
				drawer.draw();
			}
			case 5 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ENCHANT_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ENCHANT_PUT_TOOL_N), 4, false);
				drawer.draw();
			}
			case 6 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_REPAIR_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_REPAIR_NEED_INGREDIENT_N), 4,
						false);
				drawer.draw();
			}
			case 7 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_REPAIR_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				int xpCost = this.handler.getExperienceCost();
				if (this.client != null && this.client.player != null)
				{
					var player = this.client.player;
					int xpHave = IWEnchantmentUtil.getExperienceFromLevel(player.experienceLevel,
							player.experienceProgress);
					drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_REPAIR_COST_L_N), 4,
							xpHave >= xpCost);
					drawer.addXpTexture(IWEnchantmentUtil.getWorldLevelOfXpCost(xpCost), 0);
					drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_REPAIR_COST_R_N, xpCost, xpHave,
							handler.getRepairCount()), 4, xpHave >= xpCost);
				}
				drawer.draw();
			}
			case 8 -> drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_ADD_N));
			case 9 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_ADD_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_ADD_INCOMPATIBLE_N), 4,
						false);
				drawer.draw();
			}
			case 10 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_ADD_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_ADD_ALREADY_HAVE_N), 4,
						false);
				drawer.draw();
			}
			case 11 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_ADD_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_ADD_PUT_TOOL_N), 4, false);
				drawer.draw();
			}
			case 12 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_REMOVE_N));
				int preBeginX = this.backgroundWidth - 10;
				int count = handler.getRepairCount();
				Text text = Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_REMOVE_TOOL_BREAK_N, count);
				int len = this.textRenderer.getWidth(text);
				context.fill(preBeginX - len - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
				context.drawTextWithShadow(this.textRenderer, text, preBeginX - len, 69,
						MathHelper.hsvToRgb((100 - count) / 300.0f, 1.0F, 1.0F) | 0x0FF000000);
			}
			case 13 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_REMOVE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_REMOVE_NO_ABILITY_N), 4,
						false);
				drawer.draw();
			}
			case 14 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_REMOVE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_REMOVE_PUT_TOOL_N), 4,
						false);
				drawer.draw();
			}
			case 15 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_PREENCHANT_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ENCHANT_NO_USEFUL_N), 4, false);
				drawer.draw();
			}
			case 16 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_UPGRADE_N));
				if (!handler.isWorldLevelEnough())
				{
					TipDrawer drawer = new TipDrawer(context, 2, 2);
					drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_CANNOT_USE_BECAUSE_LEVEL_N), 4,
							false);
					drawer.draw();
				}
			}
			case 17 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_UPGRADE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_UPGRADE_LACK_INGREDIENT_N), 4,
						false);
				drawer.draw();
			}
			case 18 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_UPGRADE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_UPGRADE_INCOMPATIBLE_N), 4, false);
				drawer.draw();
			}
			case 19 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_UPGRADE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_UPGRADE_REPEAT_N), 4, false);
				drawer.draw();
			}
			case 20 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_UPGRADE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_UPGRADE_PUT_TOOL_N), 4, false);
				drawer.draw();
			}
			case 21 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_REMOVE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_REMOVE_INCOMPATIBLE_N), 4,
						false);
				drawer.draw();
			}
			case 22 ->
			{
				drawTitle(context, Text.translatable(TranslationPool.FORGING_BLOCK_TITLE_ABILITY_REMOVE_N));
				TipDrawer drawer = new TipDrawer(context, 2, 2);
				drawer.addText(Text.translatable(TranslationPool.FORGING_BLOCK_TIP_ABILITY_REMOVE_LEVEL_N), 4, false);
				drawer.draw();
			}
		}
	}

	@Override
	protected void drawMouseoverTooltip(DrawContext context, int x, int y)
	{
		if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack())
		{
			ItemStack itemStack = this.focusedSlot.getStack();
			if (this.getScreenHandler().isOutputInventory(this.focusedSlot.inventory) && handler.getGlobalState() == 1)
				return;
			context.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x,
					y);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
		switch (handler.getGlobalState())
		{
			case 1, 15 -> this.drawIngredientOverlay(context, stack -> {
				var it = stack.getItem();
				return it == Items.LAPIS_LAZULI || it == Items.AMETHYST_SHARD;
			});
			case 7 ->
			{
				var ig = handler.getRepairIngredient();
				if (ig != null) this.drawIngredientOverlay(context, ig::matches);
			}
			case 12 -> this.drawIngredientOverlay(context,
					stack -> IWResources.RuneItemValue.RuneItemValue.containsKey(stack.getItem()));
			case 16, 17, 18, 19, 20 ->
			{
				var need = handler.getUpgradeNeed();
				if (need != null) this.drawIngredientOverlay(context, stack -> {
					var it = stack.getItem();
					return need.containsKey(it);
				});
			}
		}
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	public static void drawSlotOverlay(DrawContext context, int x, int y)
	{
		context.fill(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 16, 1073807104);
	}


	public void drawIngredientOverlay(DrawContext context, Predicate<ItemStack> overLayCond)
	{
		int i = this.x;
		int j = this.y;
		RenderSystem.disableDepthTest();
		context.getMatrices().push();
		context.getMatrices().translate((float) i, (float) j, 0.0F);
		for (int k = 3; k < 12; k++)
		{
			Slot slot = this.handler.slots.get(k);
			if (overLayCond.test(slot.getStack()))
			{
				drawSlotOverlay(context, slot.x, slot.y);
			}
		}
		context.getMatrices().pop();
		RenderSystem.enableDepthTest();
	}

	@Override
	protected void init()
	{
		super.init();
		this.handler.addListener(this);
		//this.addDrawableChild(new ForgingBlockButton(this.x + 94, this.y + 30, 24, 17, Text.literal("button")));
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
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth,
				this.backgroundHeight, 176, 202);
	}

	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{

	}
}

