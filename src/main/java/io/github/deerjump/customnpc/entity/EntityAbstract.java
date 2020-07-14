package io.github.deerjump.customnpc.entity;

import static net.minecraft.server.v1_16_R1.IRegistry.ENTITY_TYPE;
import com.google.common.collect.ImmutableSet;
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
import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.Entity;
import net.minecraft.server.v1_16_R1.EntityCreature;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.IRegistry;
import net.minecraft.server.v1_16_R1.IWorldReader;
import net.minecraft.server.v1_16_R1.MinecraftKey;
import net.minecraft.server.v1_16_R1.Packet;
import net.minecraft.server.v1_16_R1.PacketDataSerializer;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata;
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityAbstract extends EntityCreature {
   private static final int ID_PLAYER = ENTITY_TYPE.a(EntityTypes.PLAYER);
   private static final Field FIELD_DATA;
   private static final Map<EntityTypes<?>, AttributeProvider> DEFAULT_ATTRIBUTES;

   // EntityLiving
   public static DataWatcherObject<Byte> HAND_STATE = DataWatcherRegistry.a.a(7);
   public static DataWatcherObject<Float> HEALTH = DataWatcherRegistry.c.a(8);
   public static DataWatcherObject<Integer> POTION_EFFECT_COLOR = DataWatcherRegistry.b.a(9);
   public static DataWatcherObject<Boolean> IS_POTION_AMBIENT = DataWatcherRegistry.i.a(10);
   public static DataWatcherObject<Integer> ARROWS = DataWatcherRegistry.b.a(11);
   public static DataWatcherObject<Integer> ABSORBTION_HEALTH = DataWatcherRegistry.b.a(12);
   public static DataWatcherObject<Optional<BlockPosition>> UNKNOWN = DataWatcherRegistry.m.a(13);
   
   // EntityInsentient
   public static DataWatcherObject<Byte> INSENTIENT = null;

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

   private int removeCounter = 10;
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

   public static <Entity extends EntityAbstract> EntityTypes<Entity> register(EntityTypes.b<Entity> entity, String name,
         EntityTypes<?> model) {
      EntityTypes<Entity> type = ENTITY_TYPE.a(ENTITY_TYPE.a(model), ResourceKey.a(IRegistry.n, MinecraftKey.a(name)),
            new EntityTypes<Entity>(entity, model.e(), true, model.b(), model.c(), model.d(), ImmutableSet.of(),
                  model.l(), model.getChunkRange(), model.getUpdateInterval()));
      DEFAULT_ATTRIBUTES.put(type, DEFAULT_ATTRIBUTES.get(model));
      DEFAULT_ATTRIBUTES.put(type, EntityInsentient.p().a());
      return type;
   }

   public static <Entity extends EntityAbstract> Entity spawn(EntityTypes<Entity> type, Location location) {
      final Entity entity = type.a(((CraftWorld) location.getWorld()).getHandle());
      entity.setPosition(location.getX(), location.getY(), location.getZ());
      entity.setYawPitch(location.getYaw(), location.getPitch());
      entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
      return entity;
   }

   protected EntityAbstract(EntityTypes<? extends EntityAbstract> type, World world) {
      super(type, world);
      this.profile = new GameProfile(getUniqueID(), getDisplayName().getText());
   }

   public void setPing(int ping) {
      this.ping = ping;
      if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER)
         return;
      final Packet<?> packet = info(EnumPlayerInfoAction.UPDATE_LATENCY);
      tracking.forEach(player -> {
         player.sendPacket(packet);
         player.sendPacket(new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true));
      });
   }

   public void setGamemode(EnumGamemode gamemode) {
      this.gamemode = gamemode;
      if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER)
         return;
      final Packet<?> packet = info(EnumPlayerInfoAction.UPDATE_GAME_MODE);
      tracking.forEach(player -> {
         player.sendPacket(packet);
         player.sendPacket(new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true));
      });
   }

   public void setProperty(Property property) {
      profile.getProperties().removeAll(property.getName());
      profile.getProperties().put(property.getName(), property);
      if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER)
         return;
      final Packet<?> packet = info(EnumPlayerInfoAction.ADD_PLAYER);
      tracking.forEach(player -> {
         player.sendPacket(packet);
         player.sendPacket(new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true));
      });
   }

   public void setName(String name) {
      setCustomName(new ChatComponentText(name));
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
      tracking.forEach(player -> {
         player.sendPacket(packet);
         player.sendPacket(new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true));
      });
   }

   @Override
   protected void initDatawatcher() {

         // From EntityLiving
         this.datawatcher.register(HAND_STATE, (byte) 0);
         this.datawatcher.register(POTION_EFFECT_COLOR, 0);
         this.datawatcher.register(IS_POTION_AMBIENT, false);
         this.datawatcher.register(ARROWS, 0);
         this.datawatcher.register(ABSORBTION_HEALTH, 0);
         this.datawatcher.register(HEALTH, 1.0F);
         this.datawatcher.register(UNKNOWN, Optional.empty());
      
         // From EntityInsentient
         if (ENTITY_TYPE.a(getEntityType()) != ID_PLAYER) {
            INSENTIENT = DataWatcherRegistry.a.a(14);
            this.datawatcher.register(INSENTIENT, (byte)0);
         }
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
            player.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true));
         } catch (Throwable reason) { throw new RuntimeException(reason); }
   }

   @Override
   public void tick() {
      if(ENTITY_TYPE.a(getEntityType()) == ID_PLAYER){
         if(removeCounter == 0)
            tracking.forEach(player -> player.sendPacket(info(EnumPlayerInfoAction.REMOVE_PLAYER)));
         else
            removeCounter--;
      }  
         super.tick();
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