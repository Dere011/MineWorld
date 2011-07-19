package mineworld;

import java.util.Random;

import npcspawner.BasicHumanNpc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Main_TimeControl {
	
    private final Main plugin;
    
	private final Random rand = new Random();
    
    Thread thread_01, thread_02;
    
    public int meteo_monde_tick = 0;
    public int meteo_monde_type = 3;
    public int meteo_monde_storm = 0;
    
    private int storm_next_tick;
    private int storm_tick_n = 0;
    private int meteotick = 0;
    
    public boolean dead_sun = false;
    public boolean pre_dead_sun = false;
    public boolean prepre_dead_sun = false;
    public int dead_sun_tick = 0;
    public int dead_sun_next = showRandomInteger(5000, 25000, rand);
    
    public Main_TimeControl(Main parent) {
        this.plugin = parent;
    }
	
	public Runnable runThread_meteo(final Main plugin) {
		if(thread_02 == null) {
			thread_02 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		if (plugin.playerInServer()) {
			    			do_meteo();
			    		}
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			thread_02.setPriority(Thread.MIN_PRIORITY);
			thread_02.setDaemon(false);
		}
		return thread_02;
	}
    
	private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    long range = (long)aEnd - (long)aStart + 1;
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);
	    return randomNumber;
	}
	
	public void setTime(String world, long time) {
		plugin.getServer().getWorld(world).setTime(time);
	}
	
	public Boolean isStorm() {
		World w = plugin.getServer().getWorld("world");
		if(w.hasStorm()) {
			return true;
		}
		return false;
	}
	
	public Boolean isThundering() {
		World w = plugin.getServer().getWorld("world");
		if(w.isThundering()) {
			return true;
		}
		return false;
	}
	
	public void setStorm(Boolean bool) {
		if(bool) {
			for (World w : plugin.getServer().getWorlds()) {
				w.setStorm(true);
			}
			plugin.Main_ContribControl.bool_clouds(true);
		}else{
			for (World w : plugin.getServer().getWorlds()) {
				w.setStorm(false);
			}
			plugin.Main_ContribControl.bool_clouds(false);
		}
	}
	
	public void setThundering(Boolean bool) {
		if(bool && meteo_monde_storm > 1) {
			for (World w : plugin.getServer().getWorlds()) {
				w.setThundering(true);
			}
			plugin.Main_ContribControl.bool_clouds(true);
		}else{
			for (World w : plugin.getServer().getWorlds()) {
				w.setThundering(false);
			}
			plugin.Main_ContribControl.bool_clouds(false);
		}
	}
	
	public void strike_eclaire(Location location) {
		int px = location.getBlockX();
		int pz = location.getBlockZ();
		World world = location.getWorld();
		double lastdistance = 1000000000;
		Location lastlocation = null;
		int lasttype = 0;
		for (int x = px-16; x <= px+16; x++) {
            for (int z = pz-16; z <= pz+16; z++) {
                Block block = world.getBlockAt(new Location(world, x, (world.getHighestBlockYAt(x, z)-1), z));
                int bid = block.getTypeId();
                if(bid == 42) {
	                double distance = plugin.getdistance(location, block.getLocation());
	                if(lastdistance > distance) {
	                	lastdistance = distance;
	                	lastlocation = block.getLocation();
	                	lasttype = 1;
	                }
                }else if(bid == 23 || bid == 61 || bid == 62 || bid == 33 || bid == 29) {
	                double distance = plugin.getdistance(location, block.getLocation());
	                if((lastdistance > distance && lasttype > 1) || ((lastdistance+5) > distance && lasttype == 1)) {
	                	lastdistance = distance;
	                	lastlocation = block.getLocation();
	                	lasttype = 2;
	                }
                }
            }
        }
		if(lastlocation != null) {
			Location llocation = new Location(world, lastlocation.getX(), lastlocation.getY()+1, lastlocation.getZ());
			world.strikeLightning(llocation);
			int sound_id = showRandomInteger(1, 9, rand);
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(lastlocation, "http://mineworld.fr/contrib/sound/zap"+sound_id+".wav");
			int storm_id = showRandomInteger(1, 4, rand);
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(lastlocation, "http://mineworld.fr/contrib/sound/thunder_close0"+storm_id+".wav");
		}else{
			Location slocation = new Location(world, location.getX(), world.getHighestBlockYAt(location), location.getZ());
			int storm_id = showRandomInteger(1, 4, rand);
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(slocation, "http://mineworld.fr/contrib/sound/thunder_close0"+storm_id+".wav");
			world.strikeLightning(slocation);
		}
	}
	
	private void cycle_storm() {
		if(storm_uid != cycle_uid) {
			cycle_start = plugin.timetamps;
			cycle_end = (plugin.timetamps+showRandomInteger((60*1), (60*10), rand));
			storm_force = showRandomInteger(1, 10, rand);
			storm_uid = cycle_uid;
			if(storm_force > 5) {
				storm_wind = true;
				wind_direction = new Location(null, showRandomInteger(-10000, 10000, rand), 0, showRandomInteger(-10000, 10000, rand));
			}else{
				storm_wind = false;
			}
			plugin.Main_ContribControl.bool_clouds(true);
			plugin.Main_ContribControl.h_clouds(storm_force*5);
		}
		if(plugin.timetamps < (cycle_start+30)) {
			if(showRandomInteger(1, 100, rand) < 5+(storm_force*5)) {
				plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/thunder_distant"+ showRandomInteger(1, 3, rand) +".wav");
	    	}
		}else if(plugin.timetamps > (cycle_start+30) && plugin.timetamps < (cycle_end-30)) {
			if(!isStorm()) {
		    	setStorm(true);
		    	setThundering(true);
			}
	    	if(plugin.timetamps >= storm_next_tick) {
		    	storm_next_tick = showRandomInteger(1, (15-storm_force), rand);
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(plugin.Main_Visiteur.is_visiteur(p)) {
	    				continue;
	    			}
    				World pworld = p.getWorld();
	    			if((pworld.getHighestBlockYAt(p.getLocation())-15) < p.getLocation().getBlockY()) {
		    			if(showRandomInteger(1, 2, rand) == 2) {
		    				Location loc;
		    				if(showRandomInteger(1, 100, rand) < (40+(storm_force*5))) {
		    					int dirrand = showRandomInteger(1, 4, rand);
				    			if(dirrand == 1) {
				    				loc = new Location(pworld, p.getLocation().getBlockX()+showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()+showRandomInteger(1, 50, rand));
				    			}else if(dirrand == 2) {
				    				loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 50, rand));
				    			}else if(dirrand == 3) {
				    				loc = new Location(pworld, p.getLocation().getBlockX()+showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 50, rand));
				    			}else if(dirrand == 4) {
				    				loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()+showRandomInteger(1, 50, rand));
				    			}
		    				}else{
		    					loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 5, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 5, rand));
		    				}
	    					strike_eclaire(loc, storm_force);
		    			}else{
		    				if(storm_wind && showRandomInteger(1, (6+(storm_force-5)), rand) > 5) {
		    					if((pworld.getHighestBlockYAt(p.getLocation())-5) < p.getLocation().getBlockY()) {
		    						attireEntity(p, wind_direction);
		    					}
		    					plugin.Main_ContribControl.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/stereo_gust_0"+ showRandomInteger(2, 6, rand) +".wav");
		    				}
		    				plugin.Main_ContribControl.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/thunder_distant"+ showRandomInteger(1, 3, rand) +".wav");
		    			}
	    			}
	    		}
	    	}
		}else{
			if(showRandomInteger(1, 100, rand) < 5+(storm_force*5)) {
				plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/thunder_distant"+ showRandomInteger(1, 3, rand) +".wav");
	    	}
		}
	}
	
	private void cycle_rain() {
		if(rain_uid != cycle_uid) {
			cycle_start = plugin.timetamps;
			cycle_end = (plugin.timetamps+showRandomInteger((60*1), (60*10), rand));
			rain_force = showRandomInteger(1, 10, rand);
			rain_uid = cycle_uid;
			if(rain_force > 5) {
				rain_wind = true;
				wind_direction = new Location(null, showRandomInteger(-10000, 10000, rand), 0, showRandomInteger(-10000, 10000, rand));
			}else{
				rain_wind = false;
			}
			plugin.Main_ContribControl.bool_clouds(true);
			plugin.Main_ContribControl.h_clouds(rain_force*3);
			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/rur5b_cricket_loopl.wav");
		}
		if(plugin.timetamps > (cycle_start+10)) {
			if(!isStorm() || isThundering()) {
		    	setStorm(true);
		    	setThundering(false);
			}
			if(showRandomInteger(1, 100, rand) < 5+(rain_force*5)) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(plugin.Main_Visiteur.is_visiteur(p)) {
	    				continue;
	    			}
	    			World pworld = p.getWorld();
					if((pworld.getHighestBlockYAt(p.getLocation())-5) < p.getLocation().getBlockY()) {
						attireEntity(p, wind_direction);
					}
					plugin.Main_ContribControl.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/stereo_gust_0"+ showRandomInteger(2, 6, rand) +".wav");
	    		}
	    	}
		}
	}
	
	private void do_meteo() {
		
		if(in_cycle) {
			if(plugin.timetamps > cycle_end) {
				in_cycle = false;
				next_cycle = (plugin.timetamps+showRandomInteger((60*5), (60*60), rand));
				cycle_uid = 0;
				plugin.Main_ContribControl.bool_clouds(false);
				plugin.Main_ContribControl.h_clouds(10);
			    setStorm(false);
			    setThundering(false);
			}
			switch (cycle_id) { 
				case 1:
					cycle_storm();
					cycle_wind();
				case 2:	
					cycle_rain();
					cycle_wind();
				case 3:
					cycle_fog();
				case 4:
					cycle_deadsun();
			}
		}else{
			if(plugin.timetamps > next_cycle) {
				int current_gen_id = showRandomInteger(1, 7, rand);
				if(current_gen_id < 4) {
					in_cycle = true;
					cycle_uid = plugin.timetamps;
					cycle_id = current_gen_id;
				}else{
					if(last_deadsun+((60*60)*2) < plugin.timetamps) {
						in_cycle = true;
						cycle_uid = plugin.timetamps;
						cycle_id = current_gen_id;
					}
				}
			}else{
				if(isStorm() || isThundering()) {
				    setStorm(false);
				    setThundering(false);
				}
			}
		}
    	
    	if(dead_sun) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(plugin.Main_Visiteur.is_visiteur(p) || p.isOp()) {
    				continue;
    			}
    			if(p.getWorld().getHighestBlockYAt(p.getLocation()) <= p.getLocation().getBlockY() && p.getLocation().getBlock().getLightLevel() >= 13) {
    				p.setFireTicks(100);
    				int aie_lasttime = (Integer) plugin.getPlayerConfig(p, "time_sundead_aie", "int");
    				if((plugin.timetamps-aie_lasttime) > 15) {
    					plugin.Main_MessageControl.sendTaggedMessage(p, "Attention, le soleil vous brule !", 1, "[DEAD SUN]");
    					plugin.setPlayerConfig(p, "time_sundead_aie", plugin.timetamps);
    				}
    			}
    		}
    	}
    	
    	if(dead_sun_tick >= dead_sun_next) {
    		dead_sun_tick = 0;
   			long time = plugin.getServer().getWorld("world").getTime();
			time = time - time % 24000;
    		if(dead_sun) {
	    		dead_sun_next = showRandomInteger(5000, 25000, rand);
	    		dead_sun = false;
	    		plugin.Main_ContribControl.setSunURLtoAll("http://mineworld.fr/contrib/sun/normalsun.png");
    			plugin.Main_ChunkControl.ResendAll.clear();
    			plugin.Main_ChunkControl.player_chunkupdate.clear();
    			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/sewerair.wav");
    			plugin.Main_ContribControl.sendNotificationToAll("Information", "Le soleil est parti!", Material.WATER);
	    		setTime("world", time + 37700);
    		}else{
    			dead_sun_next = 500;
    			dead_sun = true;
    			pre_dead_sun = false;
    			prepre_dead_sun = false;
				plugin.Main_MessageControl.sendTaggedMessageToAll(ChatColor.RED + "Attention, le soleil brulant est là !", 1, "[DEAD SUN]");
				plugin.Main_ContribControl.sendNotificationToAll("Attention", "Le soleil brulant est là!", Material.FIRE);
				int music_id = showRandomInteger(1, 4, rand);
				plugin.Main_ContribControl.sendSoundToAll("http://mineworld.fr/contrib/sound/deadsun_"+music_id+".ogg");
				setTime("world", time + 24000);
				if(isStorm()) {
					setStorm(false);
			    	setThundering(false);
				}
    		}
    	}else if(dead_sun_tick >= (dead_sun_next-5) && (!dead_sun && !prepre_dead_sun)) {	
    		pre_dead_sun = false;
    		prepre_dead_sun = true;
			plugin.Main_ChunkControl.ResendAll.clear();
			plugin.Main_ChunkControl.player_chunkupdate.clear();
			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/multibeep.wav");
			plugin.Main_ContribControl.setSunURLtoAll("http://mineworld.fr/contrib/sun/deadsunsun.png");
    	}else if((dead_sun_tick >= (dead_sun_next-100) && dead_sun_tick < (dead_sun_next-5)) && (!dead_sun && !pre_dead_sun)) {	
			for (BasicHumanNpc entry : plugin.Main_NPC.HumanNPCList.GetNPCS()) {
				if(entry.getBukkitEntity().getWorld().getName() == "world") {
					plugin.Main_ContribControl.sendSoundEffectToAllToLocation(entry.getBukkitEntity().getLocation(), "http://mineworld.fr/contrib/sound/ohno.ogg");
				}
			}
    		pre_dead_sun = true;
    	}
    	dead_sun_tick++;
	}
}