package org.yang.iw;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.joml.Random;
import org.yang.iw.component.HeartDataFlag;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.enchant.resource.EnchantData;
import org.yang.iw.enchant.util.RandomEnchantGenerator;
import org.yang.iw.item.heart.AbstractHeart;
import org.yang.iw.util.Server;
import org.yang.iw.util.style.Color;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static org.yang.iw.util.Base.iwlogger;

public class IWCommands
{

	public static int[] TEST_NUMBER_SLOTS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
											 0, 0, 0, 0, 0};

	public static void initialize()
	{
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				CommandManager.literal("iwworldlevel").requires(source -> source.hasPermissionLevel(2))
						.then(argument("level", IntegerArgumentType.integer()).executes(context -> {
							final int level = IntegerArgumentType.getInteger(context, "level");
							if (level >= 0 && level <= 10)
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
							final int level = Math.clamp(IntegerArgumentType.getInteger(context, "level"), 0, 10);
							var p = context.getSource().getPlayer();
							if (p != null)
							{
								var stack = p.getStackInHand(Hand.MAIN_HAND);
								if (stack.isEmpty())
								{
									context.getSource().sendFeedback(
											() -> Text.translatable(TranslationPool.COMMAND_NO_STACK_IN_HAND,
													p.getName()).withColor(Color.RED_RGB), false);
									return 0;
								}
								if (stack.getItem() instanceof AbstractHeart heart)
								{
									if (heart.supportEnchant() && HeartDataFlag.fromItemStack(stack).getTypeTaking() ==
																  HeartDataFlag.HeartTypeTaking.NULL)
									{
										p.setStackInHand(Hand.MAIN_HAND,
												RandomEnchantGenerator.generate((int) Random.newSeed(), level, heart,
														stack));
										return 1;
									}
								}
								context.getSource().sendFeedback(
										() -> Text.translatable(TranslationPool.COMMAND_NOT_A_ENCHANT_HEART,
												p.getName()).withColor(Color.RED_RGB), false);
								return 0;
							}
							context.getSource().sendFeedback(
									() -> Text.translatable(TranslationPool.COMMAND_NOT_A_PLAYER)
											.withColor(Color.RED_RGB), false);
							return 0;
						}))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				CommandManager.literal("iwtest_setnumber")
						.then(argument("number_index", IntegerArgumentType.integer()).then(
								argument("value", StringArgumentType.string()).executes(context -> {
									try
									{
										final int index = IntegerArgumentType.getInteger(context, "number_index");
										final int value = Integer.parseInt(
												StringArgumentType.getString(context, "value"));
										if (index >= 0 && index < TEST_NUMBER_SLOTS.length)
										{
											TEST_NUMBER_SLOTS[index] = value;
											context.getSource().sendFeedback(
													() -> Text.translatable(TranslationPool.COMMAND_SET_TEST_NUMBER_SUCCESS,
															index, value).withColor(Color.GREEN_RGB), false);
											return 1;
										}
									} catch (Exception ignored)
									{

									}
									return 0;
								})))));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				CommandManager.literal("iwtest_getnumber").executes(context -> {
					int size = TEST_NUMBER_SLOTS.length;
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < size; i++)
					{
						builder.append(i).append(" ").append(Integer.toHexString(TEST_NUMBER_SLOTS[i])).append('\n');
					}
					iwlogger.info(builder.toString());
					return 1;
				})));
	}
}
