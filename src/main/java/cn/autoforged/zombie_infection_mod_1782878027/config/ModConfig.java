package cn.autoforged.zombie_infection_mod_1782878027.config;

import cn.autoforged.zombie_infection_mod_1782878027.ZombieInfectionMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZombieInfectionMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue MAX_INFECTABLE_GENERATIONS;
    public static final ForgeConfigSpec.IntValue INFECTION_DURATION;
    public static final ForgeConfigSpec.BooleanValue INHERIT_EQUIPMENT;
    public static final ForgeConfigSpec.DoubleValue INHERIT_RATIO;
    public static final ForgeConfigSpec.DoubleValue EQUIPMENT_DROP_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SELF_GROWTH_HEALTH;
    public static final ForgeConfigSpec.DoubleValue SELF_GROWTH_ATTACK;
    public static final ForgeConfigSpec.DoubleValue REFLECT_RATIO;
    public static final ForgeConfigSpec.DoubleValue LIGHTNING_TRANSCEND_CHANCE;
    public static final ForgeConfigSpec.DoubleValue LIGHTNING_TRANSCEND_HEALTH_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue LIGHTNING_TRANSCEND_ATTACK_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue LIGHTNING_TRANSCEND_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue LIGHTNING_TRANSCEND_ARMOR_BONUS;
    public static final ForgeConfigSpec.DoubleValue LIGHTNING_TRANSCEND_SELF_DAMAGE_RATIO;
    public static final ForgeConfigSpec.IntValue LIGHTNING_TRANSCEND_WEAKNESS_DURATION;
    public static final ForgeConfigSpec.IntValue LIGHTNING_TRANSCEND_WEAKNESS_AMPLIFIER;

    static {
        BUILDER.comment("感染传播设置").push("infection");

        MAX_INFECTABLE_GENERATIONS = BUILDER
                .comment("可传播感染的代数上限。",
                        "0 = 无法传播（被感染生物死亡后正常死亡）。",
                        "1 = 仅第0代僵尸可感染。",
                        "2 = 第0代和第1代僵尸可感染。",
                        "默认值: 2")
                .defineInRange("maxInfectableGenerations", 2, 0, 100);

        INFECTION_DURATION = BUILDER
                .comment("感染效果的持续时间（单位:tick，20tick = 1秒）。",
                        "默认值: 1200（60秒）")
                .defineInRange("infectionDuration", 1200, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("属性继承设置").push("inheritance");

        INHERIT_EQUIPMENT = BUILDER
                .comment("感染生物死亡后生成的僵尸是否继承装备。",
                        "默认值: true")
                .define("inheritEquipment", true);

        INHERIT_RATIO = BUILDER
                .comment("从感染生物继承属性的比例（0.0 ~ 1.0）。",
                        "默认值: 0.7")
                .defineInRange("inheritRatio", 0.7, 0.0, 1.0);

        EQUIPMENT_DROP_CHANCE = BUILDER
                .comment("继承的装备被击杀时的掉落概率（0.0 ~ 1.0）。",
                        "默认值: 0.1（10%）")
                .defineInRange("equipmentDropChance", 0.1, 0.0, 1.0);

        BUILDER.pop();

        BUILDER.comment("僵尸击杀成长设置").push("selfGrowth");

        SELF_GROWTH_HEALTH = BUILDER
                .comment("僵尸击杀非僵尸生物时增加的生命值。",
                        "默认值: 1.0")
                .defineInRange("selfGrowthHealth", 1.0, 0.0, 1000.0);

        SELF_GROWTH_ATTACK = BUILDER
                .comment("僵尸击杀非僵尸生物时增加的攻击力。",
                        "默认值: 1.0")
                .defineInRange("selfGrowthAttack", 1.0, 0.0, 1000.0);

        BUILDER.pop();

        BUILDER.comment("死亡反伤设置").push("reflect");

        REFLECT_RATIO = BUILDER
                .comment("击杀僵尸时反弹给击杀者的伤害比例（0.0 ~ 1.0）。",
                        "默认值: 0.2")
                .defineInRange("reflectRatio", 0.2, 0.0, 1.0);

        BUILDER.pop();

        BUILDER.comment("僵尸渡劫（闪电）设置").push("lightningTranscend");

        LIGHTNING_TRANSCEND_CHANCE = BUILDER
                .comment("僵尸被闪电击中时成功渡劫的概率（0.0 ~ 1.0）。",
                        "默认值: 0.1（10%）")
                .defineInRange("lightningTranscendChance", 0.1, 0.0, 1.0);

        LIGHTNING_TRANSCEND_HEALTH_MULTIPLIER = BUILDER
                .comment("渡劫后生命值的倍率。",
                        "默认值: 2.0")
                .defineInRange("lightningTranscendHealthMultiplier", 2.0, 1.0, 100.0);

        LIGHTNING_TRANSCEND_ATTACK_MULTIPLIER = BUILDER
                .comment("渡劫后攻击力的倍率。",
                        "默认值: 2.0")
                .defineInRange("lightningTranscendAttackMultiplier", 2.0, 1.0, 100.0);

        LIGHTNING_TRANSCEND_SPEED_MULTIPLIER = BUILDER
                .comment("渡劫后移动速度的倍率。",
                        "默认值: 2.0")
                .defineInRange("lightningTranscendSpeedMultiplier", 2.0, 1.0, 100.0);

        LIGHTNING_TRANSCEND_ARMOR_BONUS = BUILDER
                .comment("渡劫后额外增加的护甲值。",
                        "默认值: 3.9")
                .defineInRange("lightningTranscendArmorBonus", 3.9, 0.0, 100.0);

        LIGHTNING_TRANSCEND_SELF_DAMAGE_RATIO = BUILDER
                .comment("渡劫时对自身造成的最大生命值百分比伤害（0.0 ~ 1.0）。",
                        "默认值: 0.3（30%）")
                .defineInRange("lightningTranscendSelfDamageRatio", 0.3, 0.0, 1.0);

        LIGHTNING_TRANSCEND_WEAKNESS_DURATION = BUILDER
                .comment("渡劫后虚弱效果的持续时间（单位:tick）。",
                        "默认值: 400（20秒）")
                .defineInRange("lightningTranscendWeaknessDuration", 400, 1, Integer.MAX_VALUE);

        LIGHTNING_TRANSCEND_WEAKNESS_AMPLIFIER = BUILDER
                .comment("渡劫后虚弱效果的等级（0 = 虚弱I，1 = 虚弱II，依此类推）。",
                        "默认值: 1（虚弱II）")
                .defineInRange("lightningTranscendWeaknessAmplifier", 1, 0, 255);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
