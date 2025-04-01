package org.yang.iw.item;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import org.lwjgl.glfw.GLFW;
import org.yang.iw.IWDamageTypeTags;
import org.yang.iw.client.IWInteractionUtil;
import org.yang.iw.component.IWComponents;
import org.yang.iw.component.MaterialPacketComponent;
import org.yang.iw.network.payload.C2SMaterialPacketPayload;

import java.util.List;
//TODO 收纳袋一样的材质
public class MaterialPacketItem extends Item
{

	public MaterialPacketItem(Settings settings)
	{
		super(settings.maxCount(1).component(DataComponentTypes.DAMAGE_RESISTANT,
				new DamageResistantComponent(IWDamageTypeTags.FIRE_EXPLODE)));
	}

	private static void playInsertSound(Entity entity)
	{
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F,
				0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertFailSound(Entity entity)
	{
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
	}

	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType,
							 PlayerEntity player, StackReference cursorStackReference)
	{
		if (!otherStack.isEmpty())
		{
			if (clickType == ClickType.RIGHT)
			{
				if (slot.canTakePartial(player))
				{
					MaterialPacketComponent materialPacketComponent = stack.getOrDefault(IWComponents.MATERIAL_PACKET,
							MaterialPacketComponent.DEFAULT);
					if (otherStack.getItem() == IWItems.MATERIAL_PACKET)
					{
						if (materialPacketComponent.insertMaterialPacket(otherStack, stack, false))
						{
							playInsertSound(player);
							onContentChanged(player);
						}
						else playInsertFailSound(player);
						return true;
					}
					if (materialPacketComponent.insertCommonItem(otherStack, stack))
					{
						playInsertSound(player);
						otherStack.decrement(otherStack.getCount());
						onContentChanged(player);
					}
					else playInsertFailSound(player);
					return true;
				}
				else playInsertFailSound(player);
			}
			else if (slot.canTakePartial(player) && otherStack.getItem() == IWItems.MATERIAL_PACKET)
			{
				MaterialPacketComponent materialPacketComponent = stack.getOrDefault(IWComponents.MATERIAL_PACKET,
						MaterialPacketComponent.DEFAULT);
				if (materialPacketComponent.insertMaterialPacket(otherStack, stack, true))
				{
					playInsertSound(player);
					onContentChanged(player);
				}
				else playInsertFailSound(player);
				return true;
			}
		}
		return false;
	}

	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player)
	{
		if (clickType == ClickType.RIGHT && !stack.isEmpty())
		{
			ItemStack itemStack = slot.getStack();
			if (itemStack.getItem() == IWItems.MATERIAL_PACKET) return false;
			if (stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT)
					.insertCommonItem(itemStack, stack))
			{
				slot.setStack(ItemStack.EMPTY);
				playInsertSound(player);
				onContentChanged(player);
			}
			else playInsertFailSound(player);
			return true;
		}
		return false;
	}

	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).appendToolTip(tooltip);
	}

	private void onContentChanged(PlayerEntity user)
	{
		ScreenHandler screenHandler = user.currentScreenHandler;
		if (screenHandler != null)
		{
			screenHandler.onContentChanged(user.getInventory());
		}
	}

	public static int getMaterialCount(boolean shift, ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).size(shift);
	}

	public static void setIndex(ItemStack stack, boolean shift, int idx)
	{
		var i = stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).setIndex(shift, idx);
		stack.set(IWComponents.MATERIAL_PACKET, i);
	}

	public static int getIndex(ItemStack stack, boolean shift)
	{
		return stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT).getIndex(shift);
	}

	public boolean mouseScroll(double horizontalAmount, double verticalAmount, HandledScreen<?> screen, Slot slot,
							   MinecraftClient client)
	{
		ItemStack item = slot.getStack();
		int amount = IWInteractionUtil.verticalScroll(horizontalAmount, verticalAmount);
		boolean shift = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);
		int i = MaterialPacketItem.getMaterialCount(shift, item);
		if (i == 0) return false;
		else
		{
			if (amount != 0)
			{
				int k = MaterialPacketItem.getIndex(item, shift);
				int shiftSig = (shift ? 1 : 0);
				int l = IWInteractionUtil.scrollCycling(amount, k + shiftSig, i + shiftSig) - shiftSig;
				if (k != l)
				{
					int slotId = IWInteractionUtil.predicateSlotId(screen, slot);
					if (slotId < 0) return false;
					this.sendPacket(item, slotId, l, shift, client);
				}
			}
			return true;
		}
	}

	private void sendPacket(ItemStack stack, int slotId, int selectedItemIndex, boolean shift, MinecraftClient client)
	{
		if (client.getNetworkHandler() != null)
		{
			MaterialPacketItem.setIndex(stack, shift, selectedItemIndex);
			ClientPlayNetworking.send(new C2SMaterialPacketPayload(selectedItemIndex, slotId, shift));
		}
	}
}
