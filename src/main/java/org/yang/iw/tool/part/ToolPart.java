package org.yang.iw.tool.part;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
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
import org.yang.iw.util.constants.Numbers;
import org.yang.iw.util.style.Color;
import org.yang.iw.util.style.TextStyle;

import java.util.List;

import static org.yang.iw.util.style.Color.CYAN_RGB;
import static org.yang.iw.util.style.Color.getLevelColor;

public abstract class ToolPart
{
	public final CommonToolMaterial toolMaterial;
	public final OverlayToolMaterial overlayToolMaterial;

	protected ToolPart(CommonToolMaterial toolMaterial, OverlayToolMaterial overlayToolMaterial)
	{
		this.toolMaterial = toolMaterial;
		this.overlayToolMaterial = overlayToolMaterial;
	}

	public boolean proofFire()
	{
		if (overlayToolMaterial == null) return toolMaterial.proofFire();
		return overlayToolMaterial.proofFire();
	}

	public boolean proofExplode()
	{
		return toolMaterial.proofExplode() || (overlayToolMaterial != null && overlayToolMaterial.proofExplode());
	}

	// super.modify -> self.modify
	public void setToolBuilder(ToolBuilder builder)
	{
		builder.proofFire.add(proofFire());
		builder.proofExplode.add(proofExplode());
		if (overlayToolMaterial != null)
		{
			overlayToolMaterial.setRepairNeed(builder.repairPacket, this);
			overlayToolMaterial.addBoost(this, builder.defaultBoostMap);
		}
		else
		{
			toolMaterial.setRepairNeed(builder.repairPacket, this);
		}
		toolMaterial.addBoost(this, builder.defaultBoostMap);
	}

	// super.modify -> self.modify
	public void modifyToolBuilder(ToolBuilder builder)
	{
		toolMaterial.modifyRepairNeed(builder.repairPacket, this);
		if (overlayToolMaterial != null) overlayToolMaterial.modifyRepairNeed(builder.repairPacket, this);
	}

	public abstract ToolPartType typeBelong();

	public static Text PROOF_FIRE = TranslationPool.translatable("iw.tool.part.tool_part.proof_fire", "防火",
			t -> t.withColor(0XFF4500));
	public static Text PROOF_EXPLODE = TranslationPool.translatable("iw.tool.part.tool_part.proof_explode", "防爆",
			t -> t.withColor(CYAN_RGB));

	// self.append -> super.append
	public void appendToolTipFor(List<Text> before, Item item)
	{
		if (proofFire()) before.add(PROOF_FIRE);
		if (proofExplode()) before.add(PROOF_EXPLODE);
		toolMaterial.appendTooltip(before, this);
		if (overlayToolMaterial != null) overlayToolMaterial.appendTooltip(before, this);
	}

	public static int colorForMultiplier(float f)
	{
		if (f >= 1.0f + Numbers.FLOAT_EPSILON) return Color.GREEN_RGB;
		if (f <= 1.0f - Numbers.FLOAT_EPSILON) return Color.RED_RGB;
		return Color.YELLOW_RGB;
	}

	public static void addStringACommonFloatValue(List<Text> to, String translateKey, float value)
	{
		to.add(Text.translatable(translateKey)
				.append(Text.literal(TextStyle.FLOAT_FORMAT.format(value)).withColor(Color.YELLOW_RGB)));
	}

	public static void addStringA01FluentFloatValue(List<Text> to, String translateKey, float value)
	{
		to.add(Text.translatable(translateKey).append(Text.literal(TextStyle.FLOAT_FORMAT.format(value))
				.withColor(MathHelper.hsvToRgb(Math.clamp(value, 0, 1) / 3.0F, 1.0F, 1.0F))));
	}

	public static void addStringACommonIntValue(List<Text> to, String translateKey, float value)
	{
		to.add(Text.translatable(translateKey)
				.append(Text.literal(String.valueOf((int) value)).withColor(Color.YELLOW_RGB)));
	}

	public static void addStringAMultiplierValue(List<Text> to, String translateKey, float value)
	{
		to.add(Text.translatable(translateKey)
				.append(Text.literal(TextStyle.FLOAT_FORMAT.format(value) + "x").withColor(colorForMultiplier(value))));
	}

	public static void addStringALevelValue(List<Text> to, String translateKey, float value)
	{
		to.add(Text.translatable(translateKey).append(Text.literal(String.valueOf((int) value))
				.withColor(getLevelColor(Math.clamp((int) value, 0, 10)))));
	}

	public static ToolPart getToolPartFromIngredient(ItemStack stack, ToolPartType type)
	{
		if (stack.isEmpty()) return null;
		if (stack.getItem() != IWItems.MATERIAL_PACKET)
		{
			MaterialVEntry pair = MaterialProvider.get(stack);
			if (pair == null) return null;
			ToolMaterial material = pair.material();
			if (material.isOverlay()) return null;
			return ((CommonToolMaterial) material).toolPart(type);
		}
		else
		{
			var component = stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT);
			MaterialVEntry entry = component.provideMaterial();
			if (entry == null) return null;
			MaterialVEntry overlay = component.provideMaterial(true);
			if (overlay == null) return ((CommonToolMaterial) entry.material()).toolPart(type);
			else
				return ((OverlayToolMaterial) overlay.material()).toolPart(type,
						(CommonToolMaterial) entry.material());
		}
	}
}
