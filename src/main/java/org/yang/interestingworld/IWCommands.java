package org.yang.interestingworld;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.yang.interestingworld.enchant.EnchantData;
import org.yang.interestingworld.util.Server;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static org.yang.interestingworld.util.Base.iwlogger;

public class IWCommands
{
	public static void initialize()
	{
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				CommandManager.literal("iwworldlevel").requires(source -> source.hasPermissionLevel(2))
						.then(argument("level", IntegerArgumentType.integer()).executes(context -> {
							final int level = IntegerArgumentType.getInteger(context, "level");
							if (level >= 0)
							{
								var pd = Server.getPersistentData();
								if (pd.worldEnergyLevel != level)
								{
									pd.worldEnergyLevel = level;
									pd.markDirty();
								}
								context.getSource()
										.sendFeedback(() -> Text.translatable("iw.worldlevel_set_success", level),
												false);
								return 1;
							}
							else
							{
								context.getSource().sendFeedback(
										() -> Text.translatable("iw.worldlevel_set_fail", level)
												.withColor(IWUtil.TextStyle.RED_RGB), false);
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
				CommandManager.literal("iwxp").executes(context -> {
					if (context.getSource().isExecutedByPlayer())
					{
						var p = context.getSource().getPlayer();
						if (p != null)
						{
							iwlogger.info("Player has total xp " + p.totalExperience + " level " + p.experienceLevel +
										  " progress " + p.experienceProgress);
						}
					}
					return 1;
				})));
	}

}
