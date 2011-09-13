package mineworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import me.taylorkelly.bigbrother.datablock.Chat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.RenderDistance;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Main_TimeControl {
	
    private final Main plugin;
	private final Random rand = new Random();
	List<UUID> player_horde = new ArrayList<UUID>();
	List<UUID> is_goule = new ArrayList<UUID>();
    
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
	public boolean in_cycle = false;
	private long next_cycle = 0;
	private int cycle_id = 0;
	
	public boolean dead_sun = false;
	
	// HORDE
	//private long last_horde = 0;
	private long horde_uid = 0;
	public boolean horde = false;
	public boolean prehorde = false;
    
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
	
	public boolean isDay() {
		World w = plugin.getServer().getWorld("world");
		return w.getTime() < 12000 || w.getTime() == 24000;
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
			Location slocation = new Location(world, location.getX(), world.getHighestBlockYAt(location)+1, location.getZ());
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
				if((pworld.getHighestBlockYAt(p.getLocation())-2) < p.getLocation().getBlockY()) {
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
			String wind_what = "sans";
			if(storm_force > 5) {
				wind = true;
				wind_direction = new Location(null, showRandomInteger(-10000, 10000, rand), 0, showRandomInteger(-10000, 10000, rand));
				wind_force = storm_force;
				wind_what = "avec";
			}else{
				wind = false;
			}
			plugin.Main_ContribControl.bool_clouds(true);
			plugin.Main_ContribControl.set_fog(RenderDistance.TINY);
			Chat bb = new Chat((Player) plugin.chicken, "Un orage de force "+storm_force+" "+wind_what+" vents violent tombe sur MineWorld.", "world");
			bb.send();
		}
		if(plugin.timetamps < (cycle_start+30)) {
			if(showRandomInteger(1, 100, rand) < 5+(storm_force*5)) {
				plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/thunder_distant"+ showRandomInteger(1, 3, rand) +".wav");
	    	}
		}else if(plugin.timetamps >= (cycle_start+30) && plugin.timetamps < (cycle_end-30)) {
			if(!isStorm()) {
		    	setStorm(true);
		    	setThundering(true);
			}
	    	if(plugin.timetamps >= storm_next_tick) {
				plugin.Main_ContribControl.set_fog(RenderDistance.TINY);
		    	storm_next_tick = (plugin.timetamps+showRandomInteger(1, (14-storm_force), rand));
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(plugin.Main_Visiteur.is_visiteur(p)) {
	    				continue;
	    			}
    				World pworld = p.getWorld();
	    			if((pworld.getHighestBlockYAt(p.getLocation())-15) < p.getLocation().getBlockY()) {
		    			if(showRandomInteger(1, 2, rand) == 2) {
		    				Location loc = null;
		    				if(showRandomInteger(1, 100, rand) < (30+(storm_force*5))) {
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
			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/rur5b_cricket_loopl.wav");
			Chat bb = new Chat((Player) plugin.chicken, "La pluie tombe sur MineWorld.", "world");
			bb.send();
		}
		if(plugin.timetamps > (cycle_start+10)) {
			if(!isStorm() || isThundering()) {
		    	setStorm(true);
		    	setThundering(false);
			}
		}
	}
	
	private void cycle_horde() {
		if(horde_uid != cycle_uid) {
			cycle_start = plugin.timetamps;
			cycle_end = (plugin.timetamps+showRandomInteger(70, (60*10), rand));
			horde_uid = cycle_uid;
		}
		long time = plugin.getServer().getWorld("world").getTime();
		time = time - time % 24000;
		if((plugin.timetamps >= cycle_start && plugin.timetamps < cycle_start+15) && !prehorde) {
			prehorde = true;
			plugin.Main_ChunkControl.ResendAll.clear();
			plugin.Main_ChunkControl.player_chunkupdate.clear();
			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/RUR5b_DistFoghorn.wav");
		}else if((plugin.timetamps >= cycle_start+15 && plugin.timetamps < (cycle_start+25)) && !horde && prehorde) {
				horde = true;
				plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/mega_mob_incoming.wav");
				plugin.Main_ContribControl.sendNotificationToAll("Attention", "Une horde arrive!", Material.MOB_SPAWNER);
				plugin.Main_MessageControl.sendTaggedMessageToAll("Oh non, une horde de monstre déferle sur MineWorld !", 1, "[HORDE]");
				plugin.Main_MessageControl.sendTaggedMessageToAll("Abritez-vous dans vos bunkers !", 1, "[HORDE]");
				Chat bb = new Chat((Player) plugin.zombie, "Oh non, une horde de monstre déferle sur MineWorld...", "world");
				bb.send();
				setTime("world", time + 37700);
				plugin.H.mort = 0;
				player_horde.clear();
				is_goule.clear();
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if(plugin.Main_Visiteur.is_visiteur(p) || p.isOp()) {
						continue;
					}
					if(plugin.Main_ContribControl.isClient(p, false)) {
						player_horde.add(p.getUniqueId());
					}else{
						plugin.Main_MessageControl.sendTaggedMessage(p, "Vous n'avez pas le client MW.", 1, "[HORDE]");
						plugin.Main_MessageControl.sendTaggedMessage(p, "Vous ne ferez pas partie de cette manche.", 1, "[HORDE]");
					}
					SpoutManager.getPlayer(p).setWalkingMultiplier(0.5);
				}
				plugin.Main_ContribControl.bool_clouds(true);
				plugin.Main_ContribControl.set_fog(RenderDistance.TINY);
			    setStorm(true);
			    setThundering(false);
		}else if(plugin.timetamps >= (cycle_start+25) && plugin.timetamps < (cycle_end-20)) {
			if(!isStorm() || isThundering()) {
		    	setStorm(true);
		    	setThundering(false);
			}
			for (UUID puuid : player_horde) {
				SpoutPlayer player = plugin.CC.sp.getPlayerFromId(puuid);
				if(!player.getWorld().getName().contains("world")) {
					player_horde.remove(puuid);
				}
			}
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (is_goule.contains(p)) {
	    			plugin.CC.setPlayerTitle(p, ChatColor.DARK_RED.toString() + "Infectée");
				}else{
					plugin.CC.setPlayerTitle(p, ChatColor.DARK_GREEN.toString() + "Survivant");
				}
				SpoutManager.getPlayer(p).setWalkingMultiplier(0.5);
			}
			plugin.CC.set_fog(RenderDistance.TINY);
			if(isDay()) {
				setTime("world", time + 37700);
			}
			if(showRandomInteger(1, 100, rand) < 40) {
				int randthe = showRandomInteger(1, 6, rand);
				if(randthe == 1) {
					plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/dist_explosion_0"+ showRandomInteger(1, 4, rand) +".wav", 50);
				}else if(randthe == 2) {
					plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/dist_gun_0"+ showRandomInteger(1, 5, rand) +".wav", 50);	
				}else if(randthe == 3) {
					plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/dist_machinegun_0"+ showRandomInteger(1, 6, rand) +".wav", 50);
				}else if(randthe == 4) {
					plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/wood_debris_0"+ showRandomInteger(1, 8, rand) +".wav", 40);
				}else if(randthe == 5) {
					plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/dist_pistol_0"+ showRandomInteger(1, 2, rand) +".wav", 30);
				}else if(randthe == 6) {
					plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/city_battle"+ showRandomInteger(1, 19, rand) +".wav", 30);
				}
			}
		}else if(plugin.timetamps >= (cycle_end-20) && plugin.timetamps < cycle_end && horde) {
			horde = false;
			prehorde = false;
			plugin.Main_ChunkControl.ResendAll.clear();
			plugin.Main_ChunkControl.player_chunkupdate.clear();
			plugin.Main_ContribControl.stopAllSound();
			plugin.Main_ContribControl.set_fog(RenderDistance.FAR);
			plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/RUR5b_DistFoghorn.wav");
			plugin.Main_MessageControl.sendTaggedMessageToAll("La horde est terminée !", 1, "[HORDE]");
    		for (Entity e : plugin.getServer().getWorld("world").getEntities()) {
    			if (e instanceof Monster) {
    				e.remove();
    			}
    		}
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if (MobDisguiseAPI.isDisguised(p)) {
    				MobDisguiseAPI.undisguisePlayerAsPlayer(p, "zombie");
    			}
    			if(plugin.can_horde.contains(p.getUniqueId())) {
    				plugin.can_horde.remove(p.getUniqueId());
    			}
    			SpoutManager.getPlayer(p).setWalkingMultiplier(1);
    		}
    		plugin.Main_ContribControl.sp.getAppearanceManager().resetAllTitles();
			total_horde();
			setTime("world", time + 24000);
	    	setStorm(false);
	    	setThundering(false);
		}
	}
	
	public void total_horde() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		public void run()
				{
					plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/survival_teamrec.wav");
					plugin.Main_MessageControl.sendTaggedMessageToAll(plugin.H.mort +" morts au total.", 1, "[HORDE]");
					Chat bb = new Chat((Player) plugin.zombie, "La horde est terminée, nombre de mort pendant cette attaque : "+ plugin.H.mort +" Morts", "world");
					bb.send();
				}
		}, (long) 50);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				String survivant = "";
				plugin.conf_player.load();
				Boolean first = true;
				for (UUID puuid : player_horde) {
					SpoutPlayer player = plugin.CC.sp.getPlayerFromId(puuid);
					if(first) {
						survivant = player.getName();
						first = false;
					}else{
						survivant = survivant + ", "+ player.getName();
					}
					int psurvie = plugin.conf_player.getInt("load-player."+ player.getName() +".psurvie", 0);
					plugin.Main_MessageControl.sendTaggedMessage(player, "Vous avez reçut "+ ChatColor.GOLD + "1 point"+ ChatColor.WHITE + " de survie.", 1, "");
					plugin.Main_ContribControl.sendNotification(player, "Bravo !", "+1 SPoint(s)", Material.GOLD_ORE);
					psurvie++;
					plugin.setPlayerConfig(player, "psurvie", psurvie);
				}
				if(survivant == "") {
					survivant = "Aucun survivant";
				}
				plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/survival_playerrec.wav");
				plugin.Main_MessageControl.sendTaggedMessageToAll("Survivant(s) de cette manche : "+ survivant, 1, "[HORDE]");
				Chat bb = new Chat((Player) plugin.zombie, "Survivant(s) de cette manche : "+ survivant+".", "world");
				bb.send();
			}
		}, (long) 100);
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
					{
						int ratio = (Integer) plugin.getServerConfig("horde_ratio", "integer");
						int heure = (60*60);
						long next_horde = 0;
						if(ratio > 5) {
							next_horde = (plugin.timetamps+showRandomInteger(heure*3, heure*5, rand));
						}else {
							next_horde = (plugin.timetamps+showRandomInteger(heure, heure*3, rand));
						}
						plugin.setServerConfig("horde_limit", next_horde);
						plugin.Main_ContribControl.sendSoundEffectToAll("http://mineworld.fr/contrib/sound/pickup_misc42.wav");
						plugin.Main_MessageControl.sendTaggedMessageToAll("Prochaine attaque dans << "+ (int) Math.floor(next_horde/60)+" Minutes >>", 1, "[ESTIMATION-HORDE]");
					}
		}, (long) 150);
	}
	
	public void hordedone() {
		in_cycle = true;
		cycle_uid = plugin.timetamps;
		cycle_id = 5;
		cycle_end = 0;
		cycle_start = 0;
		//last_horde = plugin.timetamps;
		plugin.sendInfo("[CYCLE] New horde cycle ("+cycle_uid+").");
	}
	
	private void do_meteo() {
		if(in_cycle) {
			if(cycle_end != 0 && plugin.timetamps >= cycle_end) {
			    plugin.sendInfo("[CYCLE] STOP "+cycle_uid+".");
				in_cycle = false;
				horde = false;
				prehorde = false;
				next_cycle = (plugin.timetamps+showRandomInteger((60*5), (60*120), rand));
				cycle_uid = 0;
				cycle_end = 0;
				cycle_start = 0;
				plugin.Main_ContribControl.bool_clouds(false);
				plugin.Main_ContribControl.set_fog(RenderDistance.FAR);
			    setStorm(false);
			    setThundering(false);
			}else{
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
							cycle_horde();
							break;
						default:
							break;
					}
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
					cycle_end = 0;
					cycle_start = 0;
					plugin.sendInfo("[CYCLE] New cycle ("+cycle_uid+") "+current_gen_id+".");
					plugin.Main_ContribControl.bool_clouds(false);
					plugin.Main_ContribControl.set_fog(RenderDistance.FAR);
				    setStorm(false);
				    setThundering(false);
				}else if(current_gen_id == 5){
					int next = (Integer) plugin.getServerConfig("horde_next", "integer");
					if(next < plugin.timetamps && (plugin.getServer().getOnlinePlayers().length >= 3)) {
						in_cycle = true;
						cycle_uid = plugin.timetamps;
						cycle_id = current_gen_id;
						cycle_end = 0;
						cycle_start = 0;
						//last_horde = plugin.timetamps;
						plugin.sendInfo("[CYCLE] New horde cycle ("+cycle_uid+").");
					}
				}
			}else{
				setStorm(false);
				setThundering(false);
			}
		}
	}
}