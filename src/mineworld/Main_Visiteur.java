package mineworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Main_Visiteur {
	
	public List<Player> visiteur = new ArrayList<Player>();
	public List<String> whitelist = new ArrayList<String>();
	
	Main plugin;
	Thread thread_01;

    public Main_Visiteur(Main parent) {
    	plugin = parent;
    }
    
	public Runnable runThread_1(final Main plugin) {
		if(thread_01 == null) {
			thread_01 = new Thread(new Runnable() {
			public void run()
			{
			    	try {
			    		if (plugin.playerInServer()) {
			    			visiteur_do();
			    		}
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			thread_01.setPriority(Thread.MIN_PRIORITY);
			thread_01.setDaemon(false);
		}
		return thread_01;
	}
    
    public void charge_whitelist() throws IOException {
    	whitelist.clear();
		BufferedReader reader = new BufferedReader(new FileReader(("plugins" + File.separatorChar + "MineWorld" + File.separatorChar + "whitelist.txt")));
	    String line = reader.readLine();
	    while (line != null)
	    {
	      whitelist.add(line.toLowerCase());
	      line = reader.readLine();
	    }
	    reader.close();
    }
	
   public int visiteur_number() {
		int number = 0;
    	for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline() && is_visiteur(p)) {
				number++;
			}
		}
		return number;
    }
    
    public void visiteur_do() {
    	for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline() && (!p.isOp() && !plugin.modo.contains(p.getName()))) {
				for (Entity ent : p.getNearbyEntities(35, 35, 35)) {
					if (ent instanceof Player) {
						if(!plugin.isbot((Player) ent) && is_visiteur((Player) ent) != is_visiteur(p)) {
							CraftPlayer unHide = (CraftPlayer) p;
							CraftPlayer unHideFrom = (CraftPlayer) ent;
							unHide.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(unHideFrom.getEntityId()));
						}
					}
				}
			}
		}
    }
    
    public Boolean is_visiteur(Player player) {
    	if (!visiteur.isEmpty() && visiteur.contains(player)) {
    		return true;
    	}
    	return false;
    }
    
    public void denied_message(Player player) {
    	int visiteurmsg_lasttime = (Integer) plugin.getPlayerConfig(player, "time_visiteur_msg", "int");
	    if((plugin.timetamps-visiteurmsg_lasttime) > 10) {
	    	player.sendMessage(ChatColor.DARK_RED + "Désolé, les visiteurs ne peuvent pas faire cette action.");
	    	plugin.setPlayerConfig(player, "time_visiteur_msg", plugin.timetamps);
	    }
    }
    
    public Boolean add_visiteur(Player player) {
    	if (!visiteur.isEmpty()) {
	    	if (visiteur.contains(player)) {
	    		return true;
	    	}else{
	    		visiteur.add(player);
	    		return true;
	    	}
    	}else{
    		visiteur.add(player);
    		return true;
    	}
    }
    
    public Boolean remove_visiteur(Player player) {
    	if (!visiteur.isEmpty()) {
	    	if (visiteur.contains(player)) {
	    		visiteur.remove(player);
	    		return true;
	    	}else{
	    		return false;
	    	}
    	}else{
    		return false;
    	}
    }
}