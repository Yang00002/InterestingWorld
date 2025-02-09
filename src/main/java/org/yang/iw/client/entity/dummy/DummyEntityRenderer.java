package org.yang.iw.client.entity.dummy;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.yang.iw.entity.dummy.DummyEntity;

import java.util.UUID;

public class DummyEntityRenderer extends LivingEntityRenderer<DummyEntity, PlayerEntityRenderState, DummyEntityModel>
{
	public static final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000006");

	public DummyEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, new DummyEntityModel(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
		this.addFeature(new ArmorFeatureRenderer<>(this,
				new ArmorEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
				new ArmorEntityModel<>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
				context.getEquipmentRenderer()));
		this.addFeature(new PlayerHeldItemFeatureRenderer<>(this));
		this.addFeature(new StuckArrowsFeatureRenderer<>(this, context));
		this.addFeature(new HeadFeatureRenderer<>(this, context.getEntityModels()));
	}

	@Override
	public PlayerEntityRenderState createRenderState()
	{
		return new PlayerEntityRenderState();
	}

	@Override
	public Identifier getTexture(PlayerEntityRenderState playerEntityRenderState)
	{
		return playerEntityRenderState.skinTextures.texture();
	}

	private static BipedEntityModel.ArmPose getArmPose(DummyEntity player, Arm arm)
	{
		ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
		ItemStack itemStack2 = player.getStackInHand(Hand.OFF_HAND);
		BipedEntityModel.ArmPose armPose = getArmPose(player, itemStack, Hand.MAIN_HAND);
		BipedEntityModel.ArmPose armPose2 = getArmPose(player, itemStack2, Hand.OFF_HAND);
		if (armPose.isTwoHanded())
		{
			armPose2 = itemStack2.isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
		}

		return player.getMainArm() == arm ? armPose : armPose2;
	}

	private static BipedEntityModel.ArmPose getArmPose(DummyEntity player, ItemStack stack, Hand hand)
	{
		if (stack.isEmpty())
		{
			return BipedEntityModel.ArmPose.EMPTY;
		}
		else
		{
			if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0)
			{
				UseAction useAction = stack.getUseAction();
				if (useAction == UseAction.BLOCK)
				{
					return BipedEntityModel.ArmPose.BLOCK;
				}

				if (useAction == UseAction.BOW)
				{
					return BipedEntityModel.ArmPose.BOW_AND_ARROW;
				}

				if (useAction == UseAction.SPEAR)
				{
					return BipedEntityModel.ArmPose.THROW_SPEAR;
				}

				if (useAction == UseAction.CROSSBOW)
				{
					return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
				}

				if (useAction == UseAction.SPYGLASS)
				{
					return BipedEntityModel.ArmPose.SPYGLASS;
				}

				if (useAction == UseAction.TOOT_HORN)
				{
					return BipedEntityModel.ArmPose.TOOT_HORN;
				}

				if (useAction == UseAction.BRUSH)
				{
					return BipedEntityModel.ArmPose.BRUSH;
				}
			}
			else if (!player.handSwinging && stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(stack))
			{
				return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
			}

			return BipedEntityModel.ArmPose.ITEM;
		}
	}


	public void updateRenderState(DummyEntity abstractClientPlayerEntity,
								  PlayerEntityRenderState playerEntityRenderState, float f)
	{
		super.updateRenderState(abstractClientPlayerEntity, playerEntityRenderState, f);
		BipedEntityRenderer.updateBipedRenderState(abstractClientPlayerEntity, playerEntityRenderState, f,
				this.itemModelResolver);
		playerEntityRenderState.leftArmPose = getArmPose(abstractClientPlayerEntity, Arm.LEFT);
		playerEntityRenderState.rightArmPose = getArmPose(abstractClientPlayerEntity, Arm.RIGHT);
		playerEntityRenderState.skinTextures = DefaultSkinHelper.getSkinTextures(uuid);
		playerEntityRenderState.stuckArrowCount = abstractClientPlayerEntity.getStuckArrowCount();
		playerEntityRenderState.stingerCount = abstractClientPlayerEntity.getStingerCount();
		playerEntityRenderState.itemUseTimeLeft = abstractClientPlayerEntity.getItemUseTimeLeft();
		playerEntityRenderState.handSwinging = abstractClientPlayerEntity.handSwinging;
		playerEntityRenderState.spectator = abstractClientPlayerEntity.isSpectator();
		playerEntityRenderState.hatVisible = true;
		playerEntityRenderState.jacketVisible = true;
		playerEntityRenderState.leftPantsLegVisible = true;
		playerEntityRenderState.rightPantsLegVisible = true;
		playerEntityRenderState.leftSleeveVisible = true;
		playerEntityRenderState.rightSleeveVisible = true;
		playerEntityRenderState.capeVisible = true;
		playerEntityRenderState.id = abstractClientPlayerEntity.getId();
		playerEntityRenderState.spyglassState.clear();
		if (playerEntityRenderState.isUsingItem)
		{
			ItemStack itemStack = abstractClientPlayerEntity.getStackInHand(playerEntityRenderState.activeHand);
			if (itemStack.isOf(Items.SPYGLASS))
			{
				this.itemModelResolver.updateForLivingEntity(playerEntityRenderState.spyglassState, itemStack,
						ModelTransformationMode.HEAD, false, abstractClientPlayerEntity);
			}
		}
	}
}
