package cn.autoforged.zombie_infection_mod_1782878027.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class ZombieRangedBowAttackGoal extends Goal {
    private final Zombie zombie;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public ZombieRangedBowAttackGoal(Zombie zombie, double speedModifier, int attackInterval, float attackRadius) {
        this.zombie = zombie;
        this.speedModifier = speedModifier;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.zombie.getTarget();
        return target != null && target.isAlive() && isHoldingBow();
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.zombie.getNavigation().isDone()) && isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        this.zombie.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.zombie.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.zombie.stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.zombie.getTarget();
        if (target == null) return;

        double distSqr = this.zombie.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean hasLineOfSight = this.zombie.getSensing().hasLineOfSight(target);
        boolean hadLineOfSight = this.seeTime > 0;

        if (hasLineOfSight != hadLineOfSight) {
            this.seeTime = 0;
        }

        if (hasLineOfSight) {
            this.seeTime++;
        } else {
            this.seeTime--;
        }

        if (!(distSqr > this.attackRadiusSqr) && this.seeTime >= 20) {
            this.zombie.getNavigation().stop();
            this.strafingTime++;
        } else {
            this.zombie.getNavigation().moveTo(target, this.speedModifier);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if (this.zombie.getRandom().nextFloat() < 0.3) {
                this.strafingClockwise = !this.strafingClockwise;
            }
            if (this.zombie.getRandom().nextFloat() < 0.3) {
                this.strafingBackwards = !this.strafingBackwards;
            }
            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (distSqr > this.attackRadiusSqr * 0.75F) {
                this.strafingBackwards = false;
            } else if (distSqr < this.attackRadiusSqr * 0.25F) {
                this.strafingBackwards = true;
            }
            this.zombie.getMoveControl().strafe(
                    this.strafingBackwards ? -0.5F : 0.5F,
                    this.strafingClockwise ? 0.5F : -0.5F
            );
            this.zombie.lookAt(target, 30.0F, 30.0F);
        } else {
            this.zombie.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.zombie.isUsingItem()) {
            if (!hasLineOfSight && this.seeTime < -60) {
                this.zombie.stopUsingItem();
            } else if (hasLineOfSight) {
                int useTime = this.zombie.getTicksUsingItem();
                if (useTime >= 20) {
                    this.zombie.stopUsingItem();
                    performRangedAttack(target, BowItem.getPowerForTime(useTime));
                    this.attackTime = 20;
                }
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.zombie, item -> item instanceof BowItem);
            this.zombie.startUsingItem(hand);
        }
    }

    private void performRangedAttack(LivingEntity target, float power) {
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(this.zombie, item -> item instanceof BowItem);
        ItemStack bowStack = this.zombie.getItemInHand(hand);
        ItemStack arrowStack = this.zombie.getProjectile(bowStack);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this.zombie, arrowStack, power);

        if (bowStack.getItem() instanceof BowItem bowItem) {
            arrow = bowItem.customArrow(arrow);
        }

        double dx = target.getX() - this.zombie.getX();
        double dy = target.getY(0.3333333333333333) - arrow.getY();
        double dz = target.getZ() - this.zombie.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        arrow.shoot(dx, dy + horizontalDist * 0.2F, dz, 1.6F,
                (float) (14 - this.zombie.level().getDifficulty().getId() * 4));

        this.zombie.playSound(SoundEvents.SKELETON_SHOOT, 1.0F,
                1.0F / (this.zombie.getRandom().nextFloat() * 0.4F + 0.8F));

        this.zombie.level().addFreshEntity(arrow);
    }

    private boolean isHoldingBow() {
        return this.zombie.isHolding(is -> is.getItem() instanceof BowItem);
    }
}
