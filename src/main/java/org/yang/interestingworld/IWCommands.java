package org.yang.interestingworld;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.Server;

import static net.minecraft.server.command.CommandManager.argument;

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
								if(pd.worldEnergyLevel != level)
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
	}

}
