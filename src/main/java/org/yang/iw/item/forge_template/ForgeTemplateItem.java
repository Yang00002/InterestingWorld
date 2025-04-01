package org.yang.iw.item.forge_template;

import com.mojang.datafixers.util.Either;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.iw.block.forgingblock.ForgeFailMessage;
import org.yang.iw.block.forgingblock.ForgingBlockScreen;
import org.yang.iw.block.forgingblock.ForgingBlockScreenHandler;
import org.yang.iw.component.IWComponents;
import org.yang.iw.component.MaterialPacketComponent;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.item.IWItems;
import org.yang.iw.resource.MaterialProvider;
import org.yang.iw.resource.MaterialVEntry;
import org.yang.iw.tool.ToolBuilder;
import org.yang.iw.tool.material.CommonToolMaterial;
import org.yang.iw.tool.material.OverlayToolMaterial;
import org.yang.iw.tool.material.ToolMaterial;
import org.yang.iw.tool.part.ToolPart;
import org.yang.iw.tool.part.ToolPartType;
import org.yang.iw.util.style.Color;

import java.util.List;

public abstract class ForgeTemplateItem extends Item
{
	final ToolPartType[] toolParts;

	public ForgeTemplateItem(Settings settings, ToolPartType... toolParts)
	{
		super(settings.maxCount(1));
		int count = 0;
		for (ToolPartType toolPart : toolParts)
		{
			if (toolPart != ToolPartType.EMPTY) count++;
			else break;
		}
		if (count > 9) count = 9;
		this.toolParts = new ToolPartType[count];
		System.arraycopy(toolParts, 0, this.toolParts, 0, this.toolParts.length);
	}

	public abstract Item itemBelong();

	@Environment(EnvType.CLIENT)
	public void drawIngredientSlots(DrawContext drawContext, ForgingBlockScreenHandler handler)
	{
		for (int i = 0; i < toolParts.length; i++)
		{
			var slot = handler.getSlot(3 + i);
			int x = slot.x;
			int y = slot.y;
			drawContext.drawTexture(RenderLayer::getGuiTextured, toolParts[i].emptyTexturePath, x, y, 0, 0, 16, 16, 16,
					16);
		}
	}

