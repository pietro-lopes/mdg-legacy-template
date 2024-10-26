package com.example.examplemod.mixin.core.main;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.mixin.SomeExtension;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements SomeExtension {
    @Inject(method = "run", at = @At("HEAD"))
    private void example$sendMessageLogAtStart(CallbackInfo ci){
        ExampleMod.LOGGER.debug("Example Mixin from Example Mod triggered!");
    }
}
