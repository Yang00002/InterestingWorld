package org.yang.iw.block.forgingblock;

import net.minecraft.inventory.SimpleInventory;

public class ForgingBlockInventory extends SimpleInventory
{
	private boolean actWhenDirty = true;
	private boolean isChanged = false;
	private boolean shouldTraceChange = true;

	public void setWaiting()
	{
		actWhenDirty = false;
		shouldTraceChange = true;
	}

	public void setSleeping()
	{
		actWhenDirty = false;
		shouldTraceChange = false;
	}


	public void setActive()
	{
		actWhenDirty = true;
		if (isChanged)
		{
			onContentChanged();
			isChanged = false;
		}
	}

	public void onContentChanged()
	{

	}

	@Override
	public final void markDirty()
	{
		super.markDirty();
		if (actWhenDirty) onContentChanged();
		else if (shouldTraceChange) isChanged = true;
	}

	ForgingBlockInventory(int size)
	{
		super(size);
	}
}
