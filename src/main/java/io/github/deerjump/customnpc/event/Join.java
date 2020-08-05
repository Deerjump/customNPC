package io.github.deerjump.customnpc.event;

import org.bukkit.entity.Player;
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
      Player player = event.getPlayer();
      // PacketReader reader = new PacketReader(plugin);
      // reader.inject(player);
   }

   @EventHandler
   private void onQuit(PlayerQuitEvent event){
      Player player = event.getPlayer();
      // PacketReader reader = new PacketReader(plugin);
      // reader.uninject(player);     
   }
}