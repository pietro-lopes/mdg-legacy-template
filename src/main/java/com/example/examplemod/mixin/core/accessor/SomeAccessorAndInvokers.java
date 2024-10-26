package com.example.examplemod.mixin.core.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface SomeAccessorAndInvokers {
    @Accessor("fps")
    int example$getFps();

    @Invoker("getFramerateLimit")
    int example$getFramerateLimit();
}
