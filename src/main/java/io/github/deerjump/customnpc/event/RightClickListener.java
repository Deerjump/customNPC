package io.github.deerjump.customnpc.event;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import io.github.deerjump.customnpc.entity.human.FakePlayer;
import net.minecraft.server.v1_16_R1.Entity;

public class RightClickListener implements Listener {
    
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
}