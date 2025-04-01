package org.yang.iw.block.forgingblock;

public enum ForgeFailMessage
{
	NEED_MATERIAL(ForgingBlockState.FORGE_NEED_MATERIALS),

	NOT_A_MATERIAL(ForgingBlockState.FORGE_NOT_A_MATERIAL),

	MATERIAL_UNSUITABLE(ForgingBlockState.FORGE_MATERIAL_UNSUITABLE),

	MATERIAL_NOT_ENOUGH(ForgingBlockState.FORGE_MATERIAL_NOT_ENOUGH),

	OVERLAY_MATERIAL_NOT_ENOUGH(ForgingBlockState.FORGE_MATERIAL_NOT_ENOUGH),

	REFUSE_OVERLAY_ALONE(ForgingBlockState.FORGE_NOT_A_MATERIAL);

	public final ForgingBlockState relevantState;

	ForgeFailMessage(ForgingBlockState relevantState)
	{
		this.relevantState = relevantState;
	}
}
