package net.depression.rhythmcraft;

import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RCMinecart extends Minecart {
    public Vec3 speed = Vec3.ZERO;
    public boolean isLocked = true;
    public RCMinecart(Level level, double d, double e, double f) {
        super(level, d, e, f);
    }
    @Override
    public Vec3 getDeltaMovement() {
        return speed;
    }
    @Override
    public void setDeltaMovement(double d, double e, double f) {
        if (!isLocked) {
            speed = new Vec3(d, e, f);
            super.setDeltaMovement(d, e, f);
        }
    }
    @Override
    public void setDeltaMovement(Vec3 vec3) {
        if (!isLocked) {
            speed = vec3;
            super.setDeltaMovement(vec3);
        }
    }
    @Override
    public void addDeltaMovement(Vec3 vec3) {
        if (!isLocked) {
            speed = speed.add(vec3);
            super.addDeltaMovement(vec3);
        }
    }
    @Override
    public boolean isVehicle() {
        return false;
    }
    @Override
    public double getMaxSpeed() {
        return Double.MAX_VALUE;
    }
    @Override
    public void applyNaturalSlowdown() {}
    public void setLockedDeltaMovement(Vec3 vec3) {
        isLocked = false;
        setDeltaMovement(vec3);
        isLocked = true;
    }
}