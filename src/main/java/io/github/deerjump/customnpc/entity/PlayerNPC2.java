package io.github.deerjump.customnpc.entity;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import io.github.deerjump.npclib.v1_16_R2.HumanBase;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.World;

public class PlayerNPC2 extends HumanBase implements InventoryHolder {
    
   public PlayerNPC2(EntityTypes<? extends HumanBase> type, World world) {
      super(type, world);
      this.setInvulnerable(true);
      this.inventory = Bukkit.createInventory(this, 27, "NPC");
   }

   protected Inventory inventory;

   @Override
   public Inventory getInventory() {
      return this.inventory;
   }
   
}