package io.github.deerjump.customnpc.entity;

import io.github.deerjump.playernpcs.HumanBase;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.World;

public class PlayerNPC extends HumanBase{
    
   public PlayerNPC(EntityTypes<PlayerNPC> type, World world){
      super(type, world);
      this.setInvulnerable(true);
      // this.setGamemode(EnumGamemode.CREATIVE);
   }
}