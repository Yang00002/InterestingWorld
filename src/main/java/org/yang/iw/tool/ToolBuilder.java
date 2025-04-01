package org.yang.iw.tool;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import org.yang.iw.IWDamageTypeTags;
import org.yang.iw.api.java.Booleans;
import org.yang.iw.api.java.PiledShortMap;
import org.yang.iw.api.util.MutableAttributeValueDetail;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.component.*;
import org.yang.iw.tool.material.ToolMaterial;
import org.yang.iw.tool.part.ToolPart;
import org.yang.iw.util.IWAttributeModifierUtil;

import java.util.Map;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static net.minecraft.item.Item.BASE_ATTACK_SPEED_MODIFIER_ID;

public class ToolBuilder
{
	ItemStack stack;
	ToolPart[] parts;
	public MutableAttributeValueDetail durability = new MutableAttributeValueDetail();
	public MutableAttributeValueDetail attackDamage = new MutableAttributeValueDetail();
	public MutableAttributeValueDetail attackSpeed = new MutableAttributeValueDetail();
	public MutableAttributeValueDetail boostTime = new MutableAttributeValueDetail();
	public MutableAttributeValueDetail energyRegenRate = new MutableAttributeValueDetail(1);
	public MutableAttributeValueDetail maxEnergy = new MutableAttributeValueDetail();
	public PiledShortMap<AbstractBoost> defaultBoostMap = new PiledShortMap<>();
	public Booleans proofFire = new Booleans(false);
	public Booleans proofExplode = new Booleans(false);
	public Map<ToolMaterial, MutableAttributeValueDetail> repairPacket = new Object2ObjectOpenHashMap<>();

	public ToolBuilder(ItemStack defaultStack, ToolPart[] parts)
	{
		stack = defaultStack;
		this.parts = parts;
		for (var part : parts) part.setToolBuilder(this);
		for (var part : parts) part.modifyToolBuilder(this);
	}

	public ItemStack build()
	{
		ItemStack ret = stack.copy();
		int lastDurability = (int) (durability.value() - 1);
		lastDurability = Math.max(lastDurability, 1);
		ret.set(DataComponentTypes.MAX_DAMAGE, lastDurability);
		ret.set(DataComponentTypes.DAMAGE, 0);
		var builder = IWAttributeModifierUtil.getModifiersFromItemStack(ret);
		builder.add(EntityAttributes.ATTACK_DAMAGE,
				new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, attackDamage.value() - 1,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		builder.add(EntityAttributes.ATTACK_SPEED,
				new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, attackSpeed.value() - 4.0f,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		IWAttributeModifierUtil.applyAttributeModifierToItemStack(builder, ret);
		var materialList = new ObjectArrayList<ToolMaterial>();
		for (var i : parts)
		{
			if (i.overlayToolMaterial != null) materialList.add(i.overlayToolMaterial);
			else materialList.add(i.toolMaterial);
		}
		ret.set(IWComponents.TOOL_MATERIAL, ToolMaterialComponent.create(materialList));
		RepairPacketComponent.Builder repairPacketBuilder = RepairPacketComponent.builder();
		for (var entry : repairPacket.entrySet())
		{
			var k = entry.getKey();
			var v = entry.getValue();
			repairPacketBuilder.add(k, (int) v.value());
		}
		ret.set(IWComponents.REPAIR_PACKET, repairPacketBuilder.build());
		int boostTimeValue = Math.max((int) boostTime.value(), 1);
		ret.set(IWComponents.BOOSTABLE, new BoostableComponent(boostTimeValue, boostTimeValue));
		ret.set(IWComponents.ENERGY_REGEN_RATE, Math.max(0, energyRegenRate.value()));
		int energy = Math.max((int) maxEnergy.value(), 1);
		ret.set(IWComponents.MAX_ENERGY, (float) energy);
		ret.set(IWComponents.CURRENT_ENERGY, (float) energy);
		boolean proofFire = this.proofFire.and();
		boolean proofExplode = this.proofExplode.and();
		if (proofFire)
		{
			if (proofExplode) ret.set(DataComponentTypes.DAMAGE_RESISTANT,
					new DamageResistantComponent(IWDamageTypeTags.FIRE_EXPLODE));
			else ret.set(DataComponentTypes.DAMAGE_RESISTANT, new DamageResistantComponent(DamageTypeTags.IS_FIRE));
		}
		else if (proofExplode)
			ret.set(DataComponentTypes.DAMAGE_RESISTANT, new DamageResistantComponent(DamageTypeTags.IS_EXPLOSION));
		if (!defaultBoostMap.asMap().isEmpty())
			ret.set(IWComponents.BOOST, BoostComponent.ofDefault(defaultBoostMap.asMap()));
		return ret;
	}
}
