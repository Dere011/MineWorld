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
    
    public int meteo_monde_tick = 0;
    public int meteo_monde_type = 3;
    public int meteo_monde_storm = 0;
    
    private int storm_next_tick;
    private int storm_tick_n = 0;
    private int meteotick = 0;
    
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
		if(meteotick > 5) {
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
				}else if(meteo_monde_tick < 100) {
					if (meteo_monde_storm > 1) {
						for (Player p : plugin.getServer().getOnlinePlayers()) {
			    			World pworld = p.getWorld();
				    		if(showRandomInteger(1, 100, rand) < 20) {
					    		Location loc = new Location(pworld, p.getLocation().getBlockX()-showRandomInteger(100, 200, rand), 128, p.getLocation().getBlockZ()-showRandomInteger(100, 200, rand));
					    		pworld.strikeLightningEffect(new Location(loc.getWorld(), loc.getBlockX(), 120, loc.getBlockZ()));
					    	}
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
	    	storm_next_tick = showRandomInteger(5, 25, rand);
	    	if(meteo_monde_storm > 1) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(plugin.Main_Visiteur.is_visiteur(p)) {
	    				continue;
	    			}
	    			if(isStorm() && isThundering()) {
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
}