package cn.autoforged.zombie_infection_mod_1782878027.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ZombieFleeExplosionGoal extends Goal {
    private final Zombie zombie;
    private final double speedModifier;
    private final double searchRange;
    private Entity threat;
    private Vec3 fleePos;

    public ZombieFleeExplosionGoal(Zombie zombie, double speedModifier, double searchRange) {
        this.zombie = zombie;
        this.speedModifier = speedModifier;
        this.searchRange = searchRange;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        this.threat = findNearestThreat();
        if (this.threat == null) return false;
        this.fleePos = findFleePosition();
        return this.fleePos != null;
    }

    @Override
    public void start() {
        if (this.fleePos != null) {
            this.zombie.getNavigation().moveTo(this.fleePos.x, this.fleePos.y, this.fleePos.z, this.speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.threat == null || !this.threat.isAlive()) return false;
        if (this.zombie.distanceToSqr(this.threat) > this.searchRange * this.searchRange * 4) return false;
        return !this.zombie.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.threat = null;
        this.fleePos = null;
        this.zombie.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.threat != null && this.zombie.distanceToSqr(this.threat) < this.searchRange * this.searchRange * 2) {
            Vec3 newFlee = findFleePosition();
            if (newFlee != null) {
                this.zombie.getNavigation().moveTo(newFlee.x, newFlee.y, newFlee.z, this.speedModifier);
            }
        }
    }

    private Entity findNearestThreat() {
        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;

        var creepers = this.zombie.level().getEntitiesOfClass(Creeper.class,
                this.zombie.getBoundingBox().inflate(this.searchRange),
                c -> c.getSwellDir() > 0);
        for (Creeper c : creepers) {
            double d = this.zombie.distanceToSqr(c);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = c;
            }
        }

        var tnts = this.zombie.level().getEntitiesOfClass(PrimedTnt.class,
                this.zombie.getBoundingBox().inflate(this.searchRange));
        for (PrimedTnt t : tnts) {
            double d = this.zombie.distanceToSqr(t);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = t;
            }
        }
        return nearest;
    }

    private Vec3 findFleePosition() {
        if (this.threat == null) return null;
        return DefaultRandomPos.getPosAway(this.zombie, (int) this.searchRange, 7, this.threat.position());
    }
}
