package mineworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

import npcspawner.BasicHumanNpc;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.*;
import org.bukkit.util.config.Configuration;

public class Main extends JavaPlugin {

	PluginManager pm;
    final Logger logger = Logger.getLogger("Minecraft");
    PluginDescriptionFile pdfFile;
    
    Main_EntityListener Main_EntityListener;
    Main_PlayerListener Main_PlayerListener;
    Main_MoveControl Main_MoveControl;
    Main_BlockListener Main_BlockListener;
    Main_Visiteur Main_Visiteur;
    Main_CommandsControl Main_CommandsControl;
    Main_MessageControl Main_MessageControl;
    
    public static Main_Visiteur Main_Visiteur_n;
    
    final Random rand = new Random();
    
    public Main_NPC Main_NPC;
    public Main_TimeControl Main_TimeControl;
    public Main_ChunkControl Main_ChunkControl;
    
    public List<Chunk> DontChunkChecked = new ArrayList<Chunk>();
    public List<Player> DontChunkCheckedPlayer = new ArrayList<Player>();
    
    public List<String> modo = new ArrayList<String>();
    public List<String> correct = new ArrayList<String>();
    public List<String> anim = new ArrayList<String>();
    
    public List<Player> world_whitelist = new ArrayList<Player>();
    
    public Map<Entity, Block> move_last = new HashMap<Entity, Block>();

	protected static File maindir = new File("plugins" + File.separatorChar + "MineWorld");
	protected File NPC_configFile = new File(maindir, "npc_config.yml");
	protected File Player_configFile = new File(maindir, "player_config.yml");
	protected File Server_configFile = new File(maindir, "server_config.yml");
	
	Configuration conf_player = new Configuration(Player_configFile);
	Configuration conf_npc = new Configuration(NPC_configFile);
	Configuration conf_server = new Configuration(Server_configFile);
	
	public Boolean npc_is_first_loaded = false;
	private Boolean debug_enable = false;
	
	/*
	public Map<Creature, List<LivingEntity>> attackedby = new HashMap<Creature, List<LivingEntity>>();
	public Boolean is_dayburning = false;
	public Boolean is_predayburning = false;
	public Boolean followbot = false;
	public Boolean followbot_see = false;
	public Boolean deathwave = false;
	public Boolean deathwavetarget = false;
	public Boolean nuke = false;
	public Boolean sure = false;
	public int isdamagedtick_tick = 0;
	public int cron_radiation = 0;
	public int wavetimer = 8;
	public int cron_tick_player = 0;
	public int cron_tick_hole = 0;
	public int cron_tick_rpname = 0;
	public Boolean hole = false;
	public Location nukeLocation;
	*/
	
	public Boolean burn = false;
	
	public int cron_tick_stats = 0;
	public int cron_tick = 0;
	public int cron_tick_gen = 0;
	public int number_creature = 0;
	public int cron_tick_heal = 0;

	public long timetamps = 0;
	
	public int lastreload = 0;
	public int lasttimereload = 0;
	public int lastplayerleft = 0;
	public int lastfirestick = 0;
	public int is_predayburning_tick = 0;
    public int lastburnstart = 0;
    public int is_core_tick = 0;
    public int is_skin_tick = 0;
    public int is_FOLLOW_tick = 0;
    public int refreshchunkt = 0;
    public int cron_tick_deathwave = 0;
    public int burnradius = 10;
    public int cron_tick_mobcontrol = 0;
    
    public boolean thor = false;
    public int cron_storm = 0;
    
    Thread t_01;
    Thread t_02;
    Thread t_03;
    
    private int tickticklatick = 0;
    
    //public Entity bomb_e;
    //public Entity bomb_r;
	//public Main_BombA lastbomb;
	//public Block lasthole;
	
    public static Entity chicken;
    public static Entity zombie;
    public static Entity slime;
    public static Entity spider;
    
