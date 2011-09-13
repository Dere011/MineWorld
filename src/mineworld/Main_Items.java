package mineworld;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.getspout.spoutapi.SpoutManager;

public class Main_Items {

	Main plugin;
	SpoutManager sp;
	
    public Main_Items(Main parent) {
    	plugin = parent;
    }
    
    public void load_items() {
    	try {
	    	Configuration conf = plugin.conf_items;
			if(plugin.ITEM_configFile.exists()){
	    		conf.load();
				List<String> itemlist = conf.getKeys("load-items");
				if(!itemlist.isEmpty()) {
					ConfigurationNode node = conf.getNode("load-items");
					for(String item : itemlist){
						int id = node.getInt(item + ".id", 0);
						String name = node.getString(item + ".name");
						String url = node.getString(item + ".url");
				    	SpoutManager.getItemManager().setCustomItemBlock(1, id, (short) 0);
				    	SpoutManager.getItemManager().setItemTexture(id, null, url);
				    	SpoutManager.getItemManager().setItemName(id, name);
				    	plugin.logger.log(Level.INFO, "[MwITEM] Chargement de l'objet "+ id +" ("+name+")");
					}
				}
			}
		} catch (Exception e) {
		      e.printStackTrace();
		}
    }
    
    public void remove_all_items() {
		for (World w : plugin.getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e instanceof Creature) {
					((Creature) e).remove();
				}
				if (e instanceof Arrow || e instanceof Item) {
					e.remove();
				}
			}
		}
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