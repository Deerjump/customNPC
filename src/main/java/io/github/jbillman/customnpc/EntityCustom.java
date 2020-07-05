package io.github.jbillman.customnpc;

import static net.minecraft.server.v1_16_R1.IRegistry.ENTITY_TYPE;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.AttributeDefaults;
import net.minecraft.server.v1_16_R1.AttributeProvider;
import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.DataWatcher;
import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.Entity;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.IRegistry;
import net.minecraft.server.v1_16_R1.MinecraftKey;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.Packet;
import net.minecraft.server.v1_16_R1.PacketDataSerializer;
import net.minecraft.server.v1_16_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R1.PlayerConnection;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R1.EnumGamemode;
import net.minecraft.server.v1_16_R1.ResourceKey;
import net.minecraft.server.v1_16_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityCustom extends EntityInsentient {
   private static final int ID_PLAYER = ENTITY_TYPE.a(EntityTypes.PLAYER);
   private static final Field FIELD_DATA;
   private static final Map<EntityTypes<?>, AttributeProvider> DEFAULT_ATTRIBUTES;

   // EntityLiving
   public static final DataWatcherObject<Byte> HAND_STATE = DataWatcherRegistry.a.a(7);
   public static final DataWatcherObject<Float> HEALTH = DataWatcherRegistry.c.a(8);
   public static final DataWatcherObject<Integer> POTION_EFFECT_COLOR = DataWatcherRegistry.b.a(9);
   public static final DataWatcherObject<Boolean> IS_POTION_AMBIENT = DataWatcherRegistry.i.a(10);
   public static final DataWatcherObject<Integer> ARROWS = DataWatcherRegistry.b.a(11);
   public static final DataWatcherObject<Integer> ABSORBTION_HEALTH = DataWatcherRegistry.b.a(12);
   public static final DataWatcherObject<Optional<BlockPosition>> UNKNOWN = DataWatcherRegistry.m.a(13);
   
   // EntityInsentient
   public static final DataWatcherObject<Byte> INSENTIENT = DataWatcherRegistry.a.a(14);
   // EntityHuman  
   public static final DataWatcherObject<Integer> SCORE = DataWatcherRegistry.b.a(15);
   public static final DataWatcherObject<Byte> SKIN_PARTS = DataWatcherRegistry.a.a(16);
   public static final DataWatcherObject<Byte> MAIN_HAND = DataWatcherRegistry.a.a(17);
   public static final DataWatcherObject<NBTTagCompound> LEFT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(18);
   public static final DataWatcherObject<NBTTagCompound> RIGHT_SHOULDER_ENTITY = DataWatcherRegistry.p.a(19);



   static {
      try {
         final Field modifiers = Field.class.getDeclaredField("modifiers");
         modifiers.setAccessible(true);
         final Field field = AttributeDefaults.class.getDeclaredField("b");
         modifiers.setInt(field, modifiers.getInt(field) & ~Modifier.FINAL);
         field.setAccessible(true);
         DEFAULT_ATTRIBUTES = new HashMap((Map<EntityTypes<?>, AttributeProvider>) field.get(null));
         field.set(null, DEFAULT_ATTRIBUTES);
      } catch (Throwable reason) {
         throw new RuntimeException(reason);
      }
   }

   private final List<PlayerConnection> tracking = new ArrayList<>();
   private GameProfile profile;
   private EnumGamemode gamemode = EnumGamemode.SURVIVAL;
   private int ping = 0;

   static {
      try {
         (FIELD_DATA = PacketPlayOutPlayerInfo.class.getDeclaredField("b")).setAccessible(true);
      } catch (Throwable reason) {
         throw new RuntimeException(reason);
      }
   }

   public static <Entity extends EntityCustom> EntityTypes<Entity> register(EntityTypes.b<Entity> entity, String name,
         EntityTypes<?> model) {
      System.out.println("EntityCustom register");
      EntityTypes<Entity> type = ENTITY_TYPE.a(ENTITY_TYPE.a(model), ResourceKey.a(IRegistry.n, MinecraftKey.a(name)),
            new EntityTypes<Entity>(entity, model.e(), true, model.b(), model.c(), model.d(), ImmutableSet.of(),
                  model.l(), model.getChunkRange(), model.getUpdateInterval()));
      DEFAULT_ATTRIBUTES.put(type, DEFAULT_ATTRIBUTES.get(model));
      DEFAULT_ATTRIBUTES.put(type, EntityInsentient.p().a());
      return type;
   }

   public static <Entity extends EntityCustom> Entity spawn(EntityTypes<Entity> type, Location location) {
      System.out.println("EntityCustom spawn()");
      final Entity entity = type.a(((CraftWorld) location.getWorld()).getHandle());
      entity.setPosition(location.getX(), location.getY(), location.getZ());
      entity.setYawPitch(location.getYaw(), location.getPitch());
      entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
      DataWatcher watcher = entity.getDataWatcher();
      watcher.set(DataWatcherRegistry.a.a(16), (byte) 127);
      return entity;
   }

   protected EntityCustom(EntityTypes<? extends EntityCustom> type, World world) {
      super(type, world);
      System.out.println("EntityCustom Constructor");
      this.profile = new GameProfile(getUniqueID(), getDisplayName().getText());
   }

   public void setPing(int ping) {
      this.ping = ping;
      if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER)
         return;
      final Packet<?> packet = info(EnumPlayerInfoAction.UPDATE_LATENCY);
      tracking.forEach(player -> player.sendPacket(packet));
   }

   public void setGamemode(EnumGamemode gamemode) {
      this.gamemode = gamemode;
      if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER)
         return;
      final Packet<?> packet = info(EnumPlayerInfoAction.UPDATE_GAME_MODE);
      tracking.forEach(player -> player.sendPacket(packet));
   }

   public void setProperty(Property property) {
      profile.getProperties().removeAll(property.getName());
      profile.getProperties().put(property.getName(), property);
      if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER)
         return;
      final Packet<?> packet = info(EnumPlayerInfoAction.ADD_PLAYER);
      tracking.forEach(player -> player.sendPacket(packet));
   }

   public void setName(String name) {
      setCustomName(new ChatComponentText(name));
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
   public void setCustomName(IChatBaseComponent name) {
      super.setCustomName(name);
      if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER)
         return;
      final PropertyMap properties = profile.getProperties();
      profile = new GameProfile(getUniqueID(), getDisplayName().getText());
      profile.getProperties().putAll(properties);
      final Packet<?> packet = info(EnumPlayerInfoAction.ADD_PLAYER);
      tracking.forEach(player -> player.sendPacket(packet));
   }

   @Override
   protected void initDatawatcher() {
      
      //From EntityLiving
      this.datawatcher.register(HAND_STATE, (byte)0);
      this.datawatcher.register(POTION_EFFECT_COLOR, 0);
      this.datawatcher.register(IS_POTION_AMBIENT, false);
      this.datawatcher.register(ARROWS, 0);
      this.datawatcher.register(ABSORBTION_HEALTH, 0);
      this.datawatcher.register(HEALTH, 1.0F);
      this.datawatcher.register(UNKNOWN, Optional.empty());

      //From EntityInsentient
      this.datawatcher.register(INSENTIENT, (byte)0);   
      this.datawatcher.markDirty(INSENTIENT);
      //From EntityHuman    
      this.datawatcher.register(SCORE, 0);      
      this.datawatcher.register(SKIN_PARTS, (byte) 0);      
      this.datawatcher.register(MAIN_HAND, (byte) 1);     
      this.datawatcher.markDirty(MAIN_HAND); 
      this.datawatcher.register(LEFT_SHOULDER_ENTITY, new NBTTagCompound());      
      this.datawatcher.register(RIGHT_SHOULDER_ENTITY, new NBTTagCompound());     
   
   }

   @Override public void b(EntityPlayer player) {
         tracking.add(player.playerConnection);
         final ByteBuf buffer = Unpooled.buffer();
         if (ENTITY_TYPE.a(getEntityType()) == ID_PLAYER) try {
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
            player.playerConnection.sendPacket(packet);

         } catch (Throwable reason) { throw new RuntimeException(reason); }
   }
   
   @Override public void c(EntityPlayer player) {
         tracking.remove(player.playerConnection);
         if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER) return;
         player.playerConnection.sendPacket(info(EnumPlayerInfoAction.REMOVE_PLAYER));
   }

   @Override public Packet<?> O() {
         if (ENTITY_TYPE.a(getEntityType()) == ID_PLAYER)
            return info(EnumPlayerInfoAction.ADD_PLAYER); 
         return new PacketPlayOutSpawnEntityLiving(this);
   }
   
   @SuppressWarnings("unchecked")
   private PacketPlayOutPlayerInfo info(EnumPlayerInfoAction action) {
         try {
            final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(action);
            ((List<PacketPlayOutPlayerInfo.PlayerInfoData>) FIELD_DATA.get(packet)).add(packet.new PlayerInfoData(
               profile, ping, gamemode, getDisplayName()
            ));
            return packet;
         } catch (Throwable reason) { throw new RuntimeException(reason); }
   }
}