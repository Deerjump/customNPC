package io.github.deerjump.customnpc.event;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import io.github.deerjump.customnpc.entity.human.FakePlayer;
import net.minecraft.server.v1_16_R1.Entity;

public class RightClickListener implements Listener {
   HashMap<String, EntityAbstract> mapEditingNPC = new HashMap<>(); 
   HashMap<String, String> mapEditingDetail = new HashMap<>(); 

   @EventHandler
   public void onRightClick(PlayerInteractAtEntityEvent event){


      Player player = event.getPlayer();
      Entity nmsEntity = ((CraftEntity)event.getRightClicked()).getHandle();
      if(event.getHand().toString().equalsIgnoreCase("hand"))
         return;
      if(!(nmsEntity instanceof FakePlayer))
         return;

      Bukkit.getPluginManager().callEvent(new PlayerInteractAtNPCEvent(player, (EntityAbstract)nmsEntity));
   }
   
   @EventHandler
   public void onNPCInteract(PlayerInteractAtNPCEvent event){
      Player player = event.getPlayer();
      EntityAbstract npc = event.getClickedEntity();

      if( player.isSneaking() && mapEditingNPC.containsKey(player.getName())){
         player.sendMessage(ChatColor.RED + "Editing Canceled!");
         mapEditingNPC.remove(player.getName());
      } else {
         mapEditingNPC.put(player.getName(), npc);
         player.sendMessage(ChatColor.GOLD + "What would you like to edit?\n" + ChatColor.GREEN + "(name, prefix, suffix, skin)");
      } 
   }

   @EventHandler(ignoreCancelled = true)
   public void editDetail(AsyncPlayerChatEvent event){
      Player player = event.getPlayer();

      if(!mapEditingDetail.containsKey(player.getName()))
         return;

      System.out.println("Editing Details");

      EntityAbstract npc = mapEditingNPC.get(player.getName());
      System.out.println(event.getMessage());
      if( mapEditingDetail.get(player.getName()).equalsIgnoreCase("name")  || !(npc instanceof FakePlayer)){
         System.out.println("Setting name");
         npc.setName(event.getMessage());
         mapEditingDetail.remove(player.getName());
         event.setCancelled(true);
         return;
      }

      FakePlayer playerNpc = (FakePlayer) npc;

      switch(mapEditingDetail.get(player.getName())){
         case "prefix": 
            System.out.println("Setting prefix to: " + event.getMessage());
            playerNpc.setPrefix(event.getMessage());     
            System.out.println("removing player from mapEditingDetail");
            mapEditingDetail.remove(player.getName());
            break;
         case "suffix":
            System.out.println("Setting suffix to: " + event.getMessage());
            playerNpc.setSuffix(event.getMessage());
            mapEditingDetail.remove(player.getName());
            break;
         case "skin":
            System.out.println("Setting skin to: " + event.getMessage());
            playerNpc.setSkin(event.getMessage());
            mapEditingDetail.remove(player.getName());
            break;
      }

      event.setCancelled(true);
   }

   @EventHandler
   public void selectDetail(AsyncPlayerChatEvent event){
      Player player = event.getPlayer();

      if(!mapEditingNPC.containsKey(player.getName()) || mapEditingDetail.containsKey(player.getName()))
         return;

      switch(event.getMessage()){
         case "name":
         case "prefix":
         case "suffix":
         case "skin":
            player.sendMessage(ChatColor.GOLD + "What would you like to change " + ChatColor.GREEN + event.getMessage() + ChatColor.GOLD +  " to?");
            mapEditingDetail.put(player.getName(), event.getMessage());
            event.setCancelled(true);
            break;
      }
   }

}