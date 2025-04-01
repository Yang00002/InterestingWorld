package org.yang.iw.item.tool_material;

import net.minecraft.item.Item;
import org.yang.iw.tool.material.ToolMaterial;

public class ShortHandleItem extends Item
{
	private final ToolMaterial material;
	private final float attackDamageMultiplier;
	private final float attackSpeedMultiplier;
	private final float durabilityMultiplier;
	private final float boostTimeMultiplier;
	private final float energyRegenRate;

	public ShortHandleItem(Settings settings, ToolMaterial material, float attackDamageMultiplier,
						   float attackSpeedMultiplier, float durabilityMultiplier, float boostTimeMultiplier,
						   float energyRegenRate)
	{
		super(settings);
		this.material = material;
		this.attackDamageMultiplier = attackDamageMultiplier;
		this.attackSpeedMultiplier = attackSpeedMultiplier;
		this.durabilityMultiplier = durabilityMultiplier;
		this.boostTimeMultiplier = boostTimeMultiplier;
		this.energyRegenRate = energyRegenRate;
	}
}
