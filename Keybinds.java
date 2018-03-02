package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

import org.lwjgl.input.Keyboard;

public class Keybinds
{
    public static KeyBinding look_h;
    public static KeyBinding look_j;
    public static KeyBinding look_k;
    public static KeyBinding look_l;
    public static KeyBinding look_g;

    public static void register()
    {
        look_h = new KeyBinding("look_h", Keyboard.KEY_H, "mcaim");
        look_j = new KeyBinding("look_j", Keyboard.KEY_J, "mcaim");
        look_k = new KeyBinding("look_k", Keyboard.KEY_K, "mcaim");
        look_l = new KeyBinding("look_l", Keyboard.KEY_L, "mcaim");
        look_g = new KeyBinding("look_g", Keyboard.KEY_G, "mcaim");

        ClientRegistry.registerKeyBinding(look_h);
        ClientRegistry.registerKeyBinding(look_j);
        ClientRegistry.registerKeyBinding(look_k);
        ClientRegistry.registerKeyBinding(look_l);
        ClientRegistry.registerKeyBinding(look_g);
    }
}
