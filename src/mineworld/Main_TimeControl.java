package mineworld;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Main_TimeControl {
	
    private final Main plugin;
    
	private final Random rand = new Random();
    
    Thread thread_01;
    Thread thread_02;
    
    //private Boolean is_burningday = false;
    //public Boolean is_preburningday = false;
    //private int is_burningday_tick = 0;
    //private int is_preburningday_tick = 0;
    //private int cron_tick = 0;
    //private int burningday_ntick = 0;
    
    public int meteo_monde_tick = 0;
    public int meteo_monde_type = 3;
    public int meteo_monde_storm = 0;
    
    //public int wave_done_cron = 0;
    //public int next_wave_cron = 10000;
	//public int wave_cron = 0;
    
    private int storm_next_tick;
    private int storm_tick_n = 0;
    private int meteotick = 0;
    
    public Main_TimeControl(Main parent) {
        this.plugin = parent;
    }
    
    // THREAD
	/*public Runnable runThread_time(final Main plugin) {
		if(thread_01 == null) {
			thread_01 = new Thread(new Runnable() {
				@SuppressWarnings("static-access")
				public void run()
				{
			    	try {
			    		if (plugin.playerInServer()) {
			    			do_cron();
			    			plugin.timetamps = plugin.timetamps();
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
	}*/
	
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
    // THREAD
    
	private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    long range = (long)aEnd - (long)aStart + 1;
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);
	    return randomNumber;
	}
    
	/*public boolean shouldBurn(Location loc) {
		if (isDay(loc.getWorld())) {
			if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()).getLightLevel() >= 15) {
					return true;
			}
		}
		return false;
	}
	
	public boolean isDay(World world) {
		return world.getTime() < 12000 || world.getTime() == 24000;
	}
	
	public void dayburning_tick() {
		for (Player p : plugin.getServer().getWorld("deathworld").getPlayers()) {
			if(!p.isDead() && !p.isOp() && shouldBurn(p.getLocation())) {
				p.damage(2, mynpc.thesun.getBukkitEntity());
				p.setFireTicks(500);
				p.sendMessage(ChatColor.RED + "Les rayons du "+ ChatColor.YELLOW + "soleil"+ ChatColor.RED + " vous brule.");
			}
    	}
	}*/
	
	public void setTime(String world, int time) {
		plugin.getServer().getWorld(world).setTime(time);
	}
	
	public Boolean isStorm() {
		for (World w : plugin.getServer().getWorlds()) {
			if(w.hasStorm()) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean isThundering() {
		for (World w : plugin.getServer().getWorlds()) {
			if(w.isThundering()) {
				return true;
			}
		}
		return false;
	}
	
	public void setStorm(Boolean bool) {
		if(bool) {
			for (World w : plugin.getServer().getWorlds()) {
				w.setStorm(true);
			}
		}else{
			for (World w : plugin.getServer().getWorlds()) {
				w.setStorm(false);
			}
		}
	}
	
	public void setThundering(Boolean bool) {
		if(bool && meteo_monde_storm > 1) {
			for (World w : plugin.getServer().getWorlds()) {
				w.setThundering(true);
			}
		}else{
			for (World w : plugin.getServer().getWorlds()) {
				w.setThundering(false);
			}
		}
	}
	
	private void do_meteo() {
		if(meteotick > 50) {
			meteotick = 0;
			if(meteo_monde_type == 2) {
				if(meteo_monde_tick > 8000) {
					meteo_monde_tick = 0;
					meteo_monde_type = 1;
			    	setStorm(false);
			    	setThundering(false);
				}else if(meteo_monde_tick > 4000) { 
					if(!isStorm()) {
				    	setStorm(true);
				    	setThundering(true);
					}
				}else{
					if(!isStorm()) {
				    	setStorm(true);
				    	setThundering(true);
					}
				}
			}else if(meteo_monde_type == 1) {
				if(meteo_monde_tick > 60000) {
					meteo_monde_tick = 0;
					meteo_monde_type = 2;
			    	setStorm(true);
			    	setThundering(true);
				}else if(meteo_monde_tick > 58000) {
					if (meteo_monde_storm == 0) {
						meteo_monde_storm = showRandomInteger(1, 10, rand);
					}
					if (meteo_monde_storm > 1) {
						for (Player p : plugin.getServer().getOnlinePlayers()) {
			    			World pworld = p.getWorld();
				    		if(showRandomInteger(1, 100, rand) < 20) {
					    		Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(100, 200, rand), 128, p.getLocation().getBlockZ()-showRandomInteger(100, 200, rand));
					    		pworld.strikeLightningEffect(new Location(loc.getWorld(), loc.getBlockX(), 120, loc.getBlockZ()));
					    	}
						}
					}
					if(isStorm()) {
						setStorm(false);
				    	setThundering(false);
					}
				}else if(meteo_monde_tick < 1000) {
					if (meteo_monde_storm > 1) {
						for (Player p : plugin.getServer().getOnlinePlayers()) {
			    			World pworld = p.getWorld();
				    		if(showRandomInteger(1, 100, rand) < 20) {
					    		Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(100, 200, rand), 128, p.getLocation().getBlockZ()-showRandomInteger(100, 200, rand));
					    		pworld.strikeLightningEffect(new Location(loc.getWorld(), loc.getBlockX(), 120, loc.getBlockZ()));
					    	}
						}
					}
				}else if(meteo_monde_tick > 1000 && meteo_monde_tick < 1500) {
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
	    	storm_next_tick = showRandomInteger(50, 250, rand);
	    	if(meteo_monde_storm > 1) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(plugin.Main_Visiteur.is_visiteur(p)) {
	    				continue;
	    			}
	    			if(p.getWorld().hasStorm() && p.getWorld().isThundering()) {
	    				World pworld = p.getWorld();
		    			if(pworld.getHighestBlockYAt(p.getLocation())-10 < p.getLocation().getBlockY()) {
			    			if(showRandomInteger(1, 2, rand) == 2) {
			    				if(showRandomInteger(1, 100, rand) < 80) {
					    			if(showRandomInteger(1, 2, rand) == 1) {
					    				Location loc = new Location(pworld, p.getLocation().getBlockX()+showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()+showRandomInteger(1, 50, rand));
					    				pworld.strikeLightningEffect(new Location(loc.getWorld(), loc.getBlockX(), p.getWorld().getHighestBlockYAt(loc), loc.getBlockZ()));
					    			}else{
					    				Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 50, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 50, rand));
					    				pworld.strikeLightningEffect(new Location(loc.getWorld(), loc.getBlockX(), p.getWorld().getHighestBlockYAt(loc), loc.getBlockZ()));
					    			}
			    				}else{
			    					Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 16, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 16, rand));
			    					pworld.strikeLightning(new Location(loc.getWorld(), loc.getBlockX(), p.getWorld().getHighestBlockYAt(loc), loc.getBlockZ()));
			    				}
			    			}
		    			}
	    			}
	    		}
	    	}
    	}else{
    		storm_tick_n++;
    	}
	}
	
	/*if(p.isOnline() && p.getWorld().getName().contains("deathworld")) {
		if(p.getWorld().hasStorm() && p.getWorld().isThundering()) {
			World pworld = p.getWorld();
			if(pworld.getHighestBlockYAt(p.getLocation())-20 < p.getLocation().getBlockY()) {
				if(showRandomInteger(1, 2, rand) == 2) {
					if(showRandomInteger(1, 100, rand) < 80) {
		    			if(showRandomInteger(1, 2, rand) == 1) {
		    				Location loc = new Location(pworld, p.getLocation().getBlockX()+showRandomInteger(1, 16, rand), 0, p.getLocation().getBlockZ()+showRandomInteger(1, 16, rand));
		    				pworld.strikeLightning(new Location(loc.getWorld(), loc.getBlockX(), p.getWorld().getHighestBlockYAt(loc), loc.getBlockZ()));
		    			}else{
		    				Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 16, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 16, rand));
		    				pworld.strikeLightning(new Location(loc.getWorld(), loc.getBlockX(), p.getWorld().getHighestBlockYAt(loc), loc.getBlockZ()));
		    			}
					}else{
						Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(1, 3, rand), 0, p.getLocation().getBlockZ()-showRandomInteger(1, 3, rand));
						pworld.strikeLightning(new Location(loc.getWorld(), loc.getBlockX(), p.getWorld().getHighestBlockYAt(loc), loc.getBlockZ()));
					}
				}
			}
		}
	}else{*/
	//}
	
	/*private void do_cron() {
		if(wave_cron > next_wave_cron) {
			if(wave_done_cron > 3000) {
				next_wave_cron = showRandomInteger(5000, 30000, rand);
				wave_done_cron = 0;
				wave_cron = 0;
				plugin.deathwave = false;
			}else{
				plugin.deathwave = true;
				wave_done_cron++;
			}
		}else{
			wave_cron++;
		}
		
		if(cron_tick > 200) {
			cron_tick = 0;
			World deathworld = plugin.getServer().getWorld("deathworld");
			long deathworld_time = deathworld.getTime();
			World spawndome = plugin.getServer().getWorld("spawndome");
			if(!is_burningday) {
				if((deathworld_time >= 20000 && deathworld_time < 24000) || deathworld_time < 12900) {
					deathworld.setTime(14000);
					burningday_ntick++;
					plugin.sendDebug("DeadDay: Night tick");
				}
		    }else{
		    	if(deathworld_time < 23100 && deathworld_time > 12900) {
			    	deathworld.setTime(14000);
					is_burningday = false;
					is_preburningday = false;
					plugin.sendDebug("DeadDay: Stop burning day.");  
		    	}
		    }
			if(spawndome != null) {
				long spawndome_time = spawndome.getTime();
			    if(spawndome_time < 23100 && spawndome_time > 12900) {
			    	spawndome.setTime(23100);
			    }
			}
		}else{
			cron_tick++;
		}
    	
    	if(!is_preburningday) {
    		if(burningday_ntick >= 10) {
    			is_preburningday = true;
    			burningday_ntick = 0;
    			plugin.sendDebug("DeadDay: Pre burning day.");  
    		}
    	}
	
    	if (is_preburningday) {
    		if(!is_burningday) {
	    		if(is_preburningday_tick >= 500) {
	    			is_burningday = true;
	    			setTime("deathworld", 2000);
	    			plugin.Main_MessageControl.sendMessageToAll(ChatColor.RED + "[DEATHWORLD] "+ ChatColor.GOLD + "Le "+ ChatColor.YELLOW + "soleil"+ ChatColor.GOLD + " se lève sur le monde DeathWorld.");
		    		plugin.sendDebug("DeadDay: Start burning day."); 
		    		plugin.setServerConfig("informations.pre_burningday", false);
	    		}else{
	    			is_preburningday_tick++;
	    			plugin.setServerConfig("informations.pre_burningday", true);
	    		}
    		}
    	}else{
    		is_burningday = false;
    		is_preburningday_tick = 0;
    	}
		
		if(is_burningday_tick > 35) {
			is_burningday_tick = 0;
			if(is_burningday) {
				dayburning_tick();
			}
	 	}
		is_burningday_tick++;
	}*/
}