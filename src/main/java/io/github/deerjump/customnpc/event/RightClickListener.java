package io.github.deerjump.customnpc.event;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import io.github.deerjump.customnpc.entity.human.FakePlayer;
import net.minecraft.server.v1_16_R1.Entity;

public class RightClickListener implements Listener {
   HashMap<String, EntityAbstract> mapEditingNPC = new HashMap<>(); 
   HashMap<String, String> mapEditingDetail = new HashMap<>(); 

   @EventHandler
   public void onRightClick(PlayerInteractEntityEvent event){
      Player player = event.getPlayer();
      Entity nmsEntity = ((CraftEntity)event.getRightClicked()).getHandle();
      if(event.getHand().toString().equalsIgnoreCase("off_hand"))
         return;

      if(!(nmsEntity instanceof EntityAbstract))
         return;

      Bukkit.getPluginManager().callEvent(new PlayerInteractAtNPCEvent(player, (EntityAbstract)nmsEntity));
   }

   @EventHandler
   public void onNPCInteract(PlayerInteractAtNPCEvent event){
      Player player = event.getPlayer();
      EntityAbstract npc = event.getClickedEntity();

      if(!(npc instanceof FakePlayer)){
         System.out.print("You're not a fake player!");
         npc.setName("Not a fake player!");
         return;
      }

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

      EntityAbstract npc = mapEditingNPC.get(player.getName());
      if( mapEditingDetail.get(player.getName()).equalsIgnoreCase("name")  || !(npc instanceof FakePlayer)){
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

      FakePlayer playerNpc = (FakePlayer) npc;

      switch(mapEditingDetail.get(player.getName())){
         case "skin":
            // System.out.println("Setting skin to: " + event.getMessage());
            player.sendMessage(ChatColor.GOLD + "Setting skin to: " + ChatColor.GREEN + event.getMessage());
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
         case "skin":
            player.sendMessage(ChatColor.GOLD + "What would you like to change " + ChatColor.GREEN + event.getMessage() + ChatColor.GOLD +  " to?");
            mapEditingDetail.put(player.getName(), event.getMessage());
            event.setCancelled(true);
            break;
      }
   }

}