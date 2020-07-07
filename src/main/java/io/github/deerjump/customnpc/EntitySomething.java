package io.github.deerjump.customnpc;

import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityTypes;

import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.World;

public class EntitySomething extends EntityCustom {
 
   // EntityHuman  
   public static DataWatcherObject<Float> EXTRA_HEARTS = DataWatcherRegistry.c.a(14);
   public static DataWatcherObject<Integer> SCORE = DataWatcherRegistry.b.a(15);
   public static DataWatcherObject<Byte> SKIN_PARTS = DataWatcherRegistry.a.a(16);
   public static DataWatcherObject<Byte> MAIN_HAND = DataWatcherRegistry.a.a(17);
   public static DataWatcherObject<NBTTagCompound> LEFT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(18);
   public static DataWatcherObject<NBTTagCompound> RIGHT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(19);

   public EntitySomething(EntityTypes<EntitySomething> type, World world) {
      super(type, world);

      goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      goalSelector.a(0, new PathfinderGoalRandomLookaround(this));
   
      setName("Fake man");
      setSkin("jbillyman");

      this.datawatcher.set(new DataWatcherObject<Byte>(16, DataWatcherRegistry.a), (byte)127);
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
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   protected void initDatawatcher() {
      super.initDatawatcher();

      this.datawatcher.register(EXTRA_HEARTS, 0.0f);
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