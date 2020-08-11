package io.github.deerjump.customnpc.entity;

import static net.minecraft.server.v1_16_R1.IRegistry.ENTITY_TYPE;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.server.v1_16_R1.AttributeDefaults;
import net.minecraft.server.v1_16_R1.AttributeProvider;
import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;
import net.minecraft.server.v1_16_R1.EntityCreature;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.EntityPose;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.IRegistry;
import net.minecraft.server.v1_16_R1.MinecraftKey;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.Packet;
import net.minecraft.server.v1_16_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R1.PlayerConnection;
import net.minecraft.server.v1_16_R1.RegistryID;
import net.minecraft.server.v1_16_R1.ResourceKey;
import net.minecraft.server.v1_16_R1.ScoreboardTeam;
import net.minecraft.server.v1_16_R1.World;
//This is just here to suppress the warning in the static init
@SuppressWarnings("unchecked")
public class EntityAbstract extends EntityCreature {
   private static final Map<EntityTypes<?>, AttributeProvider> DEFAULT_ATTRIBUTES;
   protected static final int ID_PLAYER = ENTITY_TYPE.a(EntityTypes.PLAYER);

   // Entity
   public static DataWatcherObject<Byte> EFFECTS = DataWatcherRegistry.a.a(0);
   public static DataWatcherObject<Integer> AIR = DataWatcherRegistry.b.a(1);
   public static DataWatcherObject<Optional<IChatBaseComponent>> CUSTOM_NAME = DataWatcherRegistry.f.a(2);
   public static DataWatcherObject<Boolean> CUSTOM_NAME_VISIBLE = DataWatcherRegistry.i.a(3);
   public static DataWatcherObject<Boolean> IS_SILENT = DataWatcherRegistry.i.a(4);
   public static DataWatcherObject<Boolean> NO_GRAVITY = DataWatcherRegistry.i.a(5);
   public static DataWatcherObject<EntityPose> POSE = DataWatcherRegistry.s.a(6);

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
         DEFAULT_ATTRIBUTES = new HashMap<>((Map<EntityTypes<?>, AttributeProvider>) field.get(null));
         field.set(null, DEFAULT_ATTRIBUTES);
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

   public static void unregister(EntityTypes<? extends EntityAbstract> entity){
      try{
         Field field = ENTITY_TYPE.getClass().getSuperclass().getDeclaredField("c");
         field.setAccessible(true);
         
         BiMap<MinecraftKey,Object> map = (BiMap<MinecraftKey, Object>) field.get(ENTITY_TYPE);
         
         while(map.inverse().containsKey(entity)){
            System.out.println("Removing " + entity + " from c");
            map.inverse().remove(entity);
         }
         field.setAccessible(false);
      } catch(Exception e){
         e.printStackTrace();
      }

      try{
         Field field = ENTITY_TYPE.getClass().getSuperclass().getDeclaredField("bb");
         field.setAccessible(true);
         BiMap<ResourceKey<Object>, Object> map = (BiMap<ResourceKey<Object>, Object>) field.get(ENTITY_TYPE);
         
         while(map.inverse().containsKey(entity)){
            System.out.println("Removing " + entity + " from bb");
            map.inverse().remove(entity);
         }
      } catch(Exception e){
         e.printStackTrace();
      }

      try{
         Field field = ENTITY_TYPE.getClass().getSuperclass().getDeclaredField("b");
         field.setAccessible(true);
         RegistryID<Object> registry = (RegistryID<Object>) field.get(ENTITY_TYPE);
         Field field2 = registry.getClass().getDeclaredField("b");
         field2.setAccessible(true);
         Object[] objects = (Object[])field2.get(registry);
         for(Object object : objects){
            if(object != null && object.equals(entity)){
               System.out.println("Removing " + object + "from b.b");
               object = null;
            }
         }
         Field field3 = registry.getClass().getDeclaredField("d");
         field3.setAccessible(true);
         Object[] objects2 = (Object[])field2.get(registry);
         for(Object object : objects2){
            if(object != null && object.equals(entity)){
               System.out.println("Removing " + object + "from b.d");
               object = null;
            }
         }
      } catch(Exception e){
         e.printStackTrace();
      }
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
      setPersistent();
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

   @Override
   @OverridingMethodsMustInvokeSuper
   public void saveData(NBTTagCompound nbttagcompound) {
      super.saveData(nbttagcompound);
      
   }

   @Override
   @OverridingMethodsMustInvokeSuper
   public void loadData(NBTTagCompound nbttagcompound) {
      super.loadData(nbttagcompound); 
      
   }

   @Override
   public String getName(){
      if(getCustomName() != null)
         return getCustomName().getText();
      return " ";
   }

   public void setName(String name) {
      setCustomName(new ChatComponentText(name));
   }

   @Override
   public void setCustomName(IChatBaseComponent name) {
      super.setCustomName(name);
   }

   @Override public Packet<?> O() {         
      return new PacketPlayOutSpawnEntityLiving(this);
   }

   protected void sendPackets(Packet<?>...packets){
      Bukkit.getOnlinePlayers().forEach(player -> {
         PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
         for(Packet<?> packet : packets){
            connection.sendPacket(packet);
         }
      });
   }
}