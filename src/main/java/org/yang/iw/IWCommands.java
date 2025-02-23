package org.yang.iw;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.joml.Random;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.boost.pool.RandomBoostGenerator;
import org.yang.iw.boost.pool.RandomBoostPool;
import org.yang.iw.component.BoostableComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.item.heart.BaseHeart;
import org.yang.iw.util.Server;
import org.yang.iw.util.style.Color;

import static net.minecraft.server.command.CommandManager.argument;
import static org.yang.iw.util.Base.iwlogger;

@IndependentRegister
public class IWCommands
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
	}

	public static int[] TEST_NUMBER_SLOTS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
											 0, 0, 0, 0, 0};

	private static void registerCommands()
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
								if (stack.getItem() instanceof BaseHeart heart &&
									stack.interestingWorld$getBoosts().isEmpty())
								{
									BoostableComponent boostableComponent = stack.getOrDefault(IWComponents.BOOSTABLE,
											BoostableComponent.DEFAULT);
									RandomBoostGenerator generator = new RandomBoostGenerator(RandomBoostPool.ALL,
											boostableComponent.isEmpty() ? 200 : boostableComponent.remainBoostTime(),
											level, heart.materialLevel, (int) Random.newSeed());
									if (generator.getBoostComponent().isEmpty())
									{
										context.getSource().sendFeedback(() -> Text.translatable(
														TranslationPool.FORGING_BLOCK_TIP_ENCHANT_NO_USEFUL_N,
														p.getName())
												.withColor(Color.RED_RGB), false);
										return 0;
									}
									stack.set(IWComponents.BOOST, generator.getBoostComponent());
									if (!boostableComponent.isEmpty()) stack.set(IWComponents.BOOSTABLE,
											boostableComponent.hardUse(generator.getBoostTimeConsume()));
									p.setStackInHand(Hand.MAIN_HAND, stack);
									return 1;
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
											context.getSource().sendFeedback(() -> Text.translatable(
															TranslationPool.COMMAND_SET_TEST_NUMBER_SUCCESS, index,
															value)
													.withColor(Color.GREEN_RGB), false);
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

	static
	{
		registerCommands();
	}

	public static void initialize()
	{

	}
}
