package io.github.deerjump.customnpc.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.deerjump.customnpc.Main;

public class Join implements Listener{
   
   Main plugin;

   public Join(Main plugin){
      this.plugin = plugin;
   }

   @EventHandler
   private void onJoin(PlayerJoinEvent event){
      
      
   }

   @EventHandler
   private void onQuit(PlayerQuitEvent event){
      

   }
}