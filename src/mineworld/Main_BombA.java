package mineworld;

/*import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;*/

public class Main_BombA  {
	
   /* private final Main plugin;
    
    List<Chunk> ChunkChecked = new ArrayList<Chunk>();
    private int explode_tick = 0;
    private Location location;
    private Block bomb;
    
    Boolean createenabled = true;
    
    private Boolean is_explode = false;
    public Boolean armed = false;
    public Boolean timed = false;
    
    private int force = 7;
    public int timed_tick = 0;
    private int timed_tick_msg = 0;
    private int tick = 0;
    
    Boolean msgall = false;
    
    Boolean step1 = false;
    Boolean step2 = false;
    Boolean step3 = false;
    Boolean step4 = false;
    Boolean step5 = false;
    
    Boolean is_destroyed = false;
    
    Main_BombA lastbomb;
    
    Boolean is_exploded = false;
    
    public Main_BombA(Main parent, Block block, int force) {
        this.plugin = parent;
        this.bomb = block;
        this.force = force;
        this.location = block.getLocation();
    }
    
	public Runnable runThread() {
		Thread thread = new Thread(new Runnable() {
			public void run()
			{
		    	try {
		    		do_cron();
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
	            return;
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(false);
		thread.run();
		return thread;
	}
	
	public void run() {
		if(!is_destroyed) {
			runThread();
		}
	}
	
    public void sendMessageToOP(String msg) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOp()) {
				p.sendMessage(msg);
			}
		}
    }
    
    public void sendMessageToAll(String msg) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.sendMessage(msg);
		}
    }
	
	private void do_cron() {
		if(is_explode) {
			do_explode();
		}else if(timed) {
			if(timed_tick > 2000) {
				timed = false;
				is_explode = true;
				plugin.setServerConfig("informations.nuke", false);
			}else{
				if(timed_tick_msg > 100) {
					timed_tick_msg = 0;
					int timerestant = Math.round(((2000 - timed_tick) / 100));
					if(msgall) {
						sendMessageToAll(ChatColor.DARK_RED + "Attention, explosion de la bombe dans " + timerestant + " secondes.");
					}else{
						sendMessageToOP(ChatColor.DARK_RED + "Temps restant : " + timerestant);
					}
					if(tick == 1) {
						bomb.setType(Material.GLOWSTONE);
						tick = 0;
					}else{
						bomb.setType(Material.OBSIDIAN);
						tick = 1;
					}
				}
				timed_tick_msg++;
				timed_tick++;
			}
		}
	}
	
	public void arme(Boolean armedb) {
		this.armed = armedb;
	}
	
	public void timed() {
		if(armed && is_exploded == false) {
			if(msgall) {
				sendMessageToAll(ChatColor.DARK_RED + "Activation d'une BombeA dans "+ location.getWorld().getName()+".");
			}
			this.timed = true;
			plugin.setServerConfig("informations.nuke", true);
		}
	}
	
	public void explode() {
		if(armed && is_exploded == false) {
			this.is_explode = true;
			plugin.setServerConfig("informations.nuke", false);
		}
	}
	
	private void do_crater(int radius) {
		
		if(createenabled) {
			
			plugin.setServerConfig("nukes.nuke_"+ bomb.getX() + bomb.getY()+".x", bomb.getX());
			plugin.setServerConfig("nukes.nuke_"+ bomb.getX() + bomb.getY()+".y", bomb.getY());
			plugin.setServerConfig("nukes.nuke_"+ bomb.getX() + bomb.getY()+".z", bomb.getZ());
			plugin.setServerConfig("nukes.nuke_"+ bomb.getX() + bomb.getY()+".world", bomb.getWorld().getName());
			
	        for (int x = location.getBlockX() - 4; x <= location.getBlockX() + 4; x++) {
	            for (int z = location.getBlockZ() - 4; z <= location.getBlockZ() + 4; z++) {
	                for (int y = location.getBlockY() - 4; y <= location.getBlockY() + 4; y++) {
	                	Block block = location.getWorld().getBlockAt(x, y, z);
		                World world = ((CraftWorld) block.getWorld()).getHandle();
		                block.setTypeId(0);
		                EntityTNTPrimed tnt = new EntityTNTPrimed((net.minecraft.server.World) world, x, y, z);
		                world.addEntity(tnt);
		                world.makeSound(tnt, "random.fuse", 1.0F, 1.0F);
		                block.getWorld().strikeLightning(new Location(location.getWorld(), x, y, z));
	                }
	            }
	        }
			
	        for (int x = location.getBlockX() - radius*3; x <= location.getBlockX() + radius*3; x++) {
	            for (int z = location.getBlockZ() - radius*3; z <= location.getBlockZ() + radius*3; z++) {
	                for (int y = location.getBlockY() - radius*3; y <= location.getBlockY() + radius*3; y++) {
	                	Block block = location.getWorld().getBlockAt(x, y, z);
	                	if(block.getType() != Material.BEDROCK) {
			                if(block.getTypeId() != 0 && block.getTypeId() != 49 && (block.getTypeId() != 8 && block.getTypeId() != 9)) {
			                    block.setType(Material.FIRE);
		                    }else if(block.getTypeId() == 8 || block.getTypeId() == 9) {
		                    	block.setType(Material.LAVA);
		                    }else if(block.getType() == Material.GRASS) {
		                    	block.setType(Material.LAVA);
		                    }else{
		                    	if(block.getTypeId() != 0) { block.setType(Material.AIR); }
		                    }
	                	}
	                }
	            }
	        }
	        
	        for (int x = location.getBlockX() - 15; x <= location.getBlockX() + 15; x++) {
	            for (int z = location.getBlockZ() - 15; z <= location.getBlockZ() + 15; z++) {
	                for (int y = location.getBlockY() - 15; y <= location.getBlockY() + 15; y++) {
	                	Block block = location.getWorld().getBlockAt(x, y, z);
		                if(block.getType() == Material.LEAVES) {
		                	block.setType(Material.FIRE);
	                    }
	                }
	            }
	        }
		}
	}
	
	private void do_explode() {
		Vector b_v = new Vector(this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
		if(explode_tick > 1500) { return; }
		if(explode_tick < 50 && is_exploded == false) {
			sendMessageToAll(ChatColor.DARK_RED + "Une BombeA vient d'exploser sur le monde "+ location.getWorld().getName()+".");
			is_exploded = true;
			int forcecra = force;
			if(forcecra > 10) { forcecra = 10; }
			do_crater(forcecra);
		}else if(explode_tick > 70 && step1 == false) {
			this.location.getWorld().setTime(5000);
			step1 = true;
			for (Entity e : bomb.getWorld().getEntities()) {
				double range = plugin.getdistance(e, bomb);
				if(range < 100.0) {
					if (e instanceof Player) {
						Vector p_v = new Vector(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ());
						Vector difference = b_v.subtract(p_v);
						Vector fApplied = difference.multiply( 1 / range );
						e.setVelocity(fApplied);
						World world = ((CraftWorld) e.getWorld()).getHandle();
						world.makeSound(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ(), "random.explode", 1.0F, 1.0F);
						((Player) e).damage(1000, plugin.bomb_e);
					}else if (e instanceof Creature) {
						e.remove();
					}
				}
			}
		}else if(explode_tick > 90 && step2 == false) {
			this.location.getWorld().setTime(5000);
			step2 = true;
			for (Entity e : bomb.getWorld().getEntities()) {
				Vector p_v = new Vector(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ());
				Vector difference = b_v.subtract(p_v);
				double range = plugin.getdistance(e, bomb);
				if (e instanceof Player || e instanceof Creature) {
					if(range < 400.0 && range > 100.0 && bomb.getWorld().getBlockAt(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ()).getLightLevel() > 10) {
						Vector fApplied = difference.multiply( 1 / range );
						((LivingEntity) e).damage(1, plugin.bomb_e);
						e.setFireTicks(4000);
						e.setVelocity(fApplied);
						if (e instanceof Player) {
							 World world = ((CraftWorld) e.getWorld()).getHandle();
							 world.makeSound(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ(), "random.explode", 1.0F, 1.0F);
							((Player) e).sendMessage(ChatColor.DARK_GREEN +"Vous avez reçu l'onde de choc de la BombeA.");
						}
					}else if(range < 400.0 && range > 100.0) {
						Vector fApplied = difference.multiply( 1 / range );
						e.setFireTicks(30);
						e.setVelocity(fApplied);
						if (e instanceof Player) {
							 World world = ((CraftWorld) e.getWorld()).getHandle();
							 world.makeSound(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ(), "random.explode", 1.0F, 1.0F);
							((Player) e).sendMessage(ChatColor.DARK_GREEN +"Vous avez senti le choc de la BombeA.");
						}
					}
				}
			}
		}else if(explode_tick > 250 && step3 == false) {
			step3 = true;
			for (Entity e : bomb.getWorld().getEntities()) {
				Vector p_v = new Vector(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ());
				Vector difference = b_v.subtract(p_v);
				double range = plugin.getdistance(e, bomb);
				if(range < 1000.0 && range > 400.0) {
					Vector fApplied = difference.multiply( 1 / range );
					if (e instanceof Player || e instanceof Creature) {
						e.setVelocity(fApplied);
						e.setFireTicks(20);
						if (e instanceof Player) {
							World world = ((CraftWorld) e.getWorld()).getHandle();
							world.makeSound(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ(), "ambient.weather.thunder3", 1.0F, 1.0F);
							((Player) e).sendMessage(ChatColor.DARK_GREEN +"Le sol tremble sous l'effet de la BombeA.");
						}
					}
				}
			}
			is_explode = false;
		}
		explode_tick++;
	}*/
}