package io.github.jbillman.customnpc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.jbillman.customnpc.packetReader.events.Join;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.EntityTypes;


public class Main extends JavaPlugin implements Listener {

   EntityTypes<EntitySomething> SOMETHING;
   EntitySomething something = null;
   EntityPlayer testPlayer = null;
      
   @Override public void onLoad() {

   }



   @Override public void onEnable() {
      Bukkit.getPluginManager().registerEvents(new Join(), this);
      SOMETHING = EntityCustom.register(EntitySomething::new, "something", EntityTypes.PLAYER); 
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label,
         String[] args) {
      if(label.equalsIgnoreCase("createnpc")){
         if(!(sender instanceof Player)){
            return true;
         }
         Player player = (Player) sender;

         Location location  = player.getLocation();
         location.setY(location.getY() + 1);
         
         try {
            this.something = EntitySomething.spawn(SOMETHING, location);
         } catch (Exception e) {
            e.printStackTrace();
         }

      }      
      return false;
   }

   public EntitySomething getEntity(){
      return something;
   }

   @Override public void onDisable() {
      
   }
}
