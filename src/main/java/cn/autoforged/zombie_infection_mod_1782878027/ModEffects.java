package cn.autoforged.zombie_infection_mod_1782878027;

import cn.autoforged.zombie_infection_mod_1782878027.effect.InfectionEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ZombieInfectionMod.MOD_ID);

    public static final RegistryObject<MobEffect> INFECTION =
            MOB_EFFECTS.register("infection", InfectionEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
