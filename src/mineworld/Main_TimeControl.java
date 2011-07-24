package mineworld;

import java.util.Random;

import npcspawner.BasicHumanNpc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkitcontrib.event.input.RenderDistance;

public class Main_TimeControl {
	
    private final Main plugin;
	private final Random rand = new Random();
    
    Thread thread_01, thread_02;
    
    public long nextsecond = 0;
    public int meteo_monde_tick = 0;
    public int meteo_monde_type = 3;
    public int meteo_monde_storm = 0;
    
    // WIND
    private boolean wind = false;
    private int wind_force = 0;
    private Location  wind_direction;
    private long wind_uid = 0;
    
    // STORM
    private long storm_uid = 0;
    private int storm_force = 0;
	private long storm_next_tick = 0;
	
	// RAIN
	private long rain_uid = 0;
	private int rain_force = 0;
	
	// FOG
	private long fog_uid = 0;
	private int fog_visi = 0;
	
	// CYCLE
	private long cycle_start = 0;
	private long cycle_end = 0;
	private long cycle_uid = 0;
	private boolean in_cycle = false;
	private long next_cycle = 0;
	private int cycle_id = 0;
	private long last_deadsun = 0;
    
    public boolean dead_sun = false;
    public boolean pre_dead_sun = false;
    public boolean prepre_dead_sun = false;
    public int dead_sun_tick = 0;
    public int dead_sun_next = showRandomInteger(5000, 25000, rand);
    private long deadsun_uid = 0;
    private long next_dura = 0;
    
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
	
	public void strike_eclaire(Location location, Integer force) {
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
                if(bid == 42 || (bid == 27 && block.isBlockPowered())) {
	                double distance = plugin.getdistance(location, block.getLocation());
	                if(lastdistance > distance) {
	                	lastdistance = distance;
	                	lastlocation = block.getLocation();
	                	lasttype = 1;
	                }
                }else if(bid == 23 || bid == 61 || bid == 62 || bid == 33 || bid == 29 || bid == 66 || bid == 28) {
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
			int sound_id = showRandomInteger(1, 9, rand);
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(lastlocation, "http://mineworld.fr/contrib/sound/zap"+sound_id+".wav");
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(lastlocation, "http://mineworld.fr/contrib/sound/defibrillator_use.wav");
			int storm_id = showRandomInteger(1, 4, rand);
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(lastlocation, "http://mineworld.fr/contrib/sound/thunder_close0"+storm_id+".wav");
			world.strikeLightning(llocation);
			if(force > 8) {
				world.strikeLightning(llocation);
				world.strikeLightning(llocation);
			}
		}else{
			Location slocation = new Location(world, location.getX(), world.getHighestBlockYAt(location), location.getZ());
			int storm_id = showRandomInteger(1, 4, rand);
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(slocation, "http://mineworld.fr/contrib/sound/thunder_close0"+storm_id+".wav");
			world.strikeLightning(slocation);
			if(force > 8) {
				world.strikeLightning(slocation);
				world.strikeLightning(slocation);
			}
		}
	}
	
	private void cycle_thewind() {
		if(wind_uid != cycle_uid) {
			cycle_start = plugin.timetamps;
			cycle_end = (plugin.timetamps+showRandomInteger((60*1), (60*5), rand));
			wind_direction = new Location(null, showRandomInteger(-10000, 10000, rand), 0, showRandomInteger(-10000, 10000, rand));
			wind_uid = cycle_uid;
			plugin.Main_ContribControl.bool_clouds(true);
			wind = true;
			wind_force = showRandomInteger(1, 10, rand);
		}	
	}
	
