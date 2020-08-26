package io.github.deerjump.customnpc.event;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import io.github.deerjump.playernpcs.BaseHuman;
import io.github.deerjump.playernpcs.BaseNpc;


public class RightClickListener implements Listener {
   HashMap<String, BaseHuman> mapEditingNPC = new HashMap<>(); 
   HashMap<String, String> mapEditingDetail = new HashMap<>(); 

   @EventHandler
   public void onNPCInteract(PlayerInteractEntityEvent event){
      
      Player player = event.getPlayer();
      Entity entity = event.getRightClicked();

      if(!(entity instanceof BaseHuman) && entity instanceof BaseHuman){
         System.out.print("You're not a player npc!");
         ((BaseNpc)entity).setName("Not a player npc!");
         return;
      }

      BaseHuman npc = (BaseHuman)entity;

      if(player.isSneaking() && mapEditingNPC.containsKey(player.getName())){
         player.sendMessage(ChatColor.RED + "Editing Canceled!");
         mapEditingNPC.remove(player.getName());
      } else if(player.isSneaking()) {
         mapEditingNPC.put(player.getName(), npc);
         player.sendMessage("-----------------------------\n" +
                           ChatColor.GOLD + "What would you like to edit?\n" + 
                           ChatColor.GREEN + "(name, skin)\n" + 
                           ChatColor.RED + "<Right click while sneaking to cancel!>\n" + 
                           ChatColor.RESET + "-----------------------------");
      } 
   }

   @EventHandler(ignoreCancelled = true)
   public void editDetail(AsyncPlayerChatEvent event){
      Player player = event.getPlayer();

      if(!mapEditingDetail.containsKey(player.getName()))
         return;

      // System.out.println("Editing Details");

      BaseHuman npc = mapEditingNPC.get(player.getName());
      if( mapEditingDetail.get(player.getName()).equalsIgnoreCase("name")){
         // System.out.println("Setting name");
         try{
            player.sendMessage(ChatColor.GOLD + "Setting name to: " + ChatColor.GREEN + event.getMessage());
            npc.setName(event.getMessage());
            mapEditingDetail.remove(player.getName());
         } catch (IllegalArgumentException e){
            player.sendMessage(ChatColor.RED + "This name is too long! Must be " + ChatColor.GOLD + "16" 
                              + ChatColor.RED + " Characters or less. \nProvided(" + ChatColor.GOLD + event.getMessage().length() + ChatColor.RED + ")" );         
         }
         event.setCancelled(true);
         return;
      }

      

      switch(mapEditingDetail.get(player.getName())){
         case "skin":
            // System.out.println("Setting skin to: " + event.getMessage());
            player.sendMessage(ChatColor.GOLD + "Setting skin to: " + ChatColor.GREEN + event.getMessage());
            npc.setSkin(event.getMessage());
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
         case "skin":
            player.sendMessage(ChatColor.GOLD + "What would you like to change " + ChatColor.GREEN + event.getMessage() + ChatColor.GOLD +  " to?");
            mapEditingDetail.put(player.getName(), event.getMessage());
            event.setCancelled(true);
            break;
      }
   }

   @EventHandler
   public void onDamage(EntityPortalEvent event){
      if(event.getEntity() instanceof BaseHuman){
         System.out.println("You're aweseome!");
      }
   }
}