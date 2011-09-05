package mineworld;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import net.minecraft.server.Packet51MapChunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class Main_ChunkControl {
	
    private final Main plugin;
    Thread thread_01;
    
    public List<Player> PlayerOR = new ArrayList<Player>();
    public List<Player> ResendAll = new ArrayList<Player>();
    
    public Map<Player, ArrayList<Block>> player_blocs = new HashMap<Player, ArrayList<Block>>();
    public Map<Player, Boolean> player_chunkupdate = new HashMap<Player, Boolean>();
    public Map<Player, Long> player_chunkupdate_id = new HashMap<Player, Long>();
    
    public Map<Chunk, Long> cache_antixray_lastupdate = new HashMap<Chunk, Long>();
    public Map<Player, Integer> error_tick = new HashMap<Player, Integer>();
    
    private int chunkupdatetick = 0;
    
    public Main_ChunkControl(Main parent) {
        this.plugin = parent;
        SpoutManager.getPacketManager().addListenerUncompressedChunk(new PacketListener() {
			@Override
            public boolean checkPacket(Player player, MCPacket packet) {
                @SuppressWarnings("deprecation")
				byte[] byteData = ((Packet51MapChunk) packet.getPacket()).rawData;
                if(byteData != null && byteData.length == 80*1024) {
                	Boolean no = true;
                	if(plugin.Main_ContribControl.isClient(player, false)) {
                		no = false;
                	}
                    for(int i = 0; i < 128*16*16; i++) {
                    	int bid = byteData[i];
						if(bid != 0) {
							if(no && is_blocs(bid)) {
								byteData[i] = 1;
							}else{
								/*if(plugin.Main_TimeControl.dead_sun || plugin.Main_TimeControl.prepre_dead_sun) {
							    	Material bname = Material.getMaterial(bid);
							    	if(bname == Material.LEAVES) {
							    		byteData[i] = (byte) Material.FIRE.getId();
							    	}else if(bname == Material.WATER || bname == Material.STATIONARY_WATER || bname == Material.GLASS) {
							    		byteData[i] = (byte) Material.AIR.getId();
							    	}else if(bname == Material.GRASS || bname == Material.DIRT) {
							    		byteData[i] = (byte) Material.SAND.getId();
							    	}else if(bname == Material.LONG_GRASS) {
							    		byteData[i] = (byte) Material.DEAD_BUSH.getId();
							    	}else if(bname == Material.LOG) {
							    		byteData[i] = (byte) Material.CACTUS.getId();
							    	}else if(bname == Material.FURNACE) {
							    		byteData[i] = (byte) Material.BURNING_FURNACE.getId();
							    	}else if(bname == Material.WATER || bname == Material.STATIONARY_WATER) {
							    		byteData[i] = (byte) Material.LAVA.getId();
							    	}
						    	}*/
						    	if(plugin.Main_TimeControl.prehorde || plugin.Main_TimeControl.horde) {
							    	if(bid == Material.GLOWSTONE.getId()) {
							    		byteData[i] = 1;
							    	}else if(bid == Material.TORCH.getId() || bid == Material.GLASS.getId() || bid == Material.LADDER.getId()) {
							    		byteData[i] = 0;
							    	}else if(bid == Material.LAVA.getId() || bid == Material.STATIONARY_LAVA.getId()) {
							    		byteData[i] = 8;
							    	}
						    	}
							}
						}
	                }
            	}
                return true;
            }
        });    
    }
    
    // THREAD

	public Runnable runThread_1(final Main plugin) {
		if(thread_01 == null) {
			thread_01 = new Thread(new Runnable() {
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
			thread_01.setPriority(Thread.MIN_PRIORITY);
			thread_01.setDaemon(false);
		}
		return thread_01;
	}
	
	public Runnable runThread_2(final Main plugin, final Player player) {
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

	// ANTI XRAY
	
	public double get_rangebyid(int blockid) {
			return 5.0;
	}
	
	public boolean is_blocs(int blockid) {
		if(blockid == 19 || blockid == 52 || blockid == 56 || blockid == 14 || blockid == 15 || blockid == 16 || blockid == 21 || blockid == 73) {
			return true;
		}
		return false;
	}
	
	private void do_orcontrol() {
		for (Player p : PlayerOR) {
			if(!p.isOnline()) {PlayerOR.remove(p); continue;}
			if(!p.getWorld().getName().equals("world") || plugin.Main_Visiteur.is_visiteur(p)) {continue;}
			if (!ResendAll.contains(p)) {
				plugin.Main_ChunkControl.CacheOnlyChunk(p);
				ResendAll.add(p);
			}
			if(!plugin.Main_ContribControl.isClient(p, false)) {
				Location location = p.getLocation();
				ArrayList<Block> pblock = new ArrayList<Block>();
				Main_AimBlock aiming = new Main_AimBlock(p);
				if(aiming != null) {
			        Block theblock = aiming.getTargetBlock();
			        if(theblock != null) {
						if(plugin.checkLocation(location, theblock.getLocation(), 10.0)) {
							int vpx = theblock.getLocation().getBlockX();
							int vpz = theblock.getLocation().getBlockZ();
							int vpy = theblock.getLocation().getBlockY();
							for (int x = vpx-3; x <= vpx+3; x++) {
								for (int z = vpz-3; z <= vpz+3; z++) {
									for (int y = vpy-3; y <= vpy+3; y++) {
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
				if(chunkupdatetick > 15) {
					chunkupdatetick = 0;
					int px = location.getBlockX();
					int pz = location.getBlockZ();
					int py = location.getBlockY();
			        for (int x = px-5; x <= px+5; x++) { // 3
			            for (int z = pz-5; z <= pz+5; z++) {
			                for (int y = py-5; y <= py+5; y++) {
			                	Block block = p.getWorld().getBlockAt(x, y, z);
			                	int bid = block.getTypeId();
				                if(bid != 0 && is_blocs(bid)) {
					                if(plugin.checkLocation(location, block.getLocation(), get_rangebyid(bid))) {
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
				}else{
					chunkupdatetick++;
				}
	        	if(player_blocs.containsKey(p)) {
			        for (Block tblock : player_blocs.get(p)) {
			        	int thebid = tblock.getTypeId();
			        	if(!pblock.contains(tblock)) {
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
	}
	
    /*private void do_orcontrol() {
		for (Player p : PlayerOR) {
			if(!p.isOnline()) {PlayerOR.remove(p); continue;}
			if(!p.getWorld().getName().equals("world") || plugin.Main_Visiteur.is_visiteur(p)) {continue;}
			Location location = p.getLocation();
			Chunk pchunk = p.getWorld().getChunkAt(location);
			String chunklist = "", pchunkid = "NonNonNon";
			if(player_lastchunk.containsKey(p)) {
				pchunkid = player_lastchunk.get(p);
				int cx = pchunk.getX(), cz = pchunk.getZ(), force = 0; // 5
				if(plugin.Main_ContribControl.isClient(p, false)) {force = 0;}
				if(plugin.Main_TimeControl.dead_sun || plugin.Main_TimeControl.horde) {force = 2;}
				for (int i = cx-force; i <= cx+force; i++) {
					for (int o = cz-force; o <= cz+force; o++) {
						chunklist = chunklist + i + " " + o;
					}	
				}
			}
			if (chunklist != "" && !chunklist.contains(pchunkid) || !ResendAll.contains(p)) {
				plugin.Main_ChunkControl.CacheOnlyChunk(p);
				ResendAll.add(p);
				String schunkid = pchunk.getX() + " " + pchunk.getZ();
				player_lastchunk.put(p, schunkid);
			}
			if(plugin.Main_ContribControl.isClient(p, false)) {
				ArrayList<Block> pblock = new ArrayList<Block>();
				Main_AimBlock aiming = new Main_AimBlock(p);
				if(aiming != null) {
			        Block theblock = aiming.getTargetBlock();
			        if(theblock != null) {
						if(plugin.checkLocation(location, theblock.getLocation(), 10.0)) {
							int vpx = theblock.getLocation().getBlockX();
							int vpz = theblock.getLocation().getBlockZ();
							int vpy = theblock.getLocation().getBlockY();
							for (int x = vpx-2; x <= vpx+2; x++) {
								for (int z = vpz-2; z <= vpz+2; z++) {
									for (int y = vpy-2; y <= vpy+2; y++) {
										Block viewblock = p.getWorld().getBlockAt(x, y, z);
										int bid = viewblock.getTypeId();
										if(bid != 0 && is_blocs(bid, p)) {
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
				if(chunkupdatetick > 15) {
					chunkupdatetick = 0;
					int px = location.getBlockX();
					int pz = location.getBlockZ();
					int py = location.getBlockY();
			        for (int x = px-5; x <= px+5; x++) { // 3
			            for (int z = pz-5; z <= pz+5; z++) {
			                for (int y = py-5; y <= py+5; y++) {
			                	Block block = p.getWorld().getBlockAt(x, y, z);
			                	int bid = block.getTypeId();
				                if(bid != 0 && is_blocs(bid, p)) {
					                if(plugin.checkLocation(location, block.getLocation(), get_rangebyid(bid))) {
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
				}else{
					chunkupdatetick++;
				}
	        	if(player_blocs.containsKey(p)) {
			        for (Block tblock : player_blocs.get(p)) {
			        	int thebid = tblock.getTypeId();
			        	if(!pblock.contains(tblock)) {
			        		if(is_blocs(thebid, p)) {
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
	}*/
	
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
    
    public Boolean ChunkGenerator(final Player player, Main_ChunkCopy chunkcopy, final int bid, final int x, final int y, final int z, int hy) {
    	/*if(plugin.Main_TimeControl.dead_sun || plugin.Main_TimeControl.prepre_dead_sun) {
	    	if((hy-5) <= y) {
		    	Material bname = Material.getMaterial(bid);
		    	if(bname == Material.LEAVES) {
		    		chunkcopy.setRawTypeId(x, y, z, Material.FIRE.getId());
		    		return true;
		    	}else if(bname == Material.WATER || bname == Material.STATIONARY_WATER || bname == Material.GLASS) {
		    		chunkcopy.setRawTypeId(x, y, z, Material.AIR.getId());
		    		return true;
		    	}else if(bname == Material.GRASS || bname == Material.DIRT) {
		    		chunkcopy.setRawTypeId(x, y, z, Material.SAND.getId());
		    		return true;
		    	}else if(bname == Material.LONG_GRASS) {
		    		chunkcopy.setRawTypeId(x, y, z, Material.DEAD_BUSH.getId());
		    		return true;
		    	}else if(bname == Material.LOG) {
		    		chunkcopy.setRawTypeId(x, y, z, Material.CACTUS.getId());
		    		return true;
		    	}else if(bname == Material.FURNACE) {
		    		chunkcopy.setRawTypeId(x, y, z, Material.BURNING_FURNACE.getId());
		    		return true;
		    	}else if(bname == Material.WATER || bname == Material.STATIONARY_WATER) {
		    		chunkcopy.setRawTypeId(x, y, z, Material.LAVA.getId());
		    		return true;
		    	}
	    	}
    	}*/
    	if(plugin.Main_TimeControl.prehorde || plugin.Main_TimeControl.horde) {
	    	//if((hy-20) <= y) {
		    	if(bid == Material.GLOWSTONE.getId()) {
		    		chunkcopy.setRawTypeIdAndData(x, y, z, Material.DIRT.getId(), (byte) 0);
		    		return true;
		    	}else if(bid == Material.TORCH.getId() || bid == Material.GLASS.getId() || bid == Material.LADDER.getId()) {
		    		chunkcopy.setRawTypeIdAndData(x, y, z, Material.AIR.getId(), (byte) 0);
		    		return true;
		    	}else if(bid == Material.LAVA.getId() || bid == Material.STATIONARY_LAVA.getId()) {
		    		chunkcopy.setRawTypeIdAndData(x, y, z, Material.WATER.getId(), (byte) 0);
		    		return true;
		    	}
	    	//}
    	}
    	return false;
    }
    
    public void RequestChunkSend(final Player player, final Chunk chunk) {
    	CraftChunk craftchunk = (CraftChunk) chunk;
		Block theblock = chunk.getBlock(0, 0, 0);
		final Main_ChunkCopy chunkcopy = getChunkSnapshot(craftchunk);
		final Location lastlocation = theblock.getLocation();
    	Boolean no = true;
    	if(plugin.Main_ContribControl.isClient(player, false)) {
    		no = false;
    	}
        for (int x = 0; x <= 16; x++) {
            for (int z = 0; z <= 16; z++) {
            	Block block = chunk.getBlock(x, 0, z);
            	int hblocky = chunk.getWorld().getHighestBlockYAt(block.getLocation());
                for (int y = 0; y <= hblocky; y++) {
					Block thesblock = chunk.getBlock(x, y, z);
					int bid = thesblock.getTypeId();
					if(bid != 0) {
						if(no && is_blocs(bid)) {
							chunkcopy.setRawTypeId(x, y, z, 1);
						}else{
							ChunkGenerator(player, chunkcopy, bid, x, y, z, hblocky);
						}
					}
                }
            }
        }
        if(lastlocation != null && chunkcopy.getBlockTypeId(0, 0, 0) != 0) {
    			sendChunkChange((CraftPlayer) player, new Location(lastlocation.getWorld(), lastlocation.getBlockX(), 0, lastlocation.getBlockZ()), 16, 128, 16, chunkcopy.getdata());
        }else{
        	if(error_tick.containsKey(player)) {
        		int count = error_tick.get(player);
        		if(count > 5) {
        			player.kickPlayer("MW-SECURITY : Erreur de sécuritée N°5001.");
        			plugin.sendError("Erreur de sécuritée N°5001, joueur "+ player.getName());
        		}else{
        			error_tick.put(player, count+1);
                	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            			public void run()
            			{
            				RequestChunkSend(player, chunk);
            			}
                	}, (long) 1500);
        		}
        	}
        }
    }
    
    /*public void RequestChunkSend(final Player player, final Chunk chunk) {
    	CraftChunk craftchunk = (CraftChunk) chunk;
		Block theblock = chunk.getBlock(0, 0, 0);
		final Main_ChunkCopy chunkcopy = getChunkSnapshot(craftchunk);
		final Location lastlocation = theblock.getLocation();
		if((!plugin.Main_TimeControl.dead_sun && !plugin.Main_TimeControl.horde && !plugin.Main_TimeControl.prehorde) && (cache_antixray.containsKey(chunk) && cache_antixray.get(chunk).size() <= 0) && (cache_antixray_lastupdate.containsKey(chunk) && cache_antixray_lastupdate.get(chunk)+500 > plugin.timetamps)) {
			for (Vector vec : cache_antixray.get(chunk)) {
				int xx = vec.getBlockX();
				int yy = vec.getBlockY();
				int zz = vec.getBlockZ();
				Block block = chunk.getBlock(xx, yy, zz);
				int bid = block.getTypeId();
				if(bid != 0 && is_blocs(bid, player)) {
					chunkcopy.setRawTypeId(xx, yy, zz, Material.STONE.getId());
				}else if(ChunkGenerator(player, chunkcopy, bid, xx, yy, zz, 0) == false){
					cache_antixray.get(chunk).remove(block);
				}
			}
		}else{
			ArrayList<Vector> blocktmp = new ArrayList<Vector>();
	        for (int x = 0; x <= 16; x++) {
                for (int z = 0; z <= 16; z++) {
                	Block block = chunk.getBlock(x, 0, z);
                	int hblocky = chunk.getWorld().getHighestBlockYAt(block.getLocation());
	                for (int y = 0; y <= hblocky; y++) {
						Block thesblock = chunk.getBlock(x, y, z);
						int bid = thesblock.getTypeId();
						if(bid != 0) {
							if(is_blocs(bid, player)) {
								chunkcopy.setRawTypeId(x, y, z, Material.STONE.getId());
								blocktmp.add(new Vector(x, y, z));
							}else{
								ChunkGenerator(player, chunkcopy, bid, x, y, z, hblocky);
							}
						}
	                }
	            }
	        }
	        if((!plugin.Main_TimeControl.dead_sun && !plugin.Main_TimeControl.horde && !plugin.Main_TimeControl.prehorde) && !blocktmp.isEmpty()) {
	        	cache_antixray.put(chunk, blocktmp);
	        	cache_antixray_lastupdate.put(chunk, plugin.timetamps);
	        }
		}
        if(lastlocation != null && chunkcopy.getBlockTypeId(0, 0, 0) != 0) {
    			sendChunkChange((CraftPlayer) player, new Location(lastlocation.getWorld(), lastlocation.getBlockX(), 0, lastlocation.getBlockZ()), 16, 128, 16, chunkcopy.getdata());
        }else{
        	if(cache_antixray.containsKey(chunk)) {
        		cache_antixray.remove(chunk);
        		cache_antixray_lastupdate.remove(chunk);
        	}
        	if(error_tick.containsKey(player)) {
        		int count = error_tick.get(player);
        		if(count > 5) {
        			player.kickPlayer("MW-SECURITY : Erreur de sécuritée N°5001.");
        			plugin.sendError("Erreur de sécuritée N°5001, joueur "+ player.getName());
        		}else{
        			error_tick.put(player, count+1);
                	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            			public void run()
            			{
            				RequestChunkSend(player, chunk);
            			}
                	}, (long) 1500);
        		}
        	}
        }
    }*/
    
	public void CacheOnlyChunk_do(final Player p) {
		if(player_chunkupdate.containsKey(p) && player_chunkupdate.get(p)) { 
			return; 
		}else{
			player_chunkupdate.put(p, true);
	    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					player_chunkupdate.put(p, false);
				}
	    	}, (long) 300);
		}
		int Delayed_time = 0;
		Chunk chunk_debut = p.getWorld().getChunkAt(p.getLocation());
		int x_debut = chunk_debut.getX();
		int z_debut = chunk_debut.getZ();
		final long id = plugin.timetamps;
		player_chunkupdate_id.put(p, id);
		for (int i = (x_debut)-6; i <= (x_debut)+6; i++) {
			for (int o = (z_debut)-6; o <= (z_debut)+6; o++) {
				final Chunk chunk = p.getWorld().getChunkAt(i, o);
				Delayed_time = Delayed_time+3;
		    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run()
					{
						if(player_chunkupdate_id.get(p).equals(id)) {
							RequestChunkSend(p, chunk);
						}
					}
		    	}, (long) Delayed_time);
			}
		}
	}
    
	public void CacheOnlyChunk(final Player p) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runThread_2(plugin, p), 1);
	}
	
	public Main_ChunkCopy getChunkSnapshot(CraftChunk thechunk) {
	        net.minecraft.server.Chunk chunk = thechunk.getHandle();
	        byte[] buf = new byte[32768 + 16384 + 16384 + 16384];
	        chunk.getData(buf, 0, 0, 0, 16, 128, 16, 0);
	        byte[] hmap = new byte[256];
	        System.arraycopy(chunk.heightMap, 0, hmap, 0, 256);
	        World w = thechunk.getWorld();
	        return new Main_ChunkCopy(thechunk.getX(), thechunk.getZ(), w.getName(), w.getFullTime(), buf, hmap);
	}
}