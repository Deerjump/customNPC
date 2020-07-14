package io.github.deerjump.customnpc.entity.human;

import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import io.github.deerjump.customnpc.entity.EntityAbstract;
import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.PathfinderGoalDoorInteract;
import net.minecraft.server.v1_16_R1.PathfinderGoalGotoTarget;
import net.minecraft.server.v1_16_R1.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_16_R1.World;

public class FakePlayer extends EntityAbstract {
 
   // From EntityHuman  
   public static DataWatcherObject<Float> EXTRA_HEARTS = DataWatcherRegistry.c.a(14);
   public static DataWatcherObject<Integer> SCORE = DataWatcherRegistry.b.a(15);
   public static DataWatcherObject<Byte> SKIN_PARTS = DataWatcherRegistry.a.a(16);
   public static DataWatcherObject<Byte> MAIN_HAND = DataWatcherRegistry.a.a(17);
   public static DataWatcherObject<NBTTagCompound> LEFT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(18);
   public static DataWatcherObject<NBTTagCompound> RIGHT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(19);

   public FakePlayer(EntityTypes<FakePlayer> type, World world) {
      super(type, world);
      this.datawatcher.set(SKIN_PARTS, (byte)127);
      
      goalSelector.a(2, new PathfinderGoalRandomLookaround(this));
      goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));

   
      setName("Fake man");
      setSkin("jbillyman");

      // ItemStack itemStack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.DIAMOND_SHOVEL));
      // setSlot(EnumItemSlot.MAINHAND, itemStack);
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

   @Override protected void initDatawatcher() {
      super.initDatawatcher();

      this.datawatcher.register(EXTRA_HEARTS, 0.0f);
      this.datawatcher.register(SCORE, 0);      
      this.datawatcher.register(SKIN_PARTS, (byte) 0);      
      this.datawatcher.register(MAIN_HAND, (byte) 1);     
      this.datawatcher.register(LEFT_SHOULDER_ENTITY, new NBTTagCompound());      
      this.datawatcher.register(RIGHT_SHOULDER_ENTITY, new NBTTagCompound());    
   }

   @Override
   public void tick() {
      this.yaw = getHeadRotation();
      super.tick();
   }
   

   @Override public boolean isNoAI() {
      return false;
   }
   
   @Override public void setNoAI(boolean flag) {
      return;
   }

   @Override public boolean isAggressive() {
      return false;
   }

   @Override public void setAggressive(boolean flag) {
      return;
   }

   @Override public boolean isLeftHanded() {
      return false;
   }

   @Override public void setLeftHanded(boolean flag) {
      return;
   }
}