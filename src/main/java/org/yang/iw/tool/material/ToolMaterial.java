package org.yang.iw.tool.material;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistries;
import org.yang.iw.api.java.PiledShortMap;
import org.yang.iw.api.util.MutableAttributeValueDetail;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.tool.part.ToolPart;
import org.yang.iw.util.Base;
import org.yang.iw.util.style.TextStyle;

import java.util.List;
import java.util.Map;

public abstract class ToolMaterial
{
	public boolean proofFire()
	{
		return false;
	}

	public boolean proofExplode()
	{
		return false;
	}

	public boolean isOverlay()
	{
		return false;
	}

	private static final ToolMaterial DEFAULT = createInstance();

	private static ToolMaterial createInstance()
	{
		return new WoodToolMaterial(Identifier.of(Base.MOD_ID, "wood"));
	}

	public static ToolMaterial getDefault()
	{
		return DEFAULT;
	}

	private final Identifier identifier;

	private final String translateKey;

	private final RegistryEntry<ToolMaterial> registryEntry;

	public boolean isIn(TagKey<ToolMaterial> tagKey)
	{
		return registryEntry.isIn(tagKey);
	}

	public ToolMaterial(Identifier identifier)
	{
		this.registryEntry = IWRegistries.TOOL_MATERIAL.createEntry(this);
		this.identifier = identifier;
		this.translateKey = "material.%s.%s".formatted(identifier.getNamespace(), identifier.getPath());
	}

	public ToolMaterial(String id)
	{
		this.registryEntry = IWRegistries.TOOL_MATERIAL.createEntry(this);
		this.identifier = Identifier.of(Base.MOD_ID, id);
		this.translateKey = "material.%s.%s".formatted(identifier.getNamespace(), identifier.getPath());
	}

	public Identifier identifier()
	{
		return identifier;
	}

	public String translateKey()
	{
		return translateKey;
	}

	public abstract int materialColor();

	public void appendTooltip(List<Text> tooltip, ToolPart part)
	{

	}

	public void addBoost(ToolPart toolPart, PiledShortMap<AbstractBoost> boostMap)
	{

	}

	public static Text SELF_TAKE = TranslationPool.translatable("iw.tool.material.tool_material.self_take", "自带");

	public static MutableText selfTakeBoostText(AbstractBoost boost, int level)
	{
		return SELF_TAKE.copy().append(" ").append(Text.translatable(boost.translationKey())).append(" ")
				.append(TextStyle.getNumberString(level));
	}

	public void setRepairNeed(Map<ToolMaterial, MutableAttributeValueDetail> need, ToolPart part)
	{
		var n = need.getOrDefault(this, null);
		if (n == null) n = new MutableAttributeValueDetail();
		n.base += materialTake(part);
		need.put(this, n);
	}

	public void modifyRepairNeed(Map<ToolMaterial, MutableAttributeValueDetail> need, ToolPart part)
	{

	}

	public abstract int materialTake(ToolPart part);
}
