package org.yang.iw.client.entity.dummy;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.yang.iw.entity.dummy.DummyEntity;

import java.util.UUID;

public class DummyEntityRenderer extends LivingEntityRenderer<DummyEntity, DummyEntityModel>
{
	public static final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000006");

	public DummyEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, new DummyEntityModel(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
		this.addFeature(new ArmorFeatureRenderer<>(this,
				new ArmorEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
				new ArmorEntityModel<>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()));
		this.addFeature(new DummyHeldItemFeatherRenderer<>(this, context.getHeldItemRenderer()));
		this.addFeature(new StuckArrowsFeatureRenderer<>(context, this));
		this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getHeldItemRenderer()));
	}

	@Override
	public Identifier getTexture(DummyEntity entity)
	{
		return DefaultSkinHelper.getSkinTextures(uuid).texture();
	}

	public void render(DummyEntity dummyEntity, float f, float g, MatrixStack matrixStack,
					   VertexConsumerProvider vertexConsumerProvider, int i)
	{
		this.setModelPose(dummyEntity);
		super.render(dummyEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private static BipedEntityModel.ArmPose getArmPose(DummyEntity dummy, Hand hand)
	{
		ItemStack itemStack = dummy.getStackInHand(hand);
		if (itemStack.isEmpty()) return BipedEntityModel.ArmPose.EMPTY;
		else return BipedEntityModel.ArmPose.ITEM;
	}

	private void setModelPose(DummyEntity dummy)
	{
		DummyEntityModel dummyEntityModel = this.getModel();
		dummyEntityModel.setVisible(true);
		dummyEntityModel.hat.visible = true;
		dummyEntityModel.jacket.visible = true;
		dummyEntityModel.leftPants.visible = true;
		dummyEntityModel.rightPants.visible = true;
		dummyEntityModel.leftSleeve.visible = true;
		dummyEntityModel.rightSleeve.visible = true;
		dummyEntityModel.sneaking = false;
		BipedEntityModel.ArmPose armPose = getArmPose(dummy, Hand.MAIN_HAND);
		BipedEntityModel.ArmPose armPose2 = getArmPose(dummy, Hand.OFF_HAND);
		dummyEntityModel.rightArmPose = armPose;
		dummyEntityModel.leftArmPose = armPose2;
	}
}
