package io.github.deerjump.customnpc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.deerjump.customnpc.entity.CustomVillager;
import io.github.deerjump.customnpc.entity.PlayerNPC;
import io.github.deerjump.customnpc.event.Join;
import io.github.deerjump.customnpc.event.RightClickListener;
import io.github.deerjump.playernpcs.NpcBase;
import io.github.deerjump.playernpcs.HumanBase;
import net.minecraft.server.v1_16_R2.EntityTypes;

public class Main extends JavaPlugin {
   
   public EntityTypes<PlayerNPC> FAKE_PLAYER;
   public EntityTypes<CustomVillager> CUSTOM_VILLAGER;
      
   @Override public void onLoad() {
     
   }

   @Override public void onEnable() {
      Bukkit.getPluginManager().registerEvents(new Join(this), this);
      Bukkit.getPluginManager().registerEvents(new RightClickListener(), this);

      FAKE_PLAYER = NpcBase.register(PlayerNPC::new, "player_npc", EntityTypes.PLAYER); 
      CUSTOM_VILLAGER = NpcBase.register(CustomVillager::new, "npc_villager_npc", EntityTypes.VILLAGER);
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label,
         String[] args) {
      if(!(sender instanceof Player))
         return true;

      Player player = (Player) sender;   
      
      if(label.equalsIgnoreCase("createnpc")){
         
         Location location  = player.getLocation();
         HumanBase.spawn(FAKE_PLAYER, location);
      } 

      if(label.equalsIgnoreCase("createvillager")){
         
         Location location = player.getLocation();
         CustomVillager.spawn(CUSTOM_VILLAGER, location);
      }     
      return false;
   }

   @Override public void onDisable() {
      // EntityBase.unregister(FAKE_PLAYER);
      // EntityBase.unregister(CUSTOM_VILLAGER);
   }
}
