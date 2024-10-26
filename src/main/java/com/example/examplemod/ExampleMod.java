package com.example.examplemod;

import com.mojang.logging.LogUtils;
import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ExampleMod.MOD_ID)
public final class ExampleMod {
	public static final String MOD_ID = "examplemod";
	public static final Logger LOGGER = LogUtils.getLogger();

	@SuppressWarnings("removal")
	public ExampleMod(/*FMLJavaModLoadingContext modContext*/) {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		final IEventBus gameBus = MinecraftForge.EVENT_BUS;
		modBus.addListener(this::onCommonSetup);
	}

	public void onCommonSetup(FMLCommonSetupEvent event){
		// Testing Jarjar
		PerlinNoiseGenerator perlinCosine = PerlinNoiseGenerator.newBuilder().setSeed(3301).setInterpolation(Interpolation.COSINE).build();
		event.enqueueWork(() -> {
			ExampleMod.LOGGER.info("Noise value: {}", perlinCosine.evaluateNoise(0.5f, 0.5f));
		});
	}
}
