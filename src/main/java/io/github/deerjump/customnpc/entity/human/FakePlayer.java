package io.github.deerjump.customnpc.entity.human;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftChatMessage;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.GenericAttributes;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_16_R1.ScoreboardTeam;
import net.minecraft.server.v1_16_R1.ScoreboardTeamBase.EnumNameTagVisibility;
import net.minecraft.server.v1_16_R1.World;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

public class FakePlayer extends EntityAbstract {

   // From EntityHuman
   public static DataWatcherObject<Float> EXTRA_HEARTS = DataWatcherRegistry.c.a(14);
   public static DataWatcherObject<Integer> SCORE = DataWatcherRegistry.b.a(15);
   public static DataWatcherObject<Byte> SKIN_PARTS = DataWatcherRegistry.a.a(16);
   public static DataWatcherObject<Byte> MAIN_HAND = DataWatcherRegistry.a.a(17);
   public static DataWatcherObject<NBTTagCompound> LEFT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(18);
   public static DataWatcherObject<NBTTagCompound> RIGHT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(19);

   private static final double MOVE_SPEED = 0.2;

   protected ScoreboardTeam team;
   private String simpleName;

   public FakePlayer(EntityTypes<FakePlayer> type, World world) {
      super(type, world);

      setName("New Npc");
      this.datawatcher.set(SKIN_PARTS, (byte) 127);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(MOVE_SPEED);
      
      goalSelector.a(2, new PathfinderGoalRandomLookaround(this));
      goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
      goalSelector.a(0, new PathfinderGoalRandomStroll(this, (double)2, 5));
      this.team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), getName());
      this.team.setNameTagVisibility(EnumNameTagVisibility.ALWAYS);
      
      sendPackets(new PacketPlayOutScoreboardTeam(this.team, 0));
      sendPackets(new PacketPlayOutScoreboardTeam(team, new ArrayList<String>(){{add(getName());}}, 3));

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

   @Override
   public String getName() {
      return this.simpleName;
   }

   public IChatBaseComponent getPrefix(){
      return this.team.getPrefix();
   }

   public IChatBaseComponent getSuffix(){
      return this.team.getSuffix();
   }

   @Override
   public void setName(String name) {
      if(name == null)
         return;
      if(name.trim().length() > 16)
         this.simpleName = name.split("(?<=\\G.{16})")[0]; //first 16 characters of name
      else
         this.simpleName = name;
      update();
   }

   public void setPrefix(String prefix){
      this.setPrefix(new ChatComponentText(prefix));
   }

   public void setPrefix(IChatBaseComponent prefix){
      this.team.setPrefix(prefix);
      updateTeam();
   }

   public void setSuffix(String suffix){
      this.setSuffix(new ChatComponentText(suffix));
   }

   public void setSuffix(IChatBaseComponent suffix){
      this.team.setSuffix(suffix);
      updateTeam();
   }

   @Override
   public void setCustomName(IChatBaseComponent name) {
   /*
      Override this to allow for nameTags to work
      Maybe I'll do something like
      if(config.get(nametag-enabled))
         super.setCustomName(name);      
   */
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

         skin = new Property("textures", texture, signature);
         updateProfile();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void update(){
      updateProfile();
      if(team != null)
         updateTeam();
   }

   public void updateProfile(){
      PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true);
      PacketPlayOutPlayerInfo playerInfo = getInfoPacket(EnumPlayerInfoAction.ADD_PLAYER);
      sendPackets(new PacketPlayOutEntityDestroy(this.getId()), playerInfo, getSpawnPacket(), metadata);

      resetCounter();
   }

   public void updateTeam(){
      sendPackets(new PacketPlayOutScoreboardTeam(this.team, 1),
                  new PacketPlayOutScoreboardTeam(this.team, 0),
                  new PacketPlayOutScoreboardTeam(team, new ArrayList<String>(){{add(getName());}}, 3));
   }

   @Override
   public void saveData(NBTTagCompound nbttagcompound) {
      super.saveData(nbttagcompound);
   }

   @Override
   public void loadData(NBTTagCompound nbttagcompound) {
      super.loadData(nbttagcompound);

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