    // Maintenance
    public Boolean maintenance_status = false;
    public String maintenance_message = "Notre serveur est actuellement en maintenance.";
    
    public void onEnable() {
    	PluginManager pm = getServer().getPluginManager();
    	PluginDescriptionFile pdfFile = getDescription();
    	
        Main_NPC = new Main_NPC(this);
        Main_TimeControl = new Main_TimeControl(this);
        Main_ChunkControl = new Main_ChunkControl(this);
        Main_EntityListener = new Main_EntityListener(this);
        Main_PlayerListener = new Main_PlayerListener(this);
        Main_MoveControl = new Main_MoveControl(this);
        Main_BlockListener = new Main_BlockListener(this);
        Main_Visiteur = new Main_Visiteur(this);
        Main_CommandsControl = new Main_CommandsControl(this);
        Main_MessageControl = new Main_MessageControl(this);
        
        pm.registerEvent(Type.PLAYER_JOIN, Main_PlayerListener, Priority.Normal, this); 
        pm.registerEvent(Type.PLAYER_QUIT, Main_PlayerListener, Priority.Normal, this); 
        pm.registerEvent(Type.PLAYER_INTERACT, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_PRELOGIN, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_CHAT, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_TOGGLE_SNEAK, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_PICKUP_ITEM, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_MOVE, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_RESPAWN, Main_PlayerListener, Priority.Normal, this);
        
        pm.registerEvent(Type.ENTITY_DAMAGE, Main_EntityListener, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_DEATH, Main_EntityListener, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_TARGET, Main_EntityListener, Priority.Normal, this);
        
        pm.registerEvent(Type.BLOCK_IGNITE, Main_BlockListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, Main_BlockListener, Priority.Normal, this); 
        pm.registerEvent(Type.BLOCK_DAMAGE, Main_BlockListener, Priority.Normal, this); 
        pm.registerEvent(Type.BLOCK_PLACE, Main_BlockListener, Priority.Normal, this);
        
    	runAllThread();
    	
    	try {
			Main_Visiteur.charge_whitelist();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        logger.log(Level.INFO, pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled.");
    }
    
	public Runnable runThread_01() {
		if(this.t_01 == null) {
			this.t_01 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_01();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_01.setPriority(Thread.MIN_PRIORITY);
			this.t_01.setDaemon(true);
		}
		return this.t_01;
	}
	
	public Runnable runThread_02() {
		if(this.t_02 == null) {
			this.t_02 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_02();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_02.setPriority(Thread.MIN_PRIORITY);
			this.t_02.setDaemon(true);
		}
		return this.t_02;
	}
	
	public Runnable runThread_03() {
		if(this.t_03 == null) {
			this.t_03 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_03();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_03.setPriority(Thread.MIN_PRIORITY);
			this.t_03.setDaemon(true);
		}
		return this.t_03;
	}
    
	int showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    long range = (long)aEnd - (long)aStart + 1;
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);
	    return randomNumber;
	}
	
