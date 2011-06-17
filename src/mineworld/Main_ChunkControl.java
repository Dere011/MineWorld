package mineworld;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import net.minecraft.server.EntityItem;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet51MapChunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Main_ChunkControl {
	
    private final Main plugin;
    Thread thread_01, thread_02, thread_03;
    List<Player> PlayerOR = new ArrayList<Player>();
    
    public Map<Player, ArrayList<Block>> player_blocs = new HashMap<Player, ArrayList<Block>>();
    public Map<Player, Boolean> player_chunkupdate = new HashMap<Player, Boolean>();
    public Map<Player, String> player_lastchunk = new HashMap<Player, String>();
    public Map<Chunk, ArrayList<Block>> cache_antixray = new HashMap<Chunk, ArrayList<Block>>();
	
    public Main_ChunkControl(Main parent) {
        this.plugin = parent;
    }
    
    // THREAD
    
	public Runnable runThread_1(final Main plugin) {
		if(thread_01 == null) {
			
			thread_01 = new Thread(new Runnable() {
			public void run()
			{
			    	try {
			    		if (plugin.playerInServer()) {
			    			anti_invisible_do();
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
	
	public Runnable runThread_2(final Main plugin) {
		if(thread_02 == null) {
			thread_02 = new Thread(new Runnable() {
			public void run()
			{
			    	try {
			    		if (plugin.playerInServer()) {
			    			do_orcontrol();
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
	
	public Runnable runThread_3(final Main plugin, final Player player) {
		Thread thread = new Thread(new Runnable() {
			public void run()
			{
				try {
					if (player.isOnline()) {
						CacheOnlyChunk_do(player);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		});
		return thread;
	}
	
	// ANTI INVISIBLE
	
	private void anti_invisible_tick(Player player) {
		for (Entity entity : player.getNearbyEntities(24, 24, 24)) {
			if (entity instanceof Player) {
				if(!((Player) entity).isOp()) {
					if(plugin.is_spy((Player) entity) && !plugin.isbot(player) && plugin.Main_Visiteur.is_visiteur((Player) entity) == plugin.Main_Visiteur.is_visiteur(player)) {
						CraftPlayer unHide = (CraftPlayer) player;
						CraftPlayer unHideFrom = (CraftPlayer) entity;
						unHide.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHideFrom.getHandle()));
					}
				}
			}else if (entity instanceof EntityItem) {
				CraftPlayer unHide = (CraftPlayer) player;
				EntityItem unHideFrom = (EntityItem) entity;
				unHide.getHandle().netServerHandler.sendPacket(new Packet21PickupSpawn(unHideFrom));
			}
		}
	}
	
	public void anti_invisible_delayed(final Player p) {
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				anti_invisible_tick(p);
			}
    	}, (long) 150);
	}
	
	private void anti_invisible_do() {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(plugin.Main_Visiteur.is_visiteur(p)) {
				continue;
			}
			if(p.isSneaking()) {
				anti_invisible_tick(p);
			}
		}
	}
	
	// ANTI XRAY
	
	public double get_rangebyid(int blockid) {
			return 3.0;
	}
	
	public boolean is_blocs(int blockid) {
		if(blockid == 56 || blockid == 14 || blockid == 15 || blockid == 16 || blockid == 21 || blockid == 73) {
			return true;
		}
		return false;
	}
	
	private void do_orcontrol() {
		for (Player p : PlayerOR) {
			
			if(!p.isOnline()) {
				PlayerOR.remove(p);
			}
			
			if(plugin.Main_Visiteur.is_visiteur(p) || (!p.getWorld().getName().contains("world") || p.getWorld().getName().contains("oldworld"))) {
				continue;
			}
			
			Boolean good = true;
			if(player_lastchunk.containsKey(p)) {
				String pchunkid = player_lastchunk.get(p);
				int cx = p.getWorld().getChunkAt(p.getLocation()).getX();
				int cz = p.getWorld().getChunkAt(p.getLocation()).getZ();
				for (int i = cx-3; i <= cx+3; i++) {
					for (int o = cz-3; o <= cz+3; o++) {
						String chunkid = i+" "+o;
						if (chunkid.equals(pchunkid)) {
							good = false;
							break;
						}
					}	
				}
			}
			if (good) {
				plugin.Main_ChunkControl.CacheOnlyChunk(p);
				String schunkid = p.getWorld().getChunkAt(p.getLocation()).getX()+" "+p.getWorld().getChunkAt(p.getLocation()).getZ();
				player_lastchunk.put(p, schunkid);
			}
			
			ArrayList<Block> pblock = new ArrayList<Block>();
			Main_AimBlock aiming = new Main_AimBlock(p);
			if(aiming != null) {
		        Block theblock = aiming.getTargetBlock();
		        if(theblock != null) {
					if(plugin.checkLocation(p.getLocation(), theblock.getLocation(), 10.0)) {
						int vpx = theblock.getLocation().getBlockX();
						int vpz = theblock.getLocation().getBlockZ();
						int vpy = theblock.getLocation().getBlockY();
						for (int x = vpx-2; x <= vpx+2; x++) {
							for (int z = vpz-2; z <= vpz+2; z++) {
								for (int y = vpy-2; y <= vpy+2; y++) {
									Block viewblock = p.getWorld().getBlockAt(x, y, z);
									int bid = viewblock.getTypeId();
									if(bid != 0 && is_blocs(bid)) {
										if(player_blocs.get(p) == null || !player_blocs.get(p).contains(viewblock)) {
											p.sendBlockChange(viewblock.getLocation(), bid, viewblock.getData());
										}
										pblock.add(viewblock);
									}
								}
							}
						}
					}
		        }
			}
			Location location = p.getLocation();
			int px = location.getBlockX();
			int pz = location.getBlockZ();
			int py = location.getBlockY();
	        for (int x = px-2; x <= px+2; x++) {
	            for (int z = pz-2; z <= pz+2; z++) {
	                for (int y = py-2; y <= py+2; y++) {
	                	Block block = p.getWorld().getBlockAt(x, y, z);
	                	int bid = block.getTypeId();
		                if(bid != 0 && is_blocs(bid)) {
			                if(plugin.checkLocation(p.getLocation(), block.getLocation(), get_rangebyid(bid))) {
			                	if(player_blocs.get(p) == null || !player_blocs.get(p).contains(block)) {
			                		p.sendBlockChange(block.getLocation(), bid, block.getData());
			                	}
			                	if(!pblock.contains(block)) {
				                	pblock.add(block);
			                	}
			                }
		                }
	                }
	            }
	        }
        	if(player_blocs.containsKey(p)) {
		        for (Block tblock : player_blocs.get(p)) {
		        	int thebid = tblock.getTypeId();
		        	if(pblock.contains(tblock)) {
		        		p.sendBlockChange(tblock.getLocation(), thebid, tblock.getData());
		        	}else{
		        		if(is_blocs(thebid)) {
		        			p.sendBlockChange(tblock.getLocation(), Material.STONE, tblock.getData());
		        		}
		        	}
		        }
		        player_blocs.remove(p);
        	}
	        if(!pblock.isEmpty()) {
		        player_blocs.put(p, pblock);
			}
		}
	}
	
    public boolean sendChunkChange(CraftPlayer player, Location loc, int sx, int sy, int sz, byte[] data) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int cx = x >> 4;
        int cz = z >> 4;

        if (sx <= 0 || sy <= 0 || sz <= 0) {
            return false;
        }

        if ((x + sx - 1) >> 4 != cx || (z + sz - 1) >> 4 != cz || y < 0 || y + sy > 128) {
            return false;
        }

        if (data.length != (sx * sy * sz * 5) / 2) {
            return false;
        }
        
        Packet51MapChunk packet = new Packet51MapChunk(x, y, z, sx, sy, sz, data);
        player.getHandle().netServerHandler.sendPacket(packet);
        return true;
    }
    
	public void CacheOnlyChunk_do(final Player p) {
		if(player_chunkupdate.containsKey(p) && player_chunkupdate.get(p)) { 
			return; 
		}
		player_chunkupdate.put(p, true);
		
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				player_chunkupdate.put(p, false);
			}
    	}, (long) 300);
		
		int Delayed_time = 0;
		Chunk chunk_debut = p.getWorld().getChunkAt(p.getLocation());
		int x_debut = chunk_debut.getX();
		int z_debut = chunk_debut.getZ();
		for (int i = x_debut-4; i <= x_debut+4; i++) {
			for (int o = z_debut-4; o <= z_debut+4; o++) {
				Chunk chunk = p.getWorld().getChunkAt(i, o);
				CraftChunk craftchunk = (CraftChunk) chunk;
				Delayed_time = Delayed_time+3;
				Block theblock = chunk.getBlock(0, 0, 0);
				final Main_ChunkCopy chunkcopy = getChunkSnapshot(craftchunk);
				final Location lastlocation = theblock.getLocation();
				if(cache_antixray.containsKey(chunk)) {
					for (Block tblock : cache_antixray.get(chunk)) {
						int xx = tblock.getX();
						int yy = tblock.getY();
						int zz = tblock.getZ();
						Block block = chunk.getBlock(xx, yy, zz);
						int bid = block.getTypeId();
						if(bid != 0 && is_blocs(bid)) {
							chunkcopy.setRawTypeId(xx, yy, zz, Material.STONE.getId());
						}else{
							cache_antixray.get(chunk).remove(block);
						}
					}
				}else{
					ArrayList<Block> blocktmp = new ArrayList<Block>();
			        for (int x = 0; x <= 16; x++) {
		                for (int z = 0; z <= 16; z++) {
		                	int hblocky = chunk.getWorld().getHighestBlockYAt(theblock.getLocation());
			                for (int y = 0; y <= 128; y++) {
								if(y > hblocky) {
									break;
								}else{
									Block block = chunk.getBlock(x, y, z);
									int bid = block.getTypeId();
									if(bid != 0 && is_blocs(bid)) {
										chunkcopy.setRawTypeId(x, y, z, Material.STONE.getId());
										blocktmp.add(block);
									}
								}
			                }
			            }
			        }
			        if(!blocktmp.isEmpty()) {
			        	cache_antixray.put(chunk, blocktmp);
			        	blocktmp.clear();
			        }
				}
		        if(lastlocation != null) {
		        	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		    			public void run()
		    			{
		    				sendChunkChange((CraftPlayer) p, new Location(lastlocation.getWorld(), lastlocation.getBlockX(), 0, lastlocation.getBlockZ()), 16, 128, 16, chunkcopy.getdata());
		    			}
		        	}, (long) Delayed_time);
		        }
			}
		}
	}
    
	public void CacheOnlyChunk(final Player p) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runThread_3(plugin, p), 1);
	}
	
	public Main_ChunkCopy getChunkSnapshot(CraftChunk thechunk) {
	        net.minecraft.server.Chunk chunk = thechunk.getHandle();
	        byte[] buf = new byte[32768 + 16384 + 16384 + 16384];
	        chunk.a(buf, 0, 0, 0, 16, 128, 16, 0);
	        byte[] hmap = new byte[256];
	        System.arraycopy(chunk.h, 0, hmap, 0, 256);
	        World w = thechunk.getWorld();
	        return new Main_ChunkCopy(thechunk.getX(), thechunk.getZ(), w.getName(), w.getFullTime(), buf, hmap);
	}
}