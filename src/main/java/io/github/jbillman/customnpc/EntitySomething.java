package io.github.jbillman.customnpc;


import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.World;

public class EntitySomething extends EntityCustom {
 
   public EntitySomething(EntityTypes<EntitySomething> type, World world) {
      super(type, world);
      this.datawatcher.set(SKIN_PARTS,(byte)127);
      goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      goalSelector.a(0, new PathfinderGoalRandomLookaround(this));

      setName("jbillyman");
      setSkin("jbillyman");
      
   }
}