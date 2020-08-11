package io.github.deerjump.customnpc.entity.human;

import static net.minecraft.server.v1_16_R1.IRegistry.ENTITY_TYPE;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EnumGamemode;
import net.minecraft.server.v1_16_R1.GenericAttributes;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.Packet;
import net.minecraft.server.v1_16_R1.PacketDataSerializer;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_16_R1.World;

public class FakePlayer extends EntityAbstract {
   private static final Field FIELD_DATA;

   // From EntityHuman
   public static final DataWatcherObject<Float> EXTRA_HEARTS = DataWatcherRegistry.c.a(14);
   public static final DataWatcherObject<Integer> SCORE = DataWatcherRegistry.b.a(15);
   public static final DataWatcherObject<Byte> SKIN_PARTS = DataWatcherRegistry.a.a(16);
   public static final DataWatcherObject<Byte> MAIN_HAND = DataWatcherRegistry.a.a(17);
   public static final DataWatcherObject<NBTTagCompound> LEFT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(18);
   public static final DataWatcherObject<NBTTagCompound> RIGHT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(19);
   private static final double MOVE_SPEED = 0.2;
   private static final int REMOVE_DELAY = 1;
   
   private int ping = 1;
   private int removeCounter;
   protected Property skin;


   static {
      try {
         (FIELD_DATA = PacketPlayOutPlayerInfo.class.getDeclaredField("b")).setAccessible(true);
      } catch (Throwable reason) {
         throw new RuntimeException(reason);
      }
   }

   public FakePlayer(EntityTypes<FakePlayer> type, World world) {
      super(type, world);

      setName("New Npc");
      this.datawatcher.set(SKIN_PARTS, (byte) 127);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(MOVE_SPEED);

      goalSelector.a(2, new PathfinderGoalRandomLookaround(this));
      goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      goalSelector.a(0, new PathfinderGoalRandomStroll(this, (double) 2, 5));
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
   public void tick() {
      this.yaw = getHeadRotation();
 
      if(removeCounter == 0){
         sendPackets(getInfoPacket(EnumPlayerInfoAction.REMOVE_PLAYER));
         removeCounter--;
      }else if (removeCounter >= 0)
         removeCounter--;
 
      super.tick();
   }

   @Override
   public void setName(String name) {
      if (name.length() > 16)
         throw new IllegalArgumentException("Name must be 16 characters or less! Provided: " + name.length());
      else
         setCustomName(new ChatComponentText(name));
      updateProfile();
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

         setSkin(texture, signature);
         updateProfile();
      } catch (Exception e) {
         e.printStackTrace();
         return;
      }
   }

   private void setSkin(String texture, String signature) {
      this.skin = new Property("textures", texture, signature);
   }

   public void updateProfile() {
      PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true);
      PacketPlayOutPlayerInfo playerInfo = getInfoPacket(EnumPlayerInfoAction.ADD_PLAYER);
      sendPackets(new PacketPlayOutEntityDestroy(this.getId()), playerInfo, getSpawnPacket(), metadata);

      resetCounter();
   }

   @Override
   @OverridingMethodsMustInvokeSuper
   public void saveData(NBTTagCompound nbttagcompound) {
      super.saveData(nbttagcompound);
      if(skin != null){
         nbttagcompound.setString("skinTexture", skin.getValue());
         nbttagcompound.setString("skinSignature", skin.getSignature());
      }
   }

   @Override
   @OverridingMethodsMustInvokeSuper
   public void loadData(NBTTagCompound nbttagcompound) {
      super.loadData(nbttagcompound);
      if(nbttagcompound.hasKey("skinTexture")  && nbttagcompound.hasKey("skinSignature")){
         String skinTexture = nbttagcompound.getString("skinTexture");
         String skinSignature = nbttagcompound.getString("skinSignature");
         
         setSkin(skinTexture, skinSignature);
      }

   }

   @Override public void b(EntityPlayer player) {
      if (ENTITY_TYPE.a(getEntityType()) == ID_PLAYER) try {
         updateProfile();                    
      } catch (Throwable reason) { throw new RuntimeException(reason); }
   }

   protected GameProfile getPseudoProfile(){
      GameProfile profile = new GameProfile(getUniqueID(), getName());
      
      if(skin != null)
         profile.getProperties().put(skin.getName(), skin);
      return profile;
   }   

   public Packet<?> getSpawnPacket(){
      try{
         final ByteBuf buffer = Unpooled.buffer();
         final PacketDataSerializer data = new PacketDataSerializer(buffer);
         data.d(getId());
         data.a(getUniqueID());
         data.writeDouble(locX());
         data.writeDouble(locY());
         data.writeDouble(locZ());
         data.writeByte((byte)((int)(yaw * 256.0F / 360.0F)));
         data.writeByte((byte)((int)(pitch * 256.0F / 360.0F)));
         final Packet<?> packet = new PacketPlayOutNamedEntitySpawn();
         packet.a(data); buffer.release();
         return packet;
      } catch (Throwable reason) {throw new RuntimeException(reason);}
   }

   protected PacketPlayOutPlayerInfo getInfoPacket(EnumPlayerInfoAction action) {
      try {
         if(action == EnumPlayerInfoAction.ADD_PLAYER)
            resetCounter();
         final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(action);
         ((List<PacketPlayOutPlayerInfo.PlayerInfoData>) FIELD_DATA.get(packet)).add(packet.new PlayerInfoData(
            getPseudoProfile(), ping, EnumGamemode.SURVIVAL, getCustomName()
         ));
         return packet;
      } catch (Throwable reason) { throw new RuntimeException(reason); }
   }

   @Override
   public Packet<?> O() {
      return getInfoPacket(EnumPlayerInfoAction.ADD_PLAYER); 
   }

   @Override public void c(EntityPlayer player) {
      player.playerConnection.sendPacket(getInfoPacket(EnumPlayerInfoAction.REMOVE_PLAYER));
   }

   protected void resetCounter(){
      removeCounter = REMOVE_DELAY;
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