package cn.autoforged.zombie_infection_mod_1782878027.event;

import java.util.List;

import cn.autoforged.zombie_infection_mod_1782878027.ModEffects;
import cn.autoforged.zombie_infection_mod_1782878027.ZombieInfectionMod;
import cn.autoforged.zombie_infection_mod_1782878027.ai.ZombieFleeExplosionGoal;
import cn.autoforged.zombie_infection_mod_1782878027.ai.ZombieRangedBowAttackGoal;
import cn.autoforged.zombie_infection_mod_1782878027.ai.ZombieShieldGoal;
import cn.autoforged.zombie_infection_mod_1782878027.config.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZombieInfectionMod.MOD_ID)
public class InfectionEvents {

    private static final String TAG_CHAIN_GEN = "ZombieInfectionChainGen";
    private static final String TAG_BONUS_HEALTH = "ZombieInfectionBonusHealth";
    private static final String TAG_BONUS_ATTACK = "ZombieInfectionBonusAttack";
    private static final String TAG_BONUS_SPEED = "ZombieInfectionBonusSpeed";
    private static final String TAG_INFECTOR_TYPE = "ZombieInfectorType";
    private static final double VANILLA_ZOMBIE_SPEED = 0.23;

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (zombie instanceof ZombifiedPiglin) return;

        zombie.setCanPickUpLoot(true);
        zombie.goalSelector.addGoal(0, new ZombieFleeExplosionGoal(zombie, 1.5, 10.0));

