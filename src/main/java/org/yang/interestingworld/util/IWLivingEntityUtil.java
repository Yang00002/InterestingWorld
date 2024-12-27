package org.yang.interestingworld.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class IWLivingEntityUtil
{
	public static Box getSphereAttackRangeBox(double maxLength, double x, double y, double z)
	{
		return new Box(x - maxLength - 2, y - maxLength - 6, z - maxLength - 2, x + maxLength + 2, y + maxLength + 2,
				z + maxLength + 2);
	}

	@Deprecated
	public static Box getHorizontalAttackRangeBlock(double maxAngleCosin, double maxLength, double yExpanse, double x,
													double y, double z, double baseY, double NormalizedViewX,
													double NormalizedViewY, double NormalizedViewZ)
	{
		double viewX = NormalizedViewX * maxLength;
		double viewY = NormalizedViewY * maxLength;
		double viewZ = NormalizedViewZ * maxLength;
		double horizontalLengthsq = viewX * viewX + viewZ * viewZ;
		double Lengthsq = maxLength * maxLength;
		if (Lengthsq < 0.005)
		{
			if (viewY > 0) return new Box(x - yExpanse, y, z - yExpanse, x + yExpanse, y + maxLength, z + yExpanse);
			else return new Box(x - yExpanse, y - maxLength, z - yExpanse, x + yExpanse, y, z + yExpanse);
		}
		double cosinbeta = 1 - (1 - maxAngleCosin) * (Lengthsq / horizontalLengthsq);
		double sinbeta = Math.sqrt(1 - cosinbeta * cosinbeta);
		double xrange = Math.abs(sinbeta * viewZ);
		double zrange = Math.abs(sinbeta * viewX);
		double minX = x;
		double maxX = x;
		double minZ = z;
		double maxZ = z;
		if (viewX > 0)
		{
			maxX += viewX + xrange;
			minX -= xrange;
		}
		else
		{
			maxX += xrange;
			minX += viewX - xrange;
		}
		if (viewZ > 0)
		{
			maxZ += viewZ + zrange;
			minZ -= zrange;
		}
		else
		{
			maxZ += zrange;
			minZ += viewZ - zrange;
		}
		double yMultipiler = Math.sqrt(horizontalLengthsq / Lengthsq);
		double minY = baseY - yMultipiler;
		double maxY = y + yMultipiler;
		return new Box(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static Vec3d getVec3toEntity(LivingEntity entity, double x, double y, double z)
	{
		if (entity instanceof EnderDragonEntity) return new Vec3d(0, 0, 0);
		Box boundingBox = entity.getBoundingBox();
		double xhalf = boundingBox.getLengthX() / 2;
		double yhalf = boundingBox.getLengthY() / 2;
		double zhalf = boundingBox.getLengthZ() / 2;
		double pivoty = entity.getY() + yhalf;
		double pivotx = entity.getX();
		double pivotz = entity.getZ();
		double diffx = x - pivotx;
		double diffy = y - pivoty;
		double diffz = z - pivotz;
		int sigx = diffx >= 0 ? 1 : -1;
		int sigy = diffy >= 0 ? 1 : -1;
		int sigz = diffz >= 0 ? 1 : -1;
		double sigxhalf = sigx * xhalf;
		double sigyhalf = sigy * yhalf;
		double sigzhalf = sigz * zhalf;
		double tx = diffx / sigxhalf;
		double ty = diffy / sigyhalf;
		double tz = diffz / sigzhalf;
		double retx = tx > 1 ? sigxhalf - diffx : 0;
		double rety = ty > 1 ? sigyhalf - diffy : 0;
		double retz = tz > 1 ? sigzhalf - diffz : 0;
		return new Vec3d(retx, rety, retz);
	}

	public interface DistancedEntityInfluencer
	{
		void attack(LivingEntity attacker, LivingEntity entity, double distance);
	}

	public static void influenceEntityAround(LivingEntity attacker, double maxLength, DistancedEntityInfluencer dealer)
	{
		Vec3d eyeV = attacker.getEyePos();
		double x = eyeV.x;
		double y = eyeV.y;
		double z = eyeV.z;
		Box collectBox = getSphereAttackRangeBox(maxLength, x, y, z);
		World world = attacker.getWorld();
		List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, collectBox);
		for (LivingEntity entity : list)
		{
			if (entity == null) continue;
			if (entity == attacker) continue;
			if (entity instanceof ArmorStandEntity) continue;
			if (entity instanceof TameableEntity tameableEntity)
			{
				if (tameableEntity.isOwner(attacker)) continue;
			}
			if (Saddleable.class.isAssignableFrom(entity.getClass()))
			{
				if (((Saddleable) entity).isSaddled()) continue;
			}
			if (attacker.isTeammate(entity)) continue;
			Vec3d dis = getVec3toEntity(entity, x, y, z);
			double len = dis.length();
			if (len <= maxLength) dealer.attack(attacker, entity, len);
		}
	}


	public static void sweepEntity(LivingEntity attacker, double maxAngleCosin, double maxLength, LivingEntity target,
								   DistancedEntityInfluencer dealer)
	{
		Vec3d eyeV = attacker.getEyePos();
		double x = eyeV.x;
		double y = eyeV.y;
		double z = eyeV.z;
		Vec3d viewV = attacker.getRotationVector();
		viewV = viewV.normalize();
		double viewX = viewV.x;
		double viewY = viewV.y;
		double viewZ = viewV.z;
		Box collectBox = getSphereAttackRangeBox(maxLength, x, y, z);
		World world = attacker.getWorld();
		List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, collectBox);
		if (!(target instanceof ArmorStandEntity))
			dealer.attack(attacker, target, eyeV.distanceTo(getVec3toEntity(target, x, y, z)));
		for (LivingEntity entity : list)
		{
			if (entity == null) continue;
			if (entity == attacker) continue;
			if (entity == target) continue;
			if (entity instanceof ArmorStandEntity) continue;
			if (entity instanceof TameableEntity tameableEntity)
			{
				if (tameableEntity.isOwner(attacker)) continue;
			}
			if (Saddleable.class.isAssignableFrom(entity.getClass()))
			{
				if (((Saddleable) entity).isSaddled()) continue;
			}
			if (attacker.isTeammate(entity)) continue;
			Vec3d dis = getVec3toEntity(entity, x, y, z);
			double len = dis.length();
			double angle = (viewX * dis.x + viewY * dis.y + viewZ * dis.z) / len;
			if (len <= maxLength && (len <= 0.1 || angle >= maxAngleCosin)) dealer.attack(attacker, entity, len);
		}
	}
}
