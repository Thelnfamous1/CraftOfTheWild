package com.Thelnfamous1.craft_of_the_wild.client.model;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.animal.horse.Horse;

public class TravelersSaddleModel<T extends Horse> extends HorseModel<T> {
	private final ModelPart rightBag;
	private final ModelPart leftBag;
	private final ModelPart saddle;

	public TravelersSaddleModel(ModelPart horseRoot, ModelPart root) {
        super(horseRoot);
		this.saddle = root.getChild("saddle");
		this.rightBag = this.saddle.getChild("right_bag");
		this.leftBag = this.saddle.getChild("left_bag");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();

		PartDefinition Saddle = root.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(54, 0).addBox(-5.0F, 1.0F, -7.5F, 10.0F, 10.0F, 13.0F, new CubeDeformation(0.5F))
				.texOffs(58, 37).addBox(-5.0F, -1.6F, -7.0F, 10.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(70, 67).addBox(-3.0F, -4.6F, 2.0F, 6.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(78, 87).addBox(-2.0F, -6.6F, 2.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(64, 58).addBox(-5.0F, -2.6F, -7.0F, 10.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(66, 51).addBox(-5.0F, -3.6F, 1.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 2.0F));

		PartDefinition Bag1 = Saddle.addOrReplaceChild("right_bag", CubeListBuilder.create().texOffs(26, 21).addBox(-8.0F, -2.0F, 0.0F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 1.0F, 9.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition Bag2 = Saddle.addOrReplaceChild("left_bag", CubeListBuilder.create().texOffs(26, 21).mirror().addBox(0.0F, -2.0F, 0.0F, 8.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, 1.0F, 9.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 100, 100);
	}

	@Override
	public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
		float standAnim = $$0.getStandAnim($$3);
		float standRemainder = 1.0F - standAnim;
		this.saddle.xRot = 0.0F;
		this.saddle.xRot = standAnim * -0.7853982F + standRemainder * this.saddle.xRot;
		/*
		boolean $$23 = $$0.isBaby();
		this.saddle.y = $$23 ? 10.8F : 0.0F;
		 */
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//this.saddle.y = 11.0F;
		this.saddle.y = 2.0F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.saddle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}