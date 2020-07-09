package io.github.deerjump.customnpc.entity.villager;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.World;

public class CustomVillager extends EntityAbstract {

   public CustomVillager(EntityTypes<CustomVillager> type, World world) {
      super(type, world);
      
      goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
      // goalSelector.a(1, new PathfinderGoalRandomLookaround(this));

      setName("fakeVillager");
   }
    
   @Override
   protected void initDatawatcher() {
      super.initDatawatcher();
   }
}