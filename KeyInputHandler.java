package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;


import net.minecraft.util.text.TextComponentString;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class KeyInputHandler
{
  private static float sensitivity = 2;

  private Boolean h_down = false;
  private Boolean j_down = false;
  private Boolean k_down = false;
  private Boolean l_down = false;

  private Entity tracking = null;
  private int tickCnt = 0;


  public KeyInputHandler () {}


  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event)
  {
    if (Keybinds.look_g.isPressed()) {
      this.lockTarget ();
      return;
    }

    this.h_down = Keybinds.look_h.isPressed();
    this.j_down = Keybinds.look_j.isPressed();
    this.k_down = Keybinds.look_k.isPressed();
    this.l_down = Keybinds.look_l.isPressed();

    if (this.h_down || this.j_down || this.k_down || this.l_down)
      this.unlockTarget();
  }

  @SubscribeEvent
  public void playerTickEvent(PlayerTickEvent event)
  {
    if (tickCnt++ < 100) return; // Otherwise Minecraft might crash on startup :/

    EntityPlayer p = Minecraft.getMinecraft().player;

    if (this.tracking != null) {
      this.lookAt (this.tracking, p);
    }

    if (this.h_down) p.rotationYaw   -= sensitivity;
    if (this.j_down) p.rotationPitch += sensitivity;
    if (this.k_down) p.rotationPitch -= sensitivity;
    if (this.l_down) p.rotationYaw   += sensitivity;
  }

  // Start tracking entities when the player damages them
  // turns out this is annoying
  // @SubscribeEvent
  // public void onHurtEvent(LivingHurtEvent event)
  // {
  //   Entity entity = event.getEntityLiving();
  //   DamageSource damageSource = event.getSource();
  //
  //   if(entity == null) return;
  //   if(damageSource.isFireDamage()) return;
  //   if(!isCreature(entity)) return;
  //
  //   tracking = entity;
  // }

  // Stop tracking entities when they die
  @SubscribeEvent
  public void onDeathEvent(LivingDeathEvent event)
  {
    if(event.getEntityLiving() == tracking) unlockTarget();
  }


  private void lockTarget () {
    Minecraft mc   = Minecraft.getMinecraft();
    EntityPlayer p = mc.player;

    double x = p.posX;
    double y = p.posY;
    double z = p.posZ;
    double max_dist = 10000000; // TODO: Massively reduce this?
    Entity closest = null;

    for (Entity e : mc.world.loadedEntityList) {
      if (e == this.tracking) continue;
      if (!isCreature(e)) continue;
      double _x = e.posX;
      double _y = e.posY;
      double _z = e.posZ;

      if (x == _x && y == _y && z == _z) continue;
      double dist  = Math.sqrt( Math.pow(_x - x, 2) + Math.pow(_y - y, 2) + Math.pow(_z - z, 2) );
      double lDist = lookDist (e, p);
      dist = dist + lDist;

      if (dist > max_dist) continue;

      max_dist = dist;
      closest = e;
    }

    if (closest == null) return;

    this.tracking = closest;
    this.lookAt (closest, p);
  }
  private void unlockTarget () {
    this.tracking = null;
  }

  public static Double lookDist(double px, double py, double pz, EntityPlayer me) {
    double dirx = me.posX - px;
    double diry = me.posY - py;
    double dirz = me.posZ - pz;

    double len = Math.sqrt(dirx*dirx + diry*diry + dirz*dirz);

    dirx /= len;
    diry /= len;
    dirz /= len;

    double pitch = Math.asin(diry);
    double yaw = Math.atan2(dirz, dirx);

    //to degree
    pitch *= 180.0 / Math.PI;
    yaw   *= 180.0 / Math.PI;

    yaw += 90f;

    return Math.abs(pitch - me.rotationPitch) + Math.abs(yaw - me.rotationYaw);
  }
  public static Double lookDist(Entity e, EntityPlayer p) {
    return lookDist (e.posX, e.posY-p.getEyeHeight()+e.getEyeHeight(), e.posZ, p);
  }
  public static void lookAt(double px, double py, double pz, EntityPlayer me) {
    double dirx = me.posX - px;
    double diry = me.posY - py;
    double dirz = me.posZ - pz;

    double len = Math.sqrt(dirx*dirx + diry*diry + dirz*dirz);

    dirx /= len;
    diry /= len;
    dirz /= len;

    double pitch = Math.asin(diry);
    double yaw = Math.atan2(dirz, dirx);

    //to degree
    pitch *= 180.0 / Math.PI;
    yaw   *= 180.0 / Math.PI;

    yaw += 90f;

    me.rotationPitch = (float) pitch;
    me.rotationYaw   = (float) yaw;
  }
  public static void lookAt(Entity e, EntityPlayer p) {
    lookAt (e.posX, e.posY-p.getEyeHeight()+e.getEyeHeight(), e.posZ, p);
  }

  public static Boolean isCreature(Entity e) {
    return ( e.isCreatureType(EnumCreatureType.MONSTER, true)
          || e.isCreatureType(EnumCreatureType.CREATURE, true)
          || e.isCreatureType(EnumCreatureType.AMBIENT, true)
          || e.isCreatureType(EnumCreatureType.WATER_CREATURE, true)
           );
  }

  private static void debug (String msg) {
    Minecraft.getMinecraft().player.sendStatusMessage (new TextComponentString (msg), false);
  }

}
