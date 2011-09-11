package mineworld;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;

public class Main_Items {

	Main plugin;
	SpoutManager sp;
	
    public Main_Items(Main parent) {
    	plugin = parent;
    }
    
    public void give(Player player, int itemid) {
    	give(player, itemid, 1);
    }
    
    public void give(Player player, int itemid, int number) {
    	
		ItemStack cadavre = sp.getItemManager().getCustomItemStack(itemid, number);
		player.getWorld().dropItemNaturally(event.getEntity().getLocation(), cadavre);
    	
    }
    
    public void drop(Location location, int itemid) {
    	drop(location, itemid, 1);
    }
    
    public void drop(Location location, int itemid, int number) {
		ItemStack item = sp.getItemManager().getCustomItemStack(itemid, number);
		location.getWorld().dropItemNaturally(location, item);
    }
   
}