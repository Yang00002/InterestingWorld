package org.yang.iw.tool.material;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.tool.part.Blade;
import org.yang.iw.tool.part.ShortHandle;
import org.yang.iw.tool.part.ToolPart;
import org.yang.iw.tool.part.ToolPartType;
import org.yang.iw.util.style.Color;

public class NetheriteToolMaterial extends OverlayToolMaterial
{
	public NetheriteToolMaterial(Identifier identifier)
	{
		super(identifier);
	}

	public NetheriteToolMaterial(String identifier)
	{
		super(identifier);
	}

	public boolean proofFire()
	{
		return true;
	}

	public boolean proofExplode()
	{
		return true;
	}

	@Override
	public @Nullable ToolPart toolPart(ToolPartType part, CommonToolMaterial target)
	{
		ToolPart partOrigin = target.toolPart(part);
		if (partOrigin == null) return null;
		switch (part)
		{
			case BLADE ->
			{
				Blade blade = (Blade) partOrigin;
				return new Blade(target, this, blade.attackDamage + 0.5f, blade.attackSpeed, blade.durability + 256,
						blade.boostTime + 2, blade.maxEnergy + 5, Math.min(1f, blade.energyRegenRate * 1.1f));
			}
			case SHORT_HANDLE ->
			{
				ShortHandle handle = (ShortHandle) partOrigin;
				return new ShortHandle(target, this, handle.attackDamageMultiplier + 0.1f,
						handle.attackSpeedMultiplier - 0.05f, handle.durabilityMultiplier + 0.2f, handle.boostTime + 1,
						handle.maxEnergy + 3, Math.min(1f, handle.energyRegenRate * 1.1f));

			}
		}
		return null;
	}

	@Override
	public int materialColor()
	{
		return Color.NetheriteColorRGB;
	}
}
