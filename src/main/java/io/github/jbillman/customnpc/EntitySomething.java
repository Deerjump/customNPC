package io.github.jbillman.customnpc;

import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;


import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityTypes;

import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.World;

public class EntitySomething extends EntityCustom {
 
   public EntitySomething(EntityTypes<EntitySomething> type, World world) {
      super(type, world);

      goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      goalSelector.a(0, new PathfinderGoalRandomLookaround(this));

      this.datawatcher.set(SKIN_PARTS,(byte)127);
   
      setName("jbillyman");
      setSkin("jbillyman");
      
   }

   public void setSkin(String name) {
      try {
         URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
         InputStreamReader reader = new InputStreamReader(url.openStream());
         String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

         URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
         InputStreamReader reader2 = new InputStreamReader(url2.openStream());

         JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray()
               .get(0).getAsJsonObject();
         String texture = property.get("value").getAsString();
         String signature = property.get("signature").getAsString();

         setProperty(new Property("textures", texture, signature));

         return;
      } catch (Exception e) {
         e.printStackTrace();
         return;
      }
   }

   @Override
   protected void initDatawatcher() {
      super.initDatawatcher();

      this.datawatcher.register(EXTRA_HEARTS, 0);
      this.datawatcher.register(SCORE, 0);      
      this.datawatcher.register(SKIN_PARTS, (byte) 0);      
      this.datawatcher.register(MAIN_HAND, (byte) 1);     
      this.datawatcher.register(LEFT_SHOULDER_ENTITY, new NBTTagCompound());      
      this.datawatcher.register(RIGHT_SHOULDER_ENTITY, new NBTTagCompound());    
   }

   @Override
   public boolean isNoAI() {
      return false;
   }
   
   @Override
   public void setNoAI(boolean flag) {
      return;
   }

   @Override
   public boolean isAggressive() {
      return false;
   }

   @Override
   public void setAggressive(boolean flag) {
      return;
   }

   @Override
   public boolean isLeftHanded() {
      return false;
   }

   @Override
   public void setLeftHanded(boolean flag) {
      return;
   }
   
}