package com.integral.enigmaticlegacy.client.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * Generic class for extension by armor models.
 * Originated from Botania: https://github.com/Vazkii/Botania
 *
 * @author Integral
 */

public class GenericArmorModel extends BipedModel<LivingEntity> {
	protected final EquipmentSlotType slot;

	protected List<SpecialArmorModelRenderer> helmetParts = new ArrayList<>();
	protected List<SpecialArmorModelRenderer> chestplateParts = new ArrayList<>();
	protected List<SpecialArmorModelRenderer> leggingsParts = new ArrayList<>();
	protected List<SpecialArmorModelRenderer> bootsParts = new ArrayList<>();

	public GenericArmorModel(EquipmentSlotType slot) {
		super(1);
		this.slot = slot;
	}

	// [VanillaCopy] ArmorStandArmorModel.setRotationAngles because armor stands are dumb
	// This fixes the armor "breathing" and helmets always facing south on armor stands
	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!(entity instanceof ArmorStandEntity)) {
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			return;
		}

		ArmorStandEntity entityIn = (ArmorStandEntity) entity;
		this.head.xRot = ((float) Math.PI / 180F) * entityIn.getHeadPose().getX();
		this.head.yRot = ((float) Math.PI / 180F) * entityIn.getHeadPose().getY();
		this.head.zRot = ((float) Math.PI / 180F) * entityIn.getHeadPose().getZ();
		this.head.setPos(0.0F, 1.0F, 0.0F);
		this.body.xRot = ((float) Math.PI / 180F) * entityIn.getBodyPose().getX();
		this.body.yRot = ((float) Math.PI / 180F) * entityIn.getBodyPose().getY();
		this.body.zRot = ((float) Math.PI / 180F) * entityIn.getBodyPose().getZ();
		this.leftArm.xRot = ((float) Math.PI / 180F) * entityIn.getLeftArmPose().getX();
		this.leftArm.yRot = ((float) Math.PI / 180F) * entityIn.getLeftArmPose().getY();
		this.leftArm.zRot = ((float) Math.PI / 180F) * entityIn.getLeftArmPose().getZ();
		this.rightArm.xRot = ((float) Math.PI / 180F) * entityIn.getRightArmPose().getX();
		this.rightArm.yRot = ((float) Math.PI / 180F) * entityIn.getRightArmPose().getY();
		this.rightArm.zRot = ((float) Math.PI / 180F) * entityIn.getRightArmPose().getZ();
		this.leftLeg.xRot = ((float) Math.PI / 180F) * entityIn.getLeftLegPose().getX();
		this.leftLeg.yRot = ((float) Math.PI / 180F) * entityIn.getLeftLegPose().getY();
		this.leftLeg.zRot = ((float) Math.PI / 180F) * entityIn.getLeftLegPose().getZ();
		this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
		this.rightLeg.xRot = ((float) Math.PI / 180F) * entityIn.getRightLegPose().getX();
		this.rightLeg.yRot = ((float) Math.PI / 180F) * entityIn.getRightLegPose().getY();
		this.rightLeg.zRot = ((float) Math.PI / 180F) * entityIn.getRightLegPose().getZ();
		this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
		this.hat.copyFrom(this.head);
	}

	protected void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

	public void setRenderingForType(EquipmentSlotType type) {

		for (SpecialArmorModelRenderer renderer : this.helmetParts) {
			renderer.visible = type == EquipmentSlotType.HEAD || type == EquipmentSlotType.MAINHAND;
		}
		for (SpecialArmorModelRenderer renderer : this.chestplateParts) {
			renderer.visible = type == EquipmentSlotType.CHEST || type == EquipmentSlotType.MAINHAND;
		}
		for (SpecialArmorModelRenderer renderer : this.leggingsParts) {
			renderer.visible = type == EquipmentSlotType.LEGS || type == EquipmentSlotType.MAINHAND;
		}
		for (SpecialArmorModelRenderer renderer : this.bootsParts) {
			renderer.visible = type == EquipmentSlotType.FEET || type == EquipmentSlotType.MAINHAND;
		}

	}

}

