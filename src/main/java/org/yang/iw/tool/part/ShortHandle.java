package org.yang.iw.tool.part;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.tool.ToolBuilder;
import org.yang.iw.tool.material.CommonToolMaterial;
import org.yang.iw.tool.material.OverlayToolMaterial;

import java.util.List;

public class ShortHandle extends ToolPart
{
	public final float attackDamageMultiplier;
	public final float attackSpeedMultiplier;
	public final float durabilityMultiplier;
	public final int boostTime;
	public final int maxEnergy;
	public final float energyRegenRate;

	public ShortHandle(CommonToolMaterial toolMaterial, OverlayToolMaterial overlayToolMaterial,
					   float attackDamageMultiplier, float attackSpeedMultiplier, float durabilityMultiplier,
					   int boostTime, int maxEnergy, float energyRegenRate)
	{
		super(toolMaterial, overlayToolMaterial);
		this.attackDamageMultiplier = attackDamageMultiplier;
		this.attackSpeedMultiplier = attackSpeedMultiplier;
		this.durabilityMultiplier = durabilityMultiplier;
		this.boostTime = boostTime;
		this.maxEnergy = maxEnergy;
		this.energyRegenRate = energyRegenRate;
	}

	public ShortHandle(CommonToolMaterial toolMaterial, float attackDamageMultiplier, float attackSpeedMultiplier,
					   float durabilityMultiplier, int boostTime, int maxEnergy, float energyRegenRate)
	{
		super(toolMaterial, null);
		this.attackDamageMultiplier = attackDamageMultiplier;
		this.attackSpeedMultiplier = attackSpeedMultiplier;
		this.durabilityMultiplier = durabilityMultiplier;
		this.boostTime = boostTime;
		this.maxEnergy = maxEnergy;
		this.energyRegenRate = energyRegenRate;
	}


	@Override
	public ToolPartType typeBelong()
	{
		return ToolPartType.SHORT_HANDLE;
	}

	@Override
	public void appendToolTipFor(List<Text> before, Item item)
	{
		addStringAMultiplierValue(before, TranslationPool.TOOL_PART_DAMAGE, attackDamageMultiplier);
		addStringAMultiplierValue(before, TranslationPool.TOOL_PART_SPEED, attackSpeedMultiplier);
		addStringAMultiplierValue(before, TranslationPool.TOOL_PART_DURABILITY, durabilityMultiplier);
		addStringACommonIntValue(before, TranslationPool.TOOL_PART_BOOST_TIME, boostTime);
		addStringACommonIntValue(before, TranslationPool.TOOL_PART_BOOST_MAX_ENERGY, maxEnergy);
		addStringA01FluentFloatValue(before, TranslationPool.TOOL_PART_BOOST_ENERGY_RENGEN_RATE, energyRegenRate);
		super.appendToolTipFor(before, item);
	}

	@Override
	public void setToolBuilder(ToolBuilder builder)
	{
		super.setToolBuilder(builder);
		builder.attackDamage.sum += attackDamageMultiplier - 1.0F;
		builder.attackSpeed.sum += attackSpeedMultiplier - 1.0F;
		builder.durability.sum += durabilityMultiplier - 1.0F;
		builder.boostTime.base += boostTime;
		builder.maxEnergy.base += maxEnergy;
		builder.energyRegenRate.mul *= energyRegenRate;
	}
}