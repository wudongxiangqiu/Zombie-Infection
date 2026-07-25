package cn.autoforged.zombie_infection_mod_1782878027;

import cn.autoforged.zombie_infection_mod_1782878027.config.ModConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ZombieInfectionMod.MOD_ID)
public class ZombieInfectionMod {
    public static final String MOD_ID = "zombie_infection_mod_1782878027";

    public ZombieInfectionMod() {
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEffects.register(modEventBus);
    }
}
