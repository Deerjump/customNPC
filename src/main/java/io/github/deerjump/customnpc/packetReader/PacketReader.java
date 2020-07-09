package io.github.deerjump.customnpc.packetReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.brigadier.arguments.BoolArgumentType;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import io.github.deerjump.customnpc.entity.human.FakePlayer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R1.DataWatcher.Item;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

public class PacketReader {
   Channel channel;
   public static Map<UUID, Channel> channels = new HashMap<UUID, Channel>();

   public void inject(Player player) {
      CraftPlayer craftPlayer = (CraftPlayer) player;
      channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
      channels.put(player.getUniqueId(), channel);

      if(channel.pipeline().get("PacketInjector") != null)
         return;

      channel.pipeline().addBefore("packet_handler", "custom_handler", new ChannelDuplexHandler(){
         
         @Override
         public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
         try{
            msg = checkIncoming(player, msg);
         } catch(Exception e){
         System.out.println("[checkIncoming]Error reading packet: " + msg.getClass().getSimpleName()+ "->"+ e);
         }

          
            if(msg != null)
               super.channelRead(ctx, msg);   
         }

         @Override
         public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

            try{
               msg = checkOutgoing(player, msg);
            } catch(Exception e){
               System.out.println("[checkOutgoing]Error reading packet: " + msg.getClass().getSimpleName() + "->" + e);
            }

            if(msg != null)
               super.write(ctx, msg, promise);
         }
      });
   }

   public void uninject(Player player) {
      channel = channels.get(player.getUniqueId());
      if(channel.pipeline().get("PacketInjector") != null)
         channel.pipeline().remove("PacketInjector");
      channels.remove(player.getUniqueId());
   }

   public Object checkIncoming(Player sender, Object packet){
     
      // System.out.println("Incoming: " + packet.getClass().getSimpleName());


      return packet;
   }

   public Object checkOutgoing(Player sender, Object packet){
      // System.out.println("Out: " + packet.getClass().getSimpleName());

      if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutRelEntityMove")){
         Field[] fields = packet.getClass().getFields();
         System.out.println(fields[0]);
         for(int i = 0; i < fields.length; i++)
            System.out.println("RelEntityMove." + fields[i].getName());
      }
         // readRelEntityMove(packet, sender);
      if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityLook")){
         Field[] fields = packet.getClass().getFields();
         System.out.println(fields[0]);
         for(int i = 0; i < fields.length; i++)
            System.out.println("EntityLook." + fields[i].getName());
      }
         // readEntityLook(packet, sender);
      // if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutEntityHeadRotation"))
      //    readEntityHeadRotation(packet, sender);
      return packet;
   }

   private Object getValue(Object instance, String name){
      Object result = null;
      
      for(Field field : instance.getClass().getFields())
         System.out.println(field.getName());

      try{
         Field field = instance.getClass().getDeclaredField(name);
         field.setAccessible(true);
         result = field.get(instance);
         field.setAccessible(false);
      } catch(Exception e) {
         e.printStackTrace();
   }

   return result;
   }

   private void readEntityHeadRotation(Object packet, Player sender){
      int id = (int)getValue(packet, "a");
      byte yaw = (byte)getValue(packet, "b");
      for(Entity entity: Bukkit.getWorlds().get(0).getEntities()){
         if(entity.getEntityId() == id){
            System.out.println("------------EntityLook: " + entity.getClass().getSimpleName() + "->" + entity.getType() +"-------------");
            System.out.println("yaw: " + yaw);
         }
      }
   }

   private void readRelEntityMove(Object packet, Player sender){
      int id = (int)getValue(packet, "a");
      short deltaX = (short)getValue(packet,"b");
      short deltaY = (short)getValue(packet,"c");
      short deltaZ = (short)getValue(packet,"d");
      boolean onGround = (boolean)getValue(packet,"g");

      for(Entity entity: Bukkit.getWorlds().get(0).getEntities()){
         if(entity.getEntityId() == id){
            System.out.println("------------RelEntityMove: " + entity.getClass().getSimpleName() + "->" + entity.getType() + "-------------");
            System.out.println("deltaX: " + deltaX);
            System.out.println("deltaY: " + deltaY);
            System.out.println("deltaZ: " + deltaZ);
            System.out.println("onGround: " + onGround);
         }
      }
   }

   private void readEntityLook(Object packet, Player sender){
      int id = (int)getValue(packet, "a"); 
      byte yaw = (byte)getValue(packet, "e"); 
      byte pitch = (byte)getValue(packet, "f"); 
      boolean onGround = (boolean)getValue(packet, "g");

      for(Entity entity: Bukkit.getWorlds().get(0).getEntities()){
         if(entity.getEntityId() == id){
            System.out.println("------------EntityLook: " + entity.getClass().getSimpleName() + "->" + entity.getType() + "-------------");
            System.out.println("yaw: " + yaw);
            System.out.println("pitch: " + pitch);
            System.out.println("onGround: " + onGround);
         }
      }
   }

   private void readEntityMetadata(Object packet, Player sender){
      List<Entity> entityList = Bukkit.getServer().getWorlds().get(0).getEntities();

      Entity packetOwner = null;
      FakePlayer fakePlayer = null;
      int ownerId = (int) getValue(packet, "a");
      
      for(Entity entity : entityList){
         if(entity.getEntityId() == ownerId)
            packetOwner = entity;
      }

      if(packetOwner instanceof EntityAbstract)
         fakePlayer = (FakePlayer)packetOwner;

      if(fakePlayer != null)
         System.out.println("----------------" + packet.getClass().getSimpleName() + " : " + fakePlayer.getClass().getSimpleName() + "-------------------------");
      else
         System.out.println("----------------" + packet.getClass().getSimpleName() + " : " + packetOwner.getClass().getSimpleName() + "-------------------------");
      PacketPlayOutEntityMetadata metadata = (PacketPlayOutEntityMetadata)packet;
      
      List<Item<?>> itemList = (List<Item<?>>)getValue(packet, "b");
      
      //DataWatcherObject -> Format is index : value ie.(16: 127)
      for(Item<?> item : itemList)
         System.out.println(String.format("%s : %s", item.a().a(), item.b()));

   }

   private void readPlayerInfo(Object packet, Player sender){
      EnumPlayerInfoAction action = (EnumPlayerInfoAction)getValue(packet, "a");
      List<PacketPlayOutPlayerInfo.PlayerInfoData> infoList = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
      FakePlayer fakePlayer = null;

      List<Entity> entityList = Bukkit.getServer().getWorlds().get(0).getEntities();

      for(Entity entity : entityList){
         if(entity instanceof EntityAbstract)
            fakePlayer = (FakePlayer)entity;
      }

      if(fakePlayer != null)
         System.out.println("----------------" + packet.getClass().getSimpleName() + " : " + fakePlayer.getClass().getSimpleName() + "-------------------------");
      else
         System.out.println("----------------" + packet.getClass().getSimpleName() + " : " + sender.getName() + "-------------------------");
      
      for(PacketPlayOutPlayerInfo.PlayerInfoData item : infoList){
         System.out.println("<--------------------------------");
         System.out.println("Action:" + action.toString()); //PacketPlayOutPlayerInfo.EnumPlayerInfoAction
         System.out.println("Ping:" + item.b()); //PlayerInfoData ping
         System.out.println("GameMode:" + item.c()); //PlayerInfoData gamemode
         System.out.println("GameProfile:" + item.a()); //PlayerInfoData gameProfile
         System.out.println("Name:" + item.d()); //PlayerInfoData displayName
         System.out.println("-------------------------------->");
      }
   }
   
}