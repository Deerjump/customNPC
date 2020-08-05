package io.github.deerjump.customnpc.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.deerjump.customnpc.entity.EntityAbstract;

public class PlayerInteractAtNPCEvent extends Event implements Cancellable {

   private Player player;
   private EntityAbstract entity;
   private boolean isCancelled;
   private static final HandlerList HANDLERS = new HandlerList();

   public PlayerInteractAtNPCEvent(Player who, EntityAbstract clickedEntity) {
      player = who;
      entity = clickedEntity;
   }

   public Player getPlayer(){
      return player;
   }

   public EntityAbstract getClickedEntity(){
      return entity;
   }
   
   @Override
   public boolean isCancelled() {
      return isCancelled;
   }

   @Override
   public void setCancelled(boolean flag) {
      isCancelled = flag;
   }

   @Override
   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList(){
      return HANDLERS;
   }
}