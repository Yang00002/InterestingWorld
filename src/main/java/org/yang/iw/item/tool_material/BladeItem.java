package org.yang.iw.item.tool_material;

import net.minecraft.item.Item;
import org.yang.iw.tool.material.ToolMaterial;

public class BladeItem extends Item
{
	private final ToolMaterial material;

	private final float attackDamage;
	private final float attackSpeed;
	private final float durability;
	private final float boostTime;
	private final float energyRegenRate;

	public BladeItem(Settings settings, ToolMaterial material, float attackDamage, float attackSpeed, float durability
			, float boostTime, float energyRegenRate)
	{
		super(settings);
		this.material = material;
		this.attackDamage = attackDamage;
		this.attackSpeed = attackSpeed;
		this.durability = durability;
		this.boostTime = boostTime;
		this.energyRegenRate = energyRegenRate;
	}
}
