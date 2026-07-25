package cn.autoforged.zombie_infection_mod_1782878027.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.common.ToolActions;

import java.util.EnumSet;

public class ZombieShieldGoal extends Goal {
    private final Zombie zombie;
    private static final double SHIELD_RANGE = 10.0;

    public ZombieShieldGoal(Zombie zombie) {
        this.zombie = zombie;
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.zombie.getTarget();
        return target != null && target.isAlive() && hasShield() && hasNoBow();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    private boolean hasShield() {
        return this.zombie.getMainHandItem().canPerformAction(ToolActions.SHIELD_BLOCK) ||
                this.zombie.getOffhandItem().canPerformAction(ToolActions.SHIELD_BLOCK);
    }

    private boolean hasNoBow() {
        return !(this.zombie.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem);
    }

    private InteractionHand getShieldHand() {
        if (this.zombie.getMainHandItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            return InteractionHand.MAIN_HAND;
        }
        if (this.zombie.getOffhandItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    @Override
    public void start() {
        raiseShield();
    }

    @Override
    public void stop() {
        if (this.zombie.isUsingItem()) {
            this.zombie.stopUsingItem();
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.zombie.getTarget();
        if (target == null || !target.isAlive()) {
            stop();
            return;
        }

        double distSqr = this.zombie.distanceToSqr(target);
        if (distSqr > SHIELD_RANGE * SHIELD_RANGE || !this.zombie.getSensing().hasLineOfSight(target)) {
            stop();
            return;
        }

        if (!this.zombie.isUsingItem()) {
            raiseShield();
        }
    }

    private void raiseShield() {
        if (this.zombie.isUsingItem()) return;
        InteractionHand hand = getShieldHand();
        if (hand != null) {
            this.zombie.startUsingItem(hand);
        }
    }
}
