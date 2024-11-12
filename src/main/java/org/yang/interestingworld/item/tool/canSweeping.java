package org.yang.interestingworld.item.tool;
import net.minecraft.item.ItemStack;

/**
 * Item implements this can sweep
 */
public interface canSweeping
{
    /**
     * Declare whether weapon can sweep or not,
     * ItemStack can sweep when its ability allow,
     * even if that method return false
     */
    boolean canSweep(ItemStack stack);
}
