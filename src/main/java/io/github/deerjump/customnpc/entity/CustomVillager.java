package io.github.deerjump.customnpc.entity;

import io.github.deerjump.npclib.v1_16_R2.NpcBase;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.World;

public class CustomVillager extends NpcBase {
   //A blank npc is this easy to create
   public CustomVillager(EntityTypes<CustomVillager> type, World world) {
      super(type, world);
   }
}