	public long timetamps() {
		timetamps = System.currentTimeMillis() / 1000L;
		return timetamps;
	}
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	    try {
	        if (!command.getName().toLowerCase().equals("mn") && !command.getName().toLowerCase().equals("mineworld")) {
	            return false;
	        }
	        Main_CommandsControl.Main_onCommand_do(sender, command, commandLabel, args);
	    	if(sender instanceof Player) {
	    		Main_NPC.NPC_onCommand_do(sender, command, commandLabel, args);
	    	}
	        return true;
	    } catch (Exception e) {
	        sender.sendMessage("[MINEWORLD] Une erreur est survenue.");
	        sendError(e.getMessage() + e.getStackTrace().toString());
	        e.printStackTrace();
	        return true;
	    }
    }
    
    public void onDisable() {
        try {
        	Main_NPC.HumanNPCList.removeAllNpc();
            logger.log(Level.INFO, "MineWorld disabled.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "MineWorld : error: " + e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            return;
        }
    }
    
    public Boolean playerInServer() {
    	if(getServer().getOnlinePlayers().length > 0) {
    		return true;
    	}
    	return false;
    }
    
    public void removeallitems() {
		for (World w : getServer().getWorlds()) {
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
    
    /*public void Holerun() {
    	if(cron_tick_hole > 1) {
    		cron_tick_hole = 0;
	    	Location loc = lasthole.getLocation();
	    	for (Entity e : loc.getWorld().getEntities()) {
				if (e instanceof Creature || e instanceof Player) {
					double range = getdistance(e, loc);
					if (range < 100) {
						Main_MoveControl.attireEntity(e, loc);
					}
	    		}
			}
	    }else{
			cron_tick_hole++;
		}
    }*/
    
	//if(nuke && lastbomb != null) { lastbomb.run(); }
	//if(hole && lasthole != null) { Holerun(); }
    
    /*if(deathwave) {
		if(cron_tick_deathwave > wavetimer) {
			cron_tick_deathwave = 0;
    		for (Entity e : getServer().getWorld("deathworld").getEntities()) {
    			if (e instanceof Creature) {
    				if (e instanceof Monster || e instanceof Chicken) {
    					double lastdistance = 1000000.0;
    					Entity thetarget = null;
	    				for (Entity ee : e.getNearbyEntities(75.0, 75.0, 75.0)) {
	    					if (ee instanceof Player) {
	    						if(((Creature) e).getTarget() == null) {
		    						if(!((Player) ee).isOp() && !ee.isDead() && ((Player) ee).isOnline()) {
		    							double distance = getdistance(ee, e);
		    							if (distance < lastdistance) {
			    							thetarget = ee;
			    							lastdistance = distance;
		    							}
		    						}
	    						}else{
	    							if(deathwavetarget) {
			    						if(!((Player) ee).isOp() && !ee.isDead() && ((Player) ee).isOnline()) {
			    							double distance = getdistance(ee, e);
			    							if (distance < lastdistance) {
				    							thetarget = ee;
				    							lastdistance = distance;
			    							}
			    						}
	    							}
	    						}
	    					}
	    				}
	    				if(thetarget != null) {
	    					double d = 8.0;
	    					if(deathwavetarget) {
	    						d = 4.0;
	    					}
		    				if (checkLocation(thetarget.getLocation(), e.getLocation(), d)) {
		    					((Creature) e).setTarget((LivingEntity) thetarget);
		    				}else{
		    					Main_MoveControl.moveCloserToLocation(e, thetarget.getLocation());
		    				}
    					}
	    			}
    			}
    		}
		}else{
			cron_tick_deathwave++;
		}
	}*/
    
			/*if(cron_storm > 250) {
		cron_storm = 0;
		conf_server.load();
		List<String> stormlist = conf_server.getKeys("load-storm");
		if(stormlist != null && !stormlist.isEmpty()) {
			ConfigurationNode node = conf_server.getNode("load-storm");
			for(String storm : stormlist){
				if(showRandomInteger(1, 100, rand) < 60) {
					Double x = node.getDouble(storm + ".x", 0);
					Double y = node.getDouble(storm + ".y", 0);
					Double z = node.getDouble(storm + ".z", 0);
					String w = node.getString(storm + ".world");
					getServer().getWorld(w).strikeLightning(new Location(getServer().getWorld(w), x, y, z));
				}
			}
		}
	}else{
		cron_storm++;
	}
	if(cron_radiation > 160) {
		cron_radiation = 0;
		Boolean isdamagedtick = false;
		if(isdamagedtick_tick > 1) {
			isdamagedtick_tick = 0;
			isdamagedtick = true;
		}else{
			isdamagedtick_tick++;
		}
		conf_server.load();
		List<String> nukeliste = conf_server.getKeys("nukes");
		if(nukeliste != null && !nukeliste.isEmpty()) {
			ConfigurationNode node = conf_server.getNode("nukes");
			for(String nuke : nukeliste){
	    		for (Player p : getServer().getWorld("deathworld").getPlayers()) {
					Double x = node.getDouble(nuke + ".x", 0);
					Double y = node.getDouble(nuke + ".y", 0);
					Double z = node.getDouble(nuke + ".z", 0);
					String w = node.getString(nuke + ".world");
					double range = getdistance(p.getLocation(), new Location(getServer().getWorld(w), x, y, z));
					if(range < 130) {
						if(p.getInventory().getHelmet().getTypeId() == 314 && p.getInventory().getChestplate().getTypeId() == 315 && p.getInventory().getLeggings().getTypeId() == 316) {
							if(isdamagedtick) {
								short helmetd = p.getInventory().getHelmet().getDurability();
								short plated = p.getInventory().getChestplate().getDurability();
								short leggd = p.getInventory().getLeggings().getDurability();
								p.getInventory().getHelmet().setDurability((short) (helmetd+1));
								p.getInventory().getChestplate().setDurability((short) (plated+1));
								p.getInventory().getLeggings().setDurability((short) (leggd+1));
							}
						}else{
							p.damage(1, bomb_r);
							p.sendMessage(ChatColor.DARK_GREEN +"Cette zone est sous des radiations.");
						}
					}
				}
			}
		}
	}else{
		cron_radiation++;
	}
	if(cron_tick_rpname > 150) {
		cron_tick_rpname = 0;
		for (Player p : getServer().getOnlinePlayers()) {
		    String rpname = (String) getPlayerConfig(p, "rpname", "string");
		    if(rpname != null && rpname != p.getDisplayName()) {
		    	if(rpname.length() < 15 && !p.isOp() && !rpname.contains("none")) {
		    		p.setDisplayName(ChatColor.DARK_GRAY+ "("+ p.getName() + ") "+ ChatColor.WHITE + rpname);
		    	}
		    }
		}
	}else{
		cron_tick_rpname++;
	}*/
    
    public boolean isbot(Player p) {
        BasicHumanNpc npc = Main_NPC.HumanNPCList.getBasicHumanNpc(p);
        if (npc != null) {
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public void do_cron_01() {
    	timetamps();
    	if(playerInServer()) {
			if(cron_tick_heal > 150) {
				Boolean terre = true;
				cron_tick_heal = 0;
				for (Player p : getServer().getOnlinePlayers()) {
			    	if (Main_Visiteur.is_visiteur(p)) {
			    		continue;
			    	}
					if(p.isSleeping()) {
						if(p.getHealth() < 10) {
							p.setHealth(p.getHealth()+1);
						}
					}else{
						if(p.getWorld().getName().contains("world")) {
							terre = false;
						}
					}
				}
				if(terre) {
					getServer().getWorld("world").setFullTime(23100);
				}
			}else{
				cron_tick_heal++;
			}
			lastplayerleft = 0;
    	}else{
			if(lastplayerleft > 5000) {
				lastplayerleft = 0;
				for (World w : getServer().getWorlds()) {
					for (Entity e : w.getEntities()) {
						if (e instanceof Creature) {
							((Creature) e).remove();
						}
					}
				}
			}else{
				lastplayerleft++;
			}
    	}
    }
    
    public void do_cron_02() {
    	if(playerInServer()) {
	    	if(cron_tick_gen > 1500) {
				cron_tick_gen = 0;
				number_creature = 0;
				for (World w : getServer().getWorlds()) {
					for (Entity e : w.getEntities()) {
						if (e instanceof Creature) {
							number_creature++;
						}
					}
				}
				Integer news_rev = (Integer) getServerConfig("informations.news_rev", "int");
				for (Player p : getServer().getOnlinePlayers()) {
			    	if (Main_Visiteur.is_visiteur(p)) {
			    		Main_MessageControl.sendVisiteurMsg(p);
			    		continue;
			    	}
				    boolean first_connexion = (Boolean) getPlayerConfig(p, "first_connexion", "boolean");
				    int last_news_rev = (Integer) getPlayerConfig(p, "last_news_rev", "int");
				    if(!first_connexion) {
				    	Main_MessageControl.sendSmallNotRegisteredMsg(p);
					}else if(last_news_rev < news_rev) {
						Main_MessageControl.sendSmallLastNews(p);
					}
				}
				setServerConfig("informations.number_mobs", number_creature);
		    	try {
					Main_Visiteur.charge_whitelist();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				cron_tick_gen++;
			}
			if(cron_tick_stats > 22000) {
				cron_tick_stats = 0;
				for (Player p : getServer().getOnlinePlayers()) {
			    	if (Main_Visiteur.is_visiteur(p)) {
			    		continue;
			    	}
					conf_player.load();
					int ppresences = conf_player.getInt("load-player."+ p.getName() +".ppresences", 0);
					Main_MessageControl.sendTaggedMessage(p, "Vous avez reçut "+ ChatColor.DARK_GREEN + "1 point"+ ChatColor.WHITE + " de présence.", 1, "");
					ppresences++;
					setPlayerConfig(p, "ppresences", ppresences);
				}
			}else{
				cron_tick_stats++;
			}
    	}
    }
    
    public void do_cron_03() {
    	if(playerInServer()) {
	    	if(tickticklatick > 150) {
	    		tickticklatick = 0;
	    		for (World w : getServer().getWorlds()) {
		    		for (Entity e : w.getLivingEntities()) {
						if (e instanceof Creature) {
							Creature c = (Creature) e;
							if(w.getBlockAt(c.getLocation()).getTypeId() == 8 || w.getBlockAt(c.getLocation()).getTypeId() == 9) {
								boolean playerinrange = false;
								for (Entity p : e.getNearbyEntities(13, 13, 13)) {
									if (p instanceof Player) {
										if(!Main_Visiteur.is_visiteur((Player) p)) {
											playerinrange = true;
											break;
										}
									}
								}
								if(!playerinrange) {
									c.remove();
								}
							}
						}
					}
	    		}
	    	}
	    	tickticklatick++;
	    	
	    	if(cron_tick_mobcontrol > 10) {
	    		cron_tick_mobcontrol = 0;
	    		if(!isDay(getServer().getWorld("world"))) {
		    		for (Entity e : getServer().getWorld("world").getEntities()) {
		    			if (e instanceof Creature) {
		    				if (e instanceof Monster) {
		    					double lastdistance = 1000000.0;
		    					Entity thetarget = null;
			    				for (Entity ee : e.getNearbyEntities(50, 50, 50)) {
			    					if (ee instanceof Player) {
			    						if(((Creature) e).getTarget() == null && !Main_Visiteur.is_visiteur((Player) ee)) {
				    						if(!((Player) ee).isOp() && !ee.isDead() && ((Player) ee).isOnline()) {
				    							double distance = getdistance(ee, e);
				    							if (distance < lastdistance) {
					    							thetarget = ee;
					    							lastdistance = distance;
				    							}
				    						}
			    						}
			    					}
			    				}
			    				if(thetarget != null) {
			    					double range = 12.0;
			    					if(thetarget.getLocation().getBlock().getLightLevel() < 8) {
			    						range = 6.0;
			    					}
				    				if (checkLocation(thetarget.getLocation(), e.getLocation(), range)) {
				    					((Creature) e).setTarget((LivingEntity) thetarget);
				    				}else{
				    					Main_MoveControl.moveCloserToLocation(e, thetarget.getLocation());
				    				}
		    					}
			    			}
		    			}
		    		}
	    		}
			}else{
				cron_tick_mobcontrol++;
			}
    	}
    }
    
    private void runAllThread() {
    	// Main
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_01(), 1, 1); 
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_02(), 1, 1); 
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_03(), 1, 1); 
    	
    	// Visiteur
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_Visiteur.runThread_1(this), 1, 25); 

    	// NPC
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_NPC.runThread(this), 1, 1); 
    	
    	// TIMECONTROL
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_TimeControl.runThread_meteo(this), 1, 1);
    	
    	// CHUNKCONTROL | ANTI-INVISIBLE
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_ChunkControl.runThread_1(this), 1, 50); 
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_ChunkControl.runThread_2(this), 1, 50);
    }
    
	public Object getNPCConfig(String npcname, String name, String type) {
    	conf_npc.load();
    	String config = "load-player."+ npcname +"."+ name;
      	if(type == "int") {
    		return conf_npc.getInt(config, 0);
    	}else if(type == "string") {
    		return conf_npc.getString(config);
    	}else if(type == "boolean") {
    		return conf_npc.getBoolean(config, false);
    	}
    	return false;
    }
	
	public boolean isDay(World world) {
		return world.getTime() < 12000 || world.getTime() == 24000;
	}
    
    public Object getPlayerConfig(Player player, String name, String type) {
    	conf_player.load();
    	String config = "load-player."+ player.getName() +"."+ name;
    	if(type == "int" || type == "float") {
    		return conf_player.getInt(config, 0);
    	}else if(type == "string") {
    		return conf_player.getString(config);
    	}else if(type == "boolean") {
    		return conf_player.getBoolean(config, false);
    	}
    	return false;
    }
    
    public Object getServerConfig(String name, String type) {
    	conf_server.load();
    	if(type == "int") {
    		return conf_server.getInt(name, 0);
    	}else if(type == "string") {
    		return conf_server.getString(name);
    	}else if(type == "boolean") {
    		return conf_server.getBoolean(name, false);
    	}
    	return false;
    }
    
    public void setPlayerConfig(Player player, String name, Object value) {
    	conf_player.load();
    	String config = "load-player."+ player.getName() +"."+ name;
    	conf_player.setProperty(config, value);
    	conf_player.save();
    }
    
    public void setServerConfig(String name, Object value) {
    	conf_server.load();
    	conf_server.setProperty(name, value);
    	conf_server.save();
    }
    
    public void sendError(String msg) {
    	logger.log(Level.WARNING, "MineWorld error: " + msg);
    }
    
    public void sendInfo(String msg) {
    	logger.log(Level.INFO, "MineWorld info: " + msg);
    }
    
    public void sendDebug(String msg) {
    	if(debug_enable) {
    		logger.log(Level.INFO, "MineWorld debug: " + msg);
    	}
    }
    
    public boolean checkLocation(Location loc, Location ploc, Double range) {
    	if ((ploc.getX() <= loc.getX() + range && ploc.getX() >= loc.getX()
    	- range)
    	&& (ploc.getY() >= loc.getY() - range && ploc.getY() <= loc
    	.getY() + range)
    	&& (ploc.getZ() >= loc.getZ() - range && ploc.getZ() <= loc
    	.getZ() + range))
    	return true;
    	else
    	return false;
    }
    
    public double getdistance(Entity ent1, Location loc) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - loc.getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - loc.getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - loc.getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Location loc2, Location loc) {
		double deltax = Math.abs(loc2
				.getX() - loc.getX());
		double deltay = Math.abs(loc2
				.getY() - loc.getY());
		double deltaz = Math.abs(loc2
				.getZ() - loc.getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Entity ent1, Block ent2) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - ent2.getLocation().getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - ent2.getLocation().getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - ent2.getLocation().getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Player ent1, Block ent2) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - ent2.getLocation().getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - ent2.getLocation().getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - ent2.getLocation().getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Entity ent1, Entity ent2) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - ent2.getLocation().getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - ent2.getLocation().getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - ent2.getLocation().getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
}