	private void cycle_wind() {
		if(wind && showRandomInteger(1, (20+(wind_force-5)), rand) > 20) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(plugin.Main_Visiteur.is_visiteur(p)) {
    				continue;
    			}
    			World pworld = p.getWorld();
				if((pworld.getHighestBlockYAt(p.getLocation())-3) < p.getLocation().getBlockY()) {
					plugin.Main_ContribControl.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/stereo_gust_0"+ showRandomInteger(2, 6, rand) +".wav");
					plugin.Main_MoveControl.attireEntity(p, wind_direction);
				}
			}
    	}
	}
	
	private void cycle_fog() {
		if(fog_uid != cycle_uid) {
			cycle_start = plugin.timetamps;
			cycle_end = (plugin.timetamps+showRandomInteger((60*1), (60*5), rand));
			fog_visi = showRandomInteger(1, 2, rand);
			fog_uid = cycle_uid;
			plugin.Main_ContribControl.bool_clouds(true);
		}
		if(plugin.timetamps > (cycle_start+10)) {
			if(fog_visi == 1) {
				plugin.Main_ContribControl.set_fog(RenderDistance.TINY);
			}else if(fog_visi == 2) {
				plugin.Main_ContribControl.set_fog(RenderDistance.SHORT);
			}
			if(isStorm() || isThundering()) {
		    	setStorm(false);
		    	setThundering(false);
			}
		}
	}
	
	private void cycle_storm() {
		if(storm_uid != cycle_uid) {
			cycle_start = plugin.timetamps;
			cycle_end = (plugin.timetamps+showRandomInteger((60*1), (60*10), rand));
			storm_force = showRandomInteger(1, 10, rand);
			storm_uid = cycle_uid;
			if(storm_force > 5) {
				wind = true;
				wind_direction = new Location(null, showRandomInteger(-10000, 10000, rand), 0, showRandomInteger(-10000, 10000, rand));
				wind_force = storm_force;
			}else{
				wind = false;
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
		    	storm_next_tick = (plugin.timetamps+showRandomInteger(1, (10-storm_force), rand));
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(plugin.Main_Visiteur.is_visiteur(p)) {
	    				continue;
	    			}
    				World pworld = p.getWorld();
	    			if((pworld.getHighestBlockYAt(p.getLocation())-15) < p.getLocation().getBlockY()) {
		    			if(showRandomInteger(1, 2, rand) == 2) {
		    				Location loc = null;
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
				wind = true;
				wind_direction = new Location(null, showRandomInteger(-10000, 10000, rand), 0, showRandomInteger(-10000, 10000, rand));
				wind_force = rain_force;
			}else{
				wind = false;
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
		}
	}
	
	private void cycle_deadsun() {
		if(deadsun_uid != cycle_uid) {
			cycle_start = plugin.timetamps;
			cycle_end = (plugin.timetamps+showRandomInteger((60*1), (60*10), rand));
			deadsun_uid = cycle_uid;
		}
		long time = plugin.getServer().getWorld("world").getTime();
		time = time - time % 24000;
		if(plugin.timetamps < (cycle_start+10) && !prepre_dead_sun) {
			for (BasicHumanNpc entry : plugin.Main_NPC.HumanNPCList.GetNPCS()) {
				if(entry.getBukkitEntity().getWorld().getName() == "world") {
					plugin.Main_ContribControl.sendSoundEffectToAllToLocation(entry.getBukkitEntity().getLocation(), "http://mineworld.fr/contrib/sound/ohno.ogg");
				}
			}
			prepre_dead_sun = true;
		} else if(plugin.timetamps > (cycle_start+10) && plugin.timetamps < (cycle_start+20) && !pre_dead_sun) {
			pre_dead_sun = true;
			plugin.Main_ChunkControl.ResendAll.clear();
			plugin.Main_ChunkControl.player_chunkupdate.clear();
			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/multibeep.wav");
			plugin.Main_ContribControl.setSunURLtoAll("http://mineworld.fr/contrib/sun/deadsunsun.png");
		}else if(plugin.timetamps > (cycle_start+20) && !dead_sun) {
			dead_sun = true;
			pre_dead_sun = false;
			prepre_dead_sun = false;
			plugin.Main_MessageControl.sendTaggedMessageToAll(ChatColor.RED + "Attention, le soleil brulant est là !", 1, "[DEAD SUN]");
			plugin.Main_ContribControl.sendNotificationToAll("Attention", "Le soleil brulant est là!", Material.FIRE);
			int music_id = showRandomInteger(1, 4, rand);
			plugin.Main_ContribControl.sendSoundToAll("http://mineworld.fr/contrib/sound/deadsun_"+music_id+".ogg");
			setTime("world", time + 24000);
		}else if(plugin.timetamps > (cycle_start+25) && plugin.timetamps < (cycle_end-30)) {
			if(isStorm() || isThundering()) {
		    	setStorm(false);
		    	setThundering(false);
			}
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if(plugin.Main_Visiteur.is_visiteur(p) || p.isOp()) {
					continue;
				}
				if(p.getWorld().getHighestBlockYAt(p.getLocation()) <= p.getLocation().getBlockY() && p.getLocation().getBlock().getLightLevel() >= 12) {
					ItemStack helmet = p.getInventory().getHelmet();
					ItemStack legg = p.getInventory().getLeggings();
					ItemStack chest = p.getInventory().getChestplate();
					ItemStack boot = p.getInventory().getBoots();
					if(boot.getTypeId() == 317 && legg.getTypeId() == 316 && chest.getTypeId() == 315 && helmet.getTypeId() == 314) {
						if(plugin.timetamps > next_dura) {
							next_dura = plugin.timetamps+showRandomInteger(15, (60*3), rand);
							helmet.setDurability((short) (helmet.getDurability()+1));
							legg.setDurability((short) (helmet.getDurability()+1));
							chest.setDurability((short) (helmet.getDurability()+1));
							boot.setDurability((short) (helmet.getDurability()+1));
							plugin.Main_ContribControl.sendSoundEffectToAllToLocation(p.getLocation(), "http://mineworld.fr/contrib/sound/gascan_ignite1.wav");
						}
					}else{
						p.setFireTicks(250);
						plugin.Main_ContribControl.sendSoundEffectToAllToLocation(p.getLocation(), "http://mineworld.fr/contrib/sound/Male_Death_"+showRandomInteger(1, 3, rand)+".wav");
						int aie_lasttime = (Integer) plugin.getPlayerConfig(p, "time_sundead_aie", "int");
						if((plugin.timetamps-aie_lasttime) > 15) {
							plugin.Main_MessageControl.sendTaggedMessage(p, "Attention, le soleil vous brule !", 1, "[DEAD SUN]");
							plugin.setPlayerConfig(p, "time_sundead_aie", plugin.timetamps);
						}
					}
					
				}
			}
		}else if(plugin.timetamps > (cycle_start+25) && dead_sun) {
    		dead_sun = false;
    		plugin.Main_ContribControl.setSunURLtoAll("http://mineworld.fr/contrib/sun/normalsun.png");
			plugin.Main_ChunkControl.ResendAll.clear();
			plugin.Main_ChunkControl.player_chunkupdate.clear();
			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/sewerair.wav");
			plugin.Main_ContribControl.sendNotificationToAll("Information", "Le soleil est parti!", Material.WATER);
    		setTime("world", time + 37700);
		}
	}
	
	private void do_meteo() {
		if(in_cycle) {
			if(plugin.timetamps > cycle_end && cycle_end != 0) {
			    plugin.sendInfo("[CYCLE] STOP "+cycle_uid+".");
				in_cycle = false;
				next_cycle = (plugin.timetamps+showRandomInteger((60*5), (60*60), rand));
				cycle_uid = 0;
				cycle_end = 0;
				plugin.Main_ContribControl.bool_clouds(false);
				plugin.Main_ContribControl.h_clouds(10);
				plugin.Main_ContribControl.set_fog(RenderDistance.NORMAL);
			    setStorm(false);
			    setThundering(false);
			}
			if(plugin.timetamps >= nextsecond) {
				nextsecond = plugin.timetamps+1;
				switch (cycle_id) { 
					case 1:
						cycle_storm();
						cycle_wind();
						break;
					case 2:	
						cycle_rain();
						cycle_wind();
						break;
					case 3:
						cycle_fog();
						break;
					case 4:
						cycle_thewind();
						cycle_wind();
						break;
					case 5:
						cycle_deadsun();
						break;
				}
			}
		}else{
			if(next_cycle == 0) {
				next_cycle = (plugin.timetamps+showRandomInteger((60*5), (60*60), rand));
			}
			if(plugin.timetamps > next_cycle) {
				int current_gen_id = showRandomInteger(1, 5, rand);
				if(current_gen_id < 5) {
					in_cycle = true;
					cycle_uid = plugin.timetamps;
					cycle_id = current_gen_id;
					plugin.sendInfo("[CYCLE] New cycle ("+cycle_uid+") "+current_gen_id+".");
					plugin.Main_ContribControl.bool_clouds(false);
					plugin.Main_ContribControl.h_clouds(10);
					plugin.Main_ContribControl.set_fog(RenderDistance.NORMAL);
				    setStorm(false);
				    setThundering(false);
				}else{
					if(last_deadsun+((60*60)*3) < plugin.timetamps) {
						in_cycle = true;
						cycle_uid = plugin.timetamps;
						cycle_id = current_gen_id;
						last_deadsun = plugin.timetamps;
						plugin.sendInfo("[CYCLE] New deadsun cycle ("+cycle_uid+").");
						plugin.Main_ContribControl.bool_clouds(false);
						plugin.Main_ContribControl.h_clouds(10);
						plugin.Main_ContribControl.set_fog(RenderDistance.NORMAL);
					    setStorm(false);
					    setThundering(false);
					}
				}
			}else{
				setStorm(false);
				   setThundering(false);
			}
		}
	}
}