        zombie.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                zombie, LivingEntity.class, 10, true, false,
                target -> {
                    if (target instanceof Zombie || target instanceof Creeper || target instanceof Zoglin) return false;
                    if (target instanceof AbstractFish || target instanceof Bat) return false;
                    if (target instanceof ZombieHorse) return false;
                    if (target instanceof Turtle) return false;
                    return target.isAlive();
                }
        ));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        var level = event.getEntity().level();
        if (level.isClientSide()) return;

        var source = event.getSource().getEntity();
        if (!(source instanceof Zombie attacker)) return;
        if (attacker instanceof ZombifiedPiglin) return;

        var target = event.getEntity();
        if (target instanceof Zombie || target instanceof Creeper || target instanceof Zoglin) return;

        CompoundTag attackerData = attacker.getPersistentData();
        int attackerGen = attackerData.getInt(TAG_CHAIN_GEN);
        if (attackerGen >= ModConfig.MAX_INFECTABLE_GENERATIONS.get()) return;

        target.addEffect(new MobEffectInstance(ModEffects.INFECTION.get(), ModConfig.INFECTION_DURATION.get(), 0, false, true, true));

        CompoundTag targetData = target.getPersistentData();
        if (!targetData.contains(TAG_CHAIN_GEN)) {
            targetData.putInt(TAG_CHAIN_GEN, attackerGen);
            targetData.putDouble(TAG_BONUS_HEALTH, attackerData.getDouble(TAG_BONUS_HEALTH));
            targetData.putDouble(TAG_BONUS_ATTACK, attackerData.getDouble(TAG_BONUS_ATTACK));
            targetData.putDouble(TAG_BONUS_SPEED, attackerData.getDouble(TAG_BONUS_SPEED));

            String infectorType = BuiltInRegistries.ENTITY_TYPE.getKey(attacker.getType()).toString();
            targetData.putString(TAG_INFECTOR_TYPE, infectorType);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        var entity = event.getEntity();
        var level = entity.level();
        if (level.isClientSide()) return;

        handleInfectionDeath(entity, level);
        handleZombieSelfGrowth(event);
        handleZombieDeathReflect(event);
        handleDeathInheritance(entity, level);
    }

    private static void handleInfectionDeath(LivingEntity entity, net.minecraft.world.level.Level level) {
        if (!entity.hasEffect(ModEffects.INFECTION.get())) return;
        if (entity instanceof ZombifiedPiglin || entity instanceof Zoglin) return;

        CompoundTag data = entity.getPersistentData();
        int parentGen = data.contains(TAG_CHAIN_GEN) ? data.getInt(TAG_CHAIN_GEN) : 0;
        if (parentGen >= ModConfig.MAX_INFECTABLE_GENERATIONS.get()) return;

        double inheritRatio = ModConfig.INHERIT_RATIO.get();
        double parentBonusHealth = data.contains(TAG_BONUS_HEALTH) ? data.getDouble(TAG_BONUS_HEALTH) : 0.0;
        double parentBonusAttack = data.contains(TAG_BONUS_ATTACK) ? data.getDouble(TAG_BONUS_ATTACK) : 0.0;
        double parentBonusSpeed = data.contains(TAG_BONUS_SPEED) ? data.getDouble(TAG_BONUS_SPEED) : 0.0;

        String infectorTypeStr = data.getString(TAG_INFECTOR_TYPE);
        Zombie spawned = createZombieByType(level, infectorTypeStr);
        if (spawned == null) return;

        spawned.setPos(entity.getX(), entity.getY(), entity.getZ());

        AttributeInstance healthAttr = spawned.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackAttr = spawned.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speedAttr = spawned.getAttribute(Attributes.MOVEMENT_SPEED);

        AttributeInstance entityHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance entityAttackAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance entitySpeedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (healthAttr != null && entityHealthAttr != null) {
            double newHealth = entityHealthAttr.getBaseValue() * inheritRatio + parentBonusHealth * inheritRatio;
            healthAttr.setBaseValue(newHealth);
            spawned.setHealth((float) newHealth);
        }

        if (attackAttr != null && entityAttackAttr != null) {
            double newAttack = entityAttackAttr.getBaseValue() * inheritRatio + parentBonusAttack * inheritRatio;
            attackAttr.setBaseValue(newAttack);
        }

        if (speedAttr != null && entitySpeedAttr != null) {
            double entitySpeed = entitySpeedAttr.getBaseValue();
            if (entitySpeed > VANILLA_ZOMBIE_SPEED) {
                double newSpeed = entitySpeed * inheritRatio + parentBonusSpeed * inheritRatio;
                speedAttr.setBaseValue(newSpeed);
            } else {
                speedAttr.setBaseValue(VANILLA_ZOMBIE_SPEED);
            }
        }

        if (ModConfig.INHERIT_EQUIPMENT.get()) {
            boolean hasBow = false;
            boolean hasShield = false;
            float dropChance = ModConfig.EQUIPMENT_DROP_CHANCE.get().floatValue();
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    spawned.setItemSlot(slot, stack.copy());
                    spawned.setDropChance(slot, dropChance);
                    if (stack.getItem() instanceof BowItem) hasBow = true;
                    if (stack.canPerformAction(ToolActions.SHIELD_BLOCK)) hasShield = true;
                }
            }
            if (hasBow) {
                spawned.goalSelector.addGoal(1, new ZombieRangedBowAttackGoal(spawned, 1.0, 20, 15.0F));
            } else if (hasShield) {
                spawned.goalSelector.addGoal(2, new ZombieShieldGoal(spawned));
            }
        }

        String mobName = entity.getName().getString();
        if (!mobName.contains("僵尸")) {
            spawned.setCustomName(Component.literal(mobName + "僵尸"));
        } else {
            spawned.setCustomName(Component.literal(mobName));
        }
        spawned.setCustomNameVisible(false);

        CompoundTag spawnData = spawned.getPersistentData();
        spawnData.putInt(TAG_CHAIN_GEN, parentGen + 1);
        spawnData.putDouble(TAG_BONUS_HEALTH, parentBonusHealth * inheritRatio);
        spawnData.putDouble(TAG_BONUS_ATTACK, parentBonusAttack * inheritRatio);
        spawnData.putDouble(TAG_BONUS_SPEED, parentBonusSpeed * inheritRatio);

        level.addFreshEntity(spawned);
    }

    private static Zombie createZombieByType(net.minecraft.world.level.Level level, String infectorTypeStr) {
        if (infectorTypeStr.isEmpty()) {
            return EntityType.ZOMBIE.create(level);
        }
        ResourceLocation typeKey = ResourceLocation.tryParse(infectorTypeStr);
        if (typeKey == null) return EntityType.ZOMBIE.create(level);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(typeKey);
        if (type == null || !Zombie.class.isAssignableFrom(type.getBaseClass())) {
            return EntityType.ZOMBIE.create(level);
        }
        Entity e = type.create(level);
        if (!(e instanceof Zombie)) return EntityType.ZOMBIE.create(level);
        return (Zombie) e;
    }

    private static void handleZombieDeathReflect(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (zombie instanceof ZombifiedPiglin) return;
        var source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity killer)) return;

        double reflectRatio = ModConfig.REFLECT_RATIO.get();
        if (reflectRatio <= 0) return;
        AttributeInstance attackAttr = killer.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr == null) return;

        float reflectDamage = (float) (attackAttr.getValue() * reflectRatio);
        if (reflectDamage <= 0) return;

        killer.hurt(zombie.level().damageSources().thorns(zombie), reflectDamage);
    }

    private static void handleZombieSelfGrowth(LivingDeathEvent event) {
        var source = event.getSource().getEntity();
        if (!(source instanceof Zombie zombie)) return;
        if (zombie instanceof ZombifiedPiglin) return;
        if (event.getEntity() instanceof Zombie) return;

        double selfGrowthHealth = ModConfig.SELF_GROWTH_HEALTH.get();
        double selfGrowthAttack = ModConfig.SELF_GROWTH_ATTACK.get();
        if (selfGrowthHealth <= 0 && selfGrowthAttack <= 0) return;

        CompoundTag data = zombie.getPersistentData();
        double bonusHealth = data.getDouble(TAG_BONUS_HEALTH) + selfGrowthHealth;
        double bonusAttack = data.getDouble(TAG_BONUS_ATTACK) + selfGrowthAttack;
        data.putDouble(TAG_BONUS_HEALTH, bonusHealth);
        data.putDouble(TAG_BONUS_ATTACK, bonusAttack);

        AttributeInstance healthAttr = zombie.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null && selfGrowthHealth > 0) {
            healthAttr.setBaseValue(healthAttr.getBaseValue() + selfGrowthHealth);
            zombie.setHealth(zombie.getMaxHealth());
        }

        AttributeInstance attackAttr = zombie.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null && selfGrowthAttack > 0) {
            attackAttr.setBaseValue(attackAttr.getBaseValue() + selfGrowthAttack);
        }
    }

    private static void handleDeathInheritance(LivingEntity entity, net.minecraft.world.level.Level level) {
        if (!(entity instanceof Zombie zombie)) return;
        if (zombie instanceof ZombifiedPiglin) return;

        List<Zombie> nearby = level.getEntitiesOfClass(Zombie.class,
                zombie.getBoundingBox().inflate(20.0),
                z -> z != zombie && !(z instanceof ZombifiedPiglin) && z.isAlive());
        if (nearby.isEmpty()) return;

        Zombie heir = nearby.get(0);
        double closestDist = zombie.distanceToSqr(heir);
        for (Zombie z : nearby) {
            double d = zombie.distanceToSqr(z);
            if (d < closestDist) {
                closestDist = d;
                heir = z;
            }
        }

        AttributeInstance healthAttr = zombie.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackAttr = zombie.getAttribute(Attributes.ATTACK_DAMAGE);

        if (healthAttr != null) {
            double inheritHealth = healthAttr.getBaseValue() * 0.3;
            AttributeInstance heirHealth = heir.getAttribute(Attributes.MAX_HEALTH);
            if (heirHealth != null) {
                heirHealth.setBaseValue(heirHealth.getBaseValue() + inheritHealth);
                heir.setHealth(heir.getMaxHealth());
            }
        }

        if (attackAttr != null) {
            double inheritAttack = attackAttr.getBaseValue() * 0.3;
            AttributeInstance heirAttack = heir.getAttribute(Attributes.ATTACK_DAMAGE);
            if (heirAttack != null) {
                heirAttack.setBaseValue(heirAttack.getBaseValue() + inheritAttack);
            }
        }
    }

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (zombie instanceof ZombifiedPiglin) return;
        var level = zombie.level();
        if (level.isClientSide()) return;

        double chance = ModConfig.LIGHTNING_TRANSCEND_CHANCE.get();
        if (chance <= 0 || zombie.getRandom().nextDouble() >= chance) return;

        AttributeInstance healthAttr = zombie.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackAttr = zombie.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speedAttr = zombie.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance armorAttr = zombie.getAttribute(Attributes.ARMOR);

        double healthMultiplier = ModConfig.LIGHTNING_TRANSCEND_HEALTH_MULTIPLIER.get();
        double attackMultiplier = ModConfig.LIGHTNING_TRANSCEND_ATTACK_MULTIPLIER.get();
        double speedMultiplier = ModConfig.LIGHTNING_TRANSCEND_SPEED_MULTIPLIER.get();
        double armorBonus = ModConfig.LIGHTNING_TRANSCEND_ARMOR_BONUS.get();
        double selfDamageRatio = ModConfig.LIGHTNING_TRANSCEND_SELF_DAMAGE_RATIO.get();
        int weaknessDuration = ModConfig.LIGHTNING_TRANSCEND_WEAKNESS_DURATION.get();
        int weaknessAmplifier = ModConfig.LIGHTNING_TRANSCEND_WEAKNESS_AMPLIFIER.get();

        double oldHealthBase = healthAttr != null ? healthAttr.getBaseValue() : 20.0;
        double oldHealth = zombie.getHealth();

        if (healthAttr != null) {
            healthAttr.setBaseValue(oldHealthBase * healthMultiplier);
            zombie.setHealth((float) (oldHealth * healthMultiplier));
        }
        if (attackAttr != null) {
            attackAttr.setBaseValue(attackAttr.getBaseValue() * attackMultiplier);
        }
        if (speedAttr != null) {
            speedAttr.setBaseValue(speedAttr.getBaseValue() * speedMultiplier);
        }
        if (armorAttr != null) {
            armorAttr.setBaseValue(armorAttr.getBaseValue() + armorBonus);
        }

        if (selfDamageRatio > 0) {
            float selfDamage = (float) (zombie.getMaxHealth() * selfDamageRatio);
            zombie.hurt(zombie.damageSources().magic(), selfDamage);
        }
        if (weaknessDuration > 0) {
            zombie.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, weaknessAmplifier, false, true, true));
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        HitResult hitResult = event.getRayTraceResult();
        if (!(hitResult instanceof EntityHitResult entityHit)) return;

        Projectile projectile = event.getProjectile();
        if (!(projectile.getOwner() instanceof Zombie shooter)) return;
        if (shooter instanceof ZombifiedPiglin) return;

        if (!(entityHit.getEntity() instanceof Zombie hitZombie)) return;
        if (hitZombie instanceof ZombifiedPiglin) return;

        event.setCanceled(true);
    }
}
