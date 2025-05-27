package net.depression.world;

import io.github.beardedManZhao.mathematicalExpression.core.calculation.number.BracketsCalculation2;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import top.lingyuzhao.varFormatter.utils.DataObj;

public class ParticleFormulaInstance {
    BracketsCalculation2 bracketsCalculation = BracketsCalculation2.getInstance("BracketsCalculation");
    private ServerLevel level;
    private ParticleOptions particle;
    private String x, y, z;
    private String vx, vy, vz;
    private double l, r;
    private double t;
    private double speed;
    private double density;
    private boolean isInstant;
    private double lastGenT = Integer.MIN_VALUE;
    public ParticleFormulaInstance(ServerLevel level, ParticleOptions particle, String x, String y, String z, String vx, String vy, String vz, double l, double r, double speed, double density, boolean isInstant) {
        this.level = level;
        this.particle = particle;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.l = l;
        this.r = r;
        this.speed = speed;
        this.density = density;
        this.isInstant = isInstant;
        t = l;
    }

    public boolean tick() {
        if (isInstant) {
            while (t < r) {
                double x = bracketsCalculation.calculation(this.x.replace("t", String.valueOf(t))).getResult();
                double y = bracketsCalculation.calculation(this.y.replace("t", String.valueOf(t))).getResult();
                double z = bracketsCalculation.calculation(this.z.replace("t", String.valueOf(t))).getResult();
                double vx = bracketsCalculation.calculation(this.vx.replace("t", String.valueOf(t))).getResult();
                double vy = bracketsCalculation.calculation(this.vy.replace("t", String.valueOf(t))).getResult();
                double vz = bracketsCalculation.calculation(this.vz.replace("t", String.valueOf(t))).getResult();
                level.sendParticles(particle, x, y, z, 0, vx, vy, vz, Math.sqrt(vx * vx + vy * vy + vz * vz));
                t += density;
            }
            return false;
        }
        if (t >= r) {
            return false;
        }
        while (t - lastGenT > density) {
            if (lastGenT == Integer.MIN_VALUE) {
                lastGenT = l;
            }
            else {
                lastGenT += density;
            }
            double x = bracketsCalculation.calculation(this.x.replace("t", String.valueOf(lastGenT))).getResult();
            double y = bracketsCalculation.calculation(this.y.replace("t", String.valueOf(lastGenT))).getResult();
            double z = bracketsCalculation.calculation(this.z.replace("t", String.valueOf(lastGenT))).getResult();
            double vx = bracketsCalculation.calculation(this.vx.replace("t", String.valueOf(lastGenT))).getResult();
            double vy = bracketsCalculation.calculation(this.vy.replace("t", String.valueOf(lastGenT))).getResult();
            double vz = bracketsCalculation.calculation(this.vz.replace("t", String.valueOf(lastGenT))).getResult();
            level.sendParticles(particle, x, y, z, 0, vx, vy, vz, Math.sqrt(vx * vx + vy * vy + vz * vz));
        }
        t += speed;
        return true;
    }
}
