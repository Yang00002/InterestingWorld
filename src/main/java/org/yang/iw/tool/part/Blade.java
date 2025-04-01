package org.yang.iw.tool.part;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.tool.ToolBuilder;
import org.yang.iw.tool.material.CommonToolMaterial;
import org.yang.iw.tool.material.OverlayToolMaterial;

import java.util.List;

public class Blade extends ToolPart
{
	public final float attackDamage;
	public final float attackSpeed;
	public final int durability;
	public final int boostTime;
	public final int maxEnergy;
	public final float energyRegenRate;

	public Blade(CommonToolMaterial toolMaterial, OverlayToolMaterial overlayToolMaterial, float attackDamage,
				 float attackSpeed, int durability, int boostTime, int maxEnergy, float energyRegenRate)
	{
		super(toolMaterial, overlayToolMaterial);
		this.attackDamage = attackDamage;
		this.attackSpeed = attackSpeed;
		this.durability = durability;
		this.boostTime = boostTime;
		this.maxEnergy = maxEnergy;
		this.energyRegenRate = energyRegenRate;
	}

	public Blade(CommonToolMaterial toolMaterial, float attackDamage, float attackSpeed, int durability, int boostTime
			, int maxEnergy, float energyRegenRate)
	{
		super(toolMaterial, null);
		this.attackDamage = attackDamage;
		this.attackSpeed = attackSpeed;
		this.durability = durability;
		this.boostTime = boostTime;
		this.maxEnergy = maxEnergy;
		this.energyRegenRate = energyRegenRate;
	}

	@Override
	public ToolPartType typeBelong()
	{
		return ToolPartType.BLADE;
	}

	@Override
	public void appendToolTipFor(List<Text> before, Item item)
	{
		addStringACommonFloatValue(before, TranslationPool.TOOL_PART_DAMAGE, attackDamage);
		addStringACommonFloatValue(before, TranslationPool.TOOL_PART_SPEED, attackSpeed);
		addStringACommonIntValue(before, TranslationPool.TOOL_PART_DURABILITY, durability);
		addStringALevelValue(before, TranslationPool.TOOL_PART_BOOST_TIME, boostTime);
		addStringACommonIntValue(before, TranslationPool.TOOL_PART_BOOST_MAX_ENERGY, maxEnergy);
		addStringA01FluentFloatValue(before, TranslationPool.TOOL_PART_BOOST_ENERGY_RENGEN_RATE, energyRegenRate);
		super.appendToolTipFor(before, item);
	}

	@Override
	public void setToolBuilder(ToolBuilder builder)
	{
		super.setToolBuilder(builder);
		builder.attackDamage.base += attackDamage;
		builder.attackSpeed.base += attackSpeed;
		builder.durability.base += durability;
		builder.boostTime.base += boostTime;
		builder.maxEnergy.base += maxEnergy;
		builder.energyRegenRate.mul *= energyRegenRate;
	}
}
