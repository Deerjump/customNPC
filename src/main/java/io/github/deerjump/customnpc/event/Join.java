package io.github.deerjump.customnpc.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.deerjump.customnpc.Main;
import io.github.deerjump.customnpc.packetReader.PacketReader;

public class Join implements Listener{
   
   Main plugin;

   public Join(Main plugin){
      this.plugin = plugin;
   }

   @EventHandler
   private void onJoin(PlayerJoinEvent event){
      if(event.getPlayer().getName().equalsIgnoreCase("jbillyman")){
         PacketReader reader = new PacketReader(this.plugin);
         reader.inject(event.getPlayer());
      }
   }

   @EventHandler
   private void onQuit(PlayerQuitEvent event){
      if(event.getPlayer().getName().equalsIgnoreCase("jbillyman")){
         PacketReader reader = new PacketReader(this.plugin);
         reader.uninject(event.getPlayer());
      }
   }
}