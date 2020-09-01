package io.github.deerjump.customnpc;



import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

   public EntityTypes<PlayerNPC> FAKE_PLAYER;
   public EntityTypes<PlayerNPC2> FAKE_PLAYER2;
   public EntityTypes<CustomVillager> CUSTOM_VILLAGER;

   @Override
   public void onLoad() {
      
   }

   @Override
   public void onEnable() {
      FAKE_PLAYER = NpcBase.register(PlayerNPC::new, "player_npc", EntityTypes.PLAYER);
      // FAKE_PLAYER2 = NpcBase.register(PlayerNPC2::new, "player_npc2", EntityTypes.PLAYER);
      CUSTOM_VILLAGER = NpcBase.register(CustomVillager::new, "villager_npc", EntityTypes.ZOMBIE);
      
      Bukkit.getPluginManager().registerEvents(new Join(this), this);
      Bukkit.getPluginManager().registerEvents(new RightClickListener(), this);
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label,
         String[] args) {
      if(!(sender instanceof Player))
         return true;

      Player player = (Player) sender;   
      
      if(label.equalsIgnoreCase("createnpc")){
         
         Location location  = player.getLocation();
         NpcBase.spawn(FAKE_PLAYER, location);
      } 

      if(label.equalsIgnoreCase("createnpc2")){
         
         Location location  = player.getLocation();
         NpcBase.spawn(FAKE_PLAYER2, location);
      } 

      if(label.equalsIgnoreCase("createvillager")){
         
         Location location = player.getLocation();
         NpcBase.spawn(CUSTOM_VILLAGER, location);
      }     
      return false;
   }

   @Override public void onDisable() {

   }
}