	@Environment(EnvType.CLIENT)
	public List<Text> slotTooltip(int slotIndex, List<Text> before, ToolMaterial material, int worth,
								  ForgingBlockScreenHandler handler, boolean packet, ItemStack stackInSlot)
	{
		int innerIdx = slotIndex - 3;
		if (innerIdx < 0 || innerIdx >= toolParts.length)
		{
			if (!packet) ForgingBlockScreen.appendMaterialWorthTooltip(before, material, worth);
			return before;
		}
		ToolPartType type = toolParts[innerIdx];
		ToolPart part = ToolPart.getToolPartFromIngredient(stackInSlot, type);
		if (part == null)
		{
			before = before.subList(0, 1);
			if (!packet) before.add(Text.translatable(TranslationPool.TOOLTIP_MATERIAL_WRONG).withColor(Color.RED_RGB));
			else stackInSlot.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT)
					.appendToolTip(before);
			return before;
		}
		before = before.subList(0, 1);
		int value1 = part.toolMaterial.materialTake(part);
		if (packet) stackInSlot.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT)
				.appendToolTip(before, value1,
						part.overlayToolMaterial != null ? part.overlayToolMaterial.materialTake(part) : 0);
		else before.add(ForgingBlockScreen.appendMaterialWorthText(Text.empty(), material, worth, value1, true));
		before.add(Text.empty());
		part.appendToolTipFor(before, this);
		innerIdx += toolParts.length;
		if (innerIdx < 9)
		{
			var stack = handler.getSlot(innerIdx + 3).getStack();
			if (!stack.isEmpty())
			{
				ToolPart part2 = ToolPart.getToolPartFromIngredient(stack, type);
				if (part2 == null) return before;
				before.add(Text.empty());
				part2.appendToolTipFor(before, this);
			}
		}
		return before;
	}

	public ItemStack forge(ToolPart[] parts)
	{
		return new ToolBuilder(itemBelong().getDefaultStack(), parts).build();
	}

	public Either<ItemStack, ForgeFailMessage> tryForge(ForgingBlockScreenHandler handler)
	{
		ToolPart[] parts = new ToolPart[toolParts.length];
		for (int i = 0; i < toolParts.length; i++)
		{
			ItemStack stack = handler.getSlot(i + 3).getStack();
			if (stack.isEmpty())
			{
				handler.setForgeFailSlot(i + 3);
				return Either.right(ForgeFailMessage.NEED_MATERIAL);
			}
			if (stack.getItem() != IWItems.MATERIAL_PACKET)
			{
				MaterialVEntry pair = MaterialProvider.get(stack);
				if (pair == null)
				{
					handler.setForgeFailSlot(i + 3);
					return Either.right(ForgeFailMessage.NOT_A_MATERIAL);
				}
				ToolMaterial material = pair.material();
				if (material.isOverlay())
				{
					handler.setForgeFailSlot(i + 3);
					return Either.right(ForgeFailMessage.REFUSE_OVERLAY_ALONE);
				}
				int worth = pair.value() * stack.getCount();
				ToolPart part = ((CommonToolMaterial) material).toolPart(toolParts[i]);
				if (part == null)
				{
					handler.setForgeFailSlot(i + 3);
					return Either.right(ForgeFailMessage.MATERIAL_UNSUITABLE);
				}
				if (worth < material.materialTake(part))
				{
					handler.setForgeFailSlot(i + 3);
					return Either.right(ForgeFailMessage.MATERIAL_NOT_ENOUGH);
				}
				parts[i] = part;
			}
			else
			{
				var component = stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT);
				MaterialVEntry entry = component.provideMaterial();
				if (entry == null)
				{
					handler.setForgeFailSlot(i + 3);
					return Either.right(ForgeFailMessage.NEED_MATERIAL);
				}
				MaterialVEntry overlay = component.provideMaterial(true);
				if (overlay == null)
				{
					ToolPart part = ((CommonToolMaterial) entry.material()).toolPart(toolParts[i]);
					if (part == null)
					{
						handler.setForgeFailSlot(i + 3);
						return Either.right(ForgeFailMessage.MATERIAL_UNSUITABLE);
					}
					if (entry.value() < entry.material().materialTake(part))
					{
						handler.setForgeFailSlot(i + 3);
						return Either.right(ForgeFailMessage.MATERIAL_NOT_ENOUGH);
					}
					parts[i] = part;
				}
				else
				{
					ToolPart part = ((OverlayToolMaterial) overlay.material()).toolPart(toolParts[i],
							(CommonToolMaterial) entry.material());
					if (part == null)
					{
						handler.setForgeFailSlot(i + 3);
						return Either.right(ForgeFailMessage.MATERIAL_UNSUITABLE);
					}
					if (entry.value() < entry.material().materialTake(part))
					{
						handler.setForgeFailSlot(i + 3);
						return Either.right(ForgeFailMessage.MATERIAL_NOT_ENOUGH);
					}
					if (overlay.value() < overlay.material().materialTake(part))
					{
						handler.setForgeFailSlot(i + 3);
						return Either.right(ForgeFailMessage.OVERLAY_MATERIAL_NOT_ENOUGH);
					}
					parts[i] = part;
				}
			}
		}
		return Either.left(forge(parts));
	}

	public void extractMaterials(Inventory inventory)
	{
		for (int i = 0; i < toolParts.length; i++)
		{
			ItemStack stack = inventory.getStack(i);
			ToolPart part = ToolPart.getToolPartFromIngredient(stack, toolParts[i]);
			if (part == null) continue;
			int take = part.toolMaterial.materialTake(part);
			if (stack.getItem() == IWItems.MATERIAL_PACKET)
			{
				var component = stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT);
				component = component.takeMaterial(part.toolMaterial, take);
				if (part.overlayToolMaterial != null) component = component.takeMaterial(part.overlayToolMaterial,
						part.overlayToolMaterial.materialTake(part));
				stack.set(IWComponents.MATERIAL_PACKET, component);
				continue;
			}
			MaterialVEntry entry = MaterialProvider.get(stack);
			int per = entry.value();
			int takeCount = (take + per - 1) / per;
			if (takeCount >= stack.getCount()) inventory.removeStack(i);
			else inventory.removeStack(i, takeCount);
		}
	}
}
