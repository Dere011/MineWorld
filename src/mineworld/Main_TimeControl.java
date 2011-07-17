package mineworld;

import java.util.Random;

import npcspawner.BasicHumanNpc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
	
	private void do_meteo() {
		if(meteotick > 5 && !dead_sun) {
			meteotick = 0;
			if(meteo_monde_type == 2) {
				if(meteo_monde_tick > 800) {
					meteo_monde_tick = 0;
					meteo_monde_type = 1;
			    	setStorm(false);
			    	setThundering(false);
				}else{
					if(!isStorm()) {
				    	setStorm(true);
				    	setThundering(true);
					}
				}
			}else if(meteo_monde_type == 1) {
				if(meteo_monde_tick > 6000) {
					meteo_monde_tick = 0;
					meteo_monde_type = 2;
			    	setStorm(true);
			    	setThundering(true);
				}else if(meteo_monde_tick > 5800) {
					if (meteo_monde_storm == 0) {
						meteo_monde_storm = showRandomInteger(1, 10, rand);
					}
					if (meteo_monde_storm > 1) {
			    		if(showRandomInteger(1, 100, rand) < 10) {
				    		int song_id = showRandomInteger(1, 3, rand);
		    				plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/thunder_distant"+song_id+".wav");
				    	}
					}
					if(isStorm()) {
						setStorm(false);
				    	setThundering(false);
					}
				}else if(meteo_monde_tick < 100) {
					if (meteo_monde_storm > 1) {
			    		if(showRandomInteger(1, 100, rand) < 10) {
				    		int song_id = showRandomInteger(1, 3, rand);
		    				plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/thunder_distant"+song_id+".wav");
			    		}
					}
				}else if(meteo_monde_tick > 100 && meteo_monde_tick < 150) {
					if (meteo_monde_storm > 1) {
						meteo_monde_storm = 0;
					}
				}else{
					if(isStorm()) {
						setStorm(false);
				    	setThundering(false);
					}
				}
			}else{
				meteo_monde_type = 1;
				setStorm(false);
		    	setThundering(false);
			}
		}
		meteo_monde_tick++;
		meteotick++;
		
    	if(storm_tick_n >= storm_next_tick) {
	    	storm_tick_n = 0;
	    	storm_next_tick = showRandomInteger(5, 15, rand);
	    	if(meteo_monde_storm > 1) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(plugin.Main_Visiteur.is_visiteur(p)) {
	    				continue;
	    			}
	    			if(isStorm()) {
	    				World pworld = p.getWorld();
		    			if((pworld.getHighestBlockYAt(p.getLocation())-20) < p.getLocation().getBlockY()) {
			    			if(showRandomInteger(1, 2, rand) == 2) {
			    				if(showRandomInteger(1, 100, rand) < 60) {
					    			if(showRandomInteger(1, 2, rand) == 1) {
					    				Location loc = new Location(pworld, p.getLocation().getBlockX()+showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()+showRandomInteger(1, 50, rand));
					    				strike_eclaire(loc);
					    			}else{
					    				Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 50, rand));
					    				strike_eclaire(loc);
					    			}
			    				}else{
			    					Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 5, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 5, rand));
			    					strike_eclaire(loc);
			    				}
			    			}else{
			    				int song_id = showRandomInteger(2, 6, rand);
			    				plugin.Main_ContribControl.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/stereo_gust_0"+song_id+".wav");
			    				int song2_id = showRandomInteger(1, 3, rand);
			    				plugin.Main_ContribControl.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/thunder_distant"+song2_id+".wav");
			    			}
		    			}
	    			}
	    		}
	    	}
    	}else{
    		storm_tick_n++;
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