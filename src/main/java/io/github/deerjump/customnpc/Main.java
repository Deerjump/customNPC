package io.github.deerjump.customnpc;



import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.deerjump.customnpc.entity.CustomVillager;
import io.github.deerjump.customnpc.entity.PlayerNPC;
import io.github.deerjump.customnpc.entity.PlayerNPC2;
import io.github.deerjump.customnpc.event.Join;
import io.github.deerjump.customnpc.event.RightClickListener;
import io.github.deerjump.npclib.v1_16_R2.NpcBase;
import net.minecraft.server.v1_16_R2.EntityTypes;

public class Main extends JavaPlugin {

   //Save the type for spawning them in later
   public EntityTypes<PlayerNPC> FAKE_PLAYER;
   public EntityTypes<PlayerNPC2> FAKE_PLAYER2;
   public EntityTypes<CustomVillager> CUSTOM_VILLAGER;

   @Override
   public void onLoad() {
      //Just here for testing (usually code viability)
   }

   @Override
   public void onEnable() {
      //Register your entity (also make sure your plugin loads on startup)
      //EntityTypes.PLAYER determines what the npc will look like
      FAKE_PLAYER = NpcBase.register(PlayerNPC::new, "player_npc", EntityTypes.PLAYER);
      FAKE_PLAYER2 = NpcBase.register(PlayerNPC2::new, "player_npc2", EntityTypes.PLAYER);
      CUSTOM_VILLAGER = NpcBase.register(CustomVillager::new, "villager_npc", EntityTypes.VILLAGER);

      //not important just for testing
      Bukkit.getPluginManager().registerEvents(new Join(this), this);
      Bukkit.getPluginManager().registerEvents(new RightClickListener(), this);
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label,
         String[] args) {
      if(!(sender instanceof Player))
         return true;

      Player player = (Player) sender;   
      Location location  = player.getLocation();

      //Built in spawn function, just pass in your custom type
      if(label.equalsIgnoreCase("createnpc")){
         NpcBase.spawn(FAKE_PLAYER, location);
      } 

      if(label.equalsIgnoreCase("createnpc2")){
         NpcBase.spawn(FAKE_PLAYER2, location);
      } 

      if(label.equalsIgnoreCase("createvillager")){
         NpcBase.spawn(CUSTOM_VILLAGER, location);
      }     
      return false;
   }

   @Override public void onDisable() {

   }
}
