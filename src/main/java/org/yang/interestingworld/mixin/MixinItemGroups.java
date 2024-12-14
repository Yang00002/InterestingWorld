package org.yang.interestingworld.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

@Mixin(ItemGroups.class)
public abstract class MixinItemGroups
{
	@Inject(method = "addMaxLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
	private static void hideEnchantments(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper,
										 ItemGroup.StackVisibility stackVisibility, CallbackInfo ci)
	{
		registryWrapper.streamEntries().map(enchantmentEntry -> {
			if (enchantmentEntry.getIdAsString().contains("interestingworld:")) return null;
			else return EnchantedBookItem.forEnchantment(
					new EnchantmentLevelEntry(enchantmentEntry, enchantmentEntry.value().getMaxLevel()));
		}).forEach(stack -> {
			if (stack != null) entries.add(stack, stackVisibility);
		});
		ci.cancel();
	}

	@Inject(method = "addAllLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
	private static void hideEnchantments2(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper,
										  ItemGroup.StackVisibility stackVisibility, CallbackInfo ci)
	{
		registryWrapper.streamEntries().flatMap(enchantmentEntry -> {
			if (enchantmentEntry.getIdAsString().contains("interestingworld:") &&
				!enchantmentEntry.isIn(EnchantmentTags.IN_ENCHANTING_TABLE)) return null;
			else return IntStream.rangeClosed(enchantmentEntry.value().getMinLevel(),
					enchantmentEntry.value().getMaxLevel()).mapToObj(
					level -> EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantmentEntry, level)));
		}).forEach(stack -> entries.add(stack, stackVisibility));
		ci.cancel();
	}
}
