package org.yang.iw;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.joml.Random;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.enchant.resource.EnchantData;
import org.yang.iw.enchant.util.RandomEnchantGenerator;
import org.yang.iw.item.heart.base.BaseHeart;
import org.yang.iw.util.Server;
import org.yang.iw.util.style.Color;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;

public class IWCommands
{
	public static void initialize()
	{
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				CommandManager.literal("iwworldlevel").requires(source -> source.hasPermissionLevel(2))
						.then(argument("level", IntegerArgumentType.integer()).executes(context -> {
							final int level = IntegerArgumentType.getInteger(context, "level");
							if (level >= 0 && level <= 8)
							{
								var pd = Server.getPersistentData();
								if (pd.worldEnergyLevel != level)
								{
									pd.worldEnergyLevel = level;
									pd.markDirty();
								}
								context.getSource().sendFeedback(
										() -> Text.translatable(TranslationPool.COMMAND_SET_WORLD_LEVEL, level),
										false);
								return 1;
							}
							else
							{
								context.getSource().sendFeedback(
										() -> Text.translatable(TranslationPool.COMMAND_SET_WORLD_LEVEL_FAIL, level)
												.withColor(Color.RED_RGB), false);
								return 0;
							}
						}))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				CommandManager.literal("iwcheckresource")
						.then(argument("type", StringArgumentType.string()).executes(context -> {
							final String arg = StringArgumentType.getString(context, "type");
							if (Objects.equals(arg, "enchant_data"))
							{
								EnchantData.checkResource();
								return 1;
							}
							else if (Objects.equals(arg, "item_value"))
							{
								IWResources.RuneItemValue.checkResource();
								return 1;
							}
							return 0;
						}))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				CommandManager.literal("iwenchant").requires(source -> source.hasPermissionLevel(2))
						.then(argument("level", IntegerArgumentType.integer()).executes(context -> {
							final int level = Math.clamp(IntegerArgumentType.getInteger(context, "level"), 0, 8);
							var p = context.getSource().getPlayer();
							if (p != null) p.giveItemStack(
									RandomEnchantGenerator.generate((int) Random.newSeed(), level,
											BaseHeart.baseHeartSupportLevel(level)));
							return 1;
						}))));
	}
}
