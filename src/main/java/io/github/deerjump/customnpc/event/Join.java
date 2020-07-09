package io.github.deerjump.customnpc.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.deerjump.customnpc.packetReader.PacketReader;

public class Join implements Listener{
   
   @EventHandler
   private void onJoin(PlayerJoinEvent event){
      PacketReader reader = new PacketReader();
      reader.inject(event.getPlayer());
   }

   @EventHandler
   private void onQuit(PlayerQuitEvent event){
      PacketReader reader = new PacketReader();
      reader.uninject(event.getPlayer());
   }
}