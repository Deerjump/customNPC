package io.github.deerjump.customnpc;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.deerjump.customnpc.entity.EntityAbstract;
import io.github.deerjump.customnpc.entity.human.FakePlayer;
import io.github.deerjump.customnpc.entity.villager.CustomVillager;
import io.github.deerjump.customnpc.event.Join;
import io.github.deerjump.customnpc.event.RightClickListener;
import net.minecraft.server.v1_16_R1.EntityTypes;




public class Main extends JavaPlugin implements Listener {
   
   public EntityTypes<FakePlayer> FAKE_PLAYER;
   public EntityTypes<CustomVillager> CUSTOM_VILLAGER;
      
   @Override public void onLoad() {

     
   }

   @Override public void onEnable() {     
      Bukkit.getPluginManager().registerEvents(new Join(this), this);
      Bukkit.getPluginManager().registerEvents(new RightClickListener(), this);

      FAKE_PLAYER = EntityAbstract.register(FakePlayer::new, "player_npc", EntityTypes.PLAYER); 
      CUSTOM_VILLAGER = EntityAbstract.register(CustomVillager::new, "npc_villager_npc", EntityTypes.VILLAGER);
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label,
         String[] args) {
      if(!(sender instanceof Player))
         return true;

      Player player = (Player) sender;   
      
      if(label.equalsIgnoreCase("createnpc")){
         
         Location location  = player.getLocation();
         FakePlayer.spawn(FAKE_PLAYER, location);
      } 

      if(label.equalsIgnoreCase("createvillager")){
         
         Location location = player.getLocation();
         CustomVillager.spawn(CUSTOM_VILLAGER, location);
      }     
      return false;
   }

   @Override public void onDisable() {
      // EntityAbstract.unregister(FAKE_PLAYER);
      // EntityAbstract.unregister(CUSTOM_VILLAGER);
   }
}
