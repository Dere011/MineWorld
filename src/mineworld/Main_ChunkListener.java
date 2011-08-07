package mineworld;

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

public class Main_ChunkListener extends WorldListener {
	
	Main plugin;

    public Main_ChunkListener(Main parent) {
    	plugin = parent;
    }
    
    public void onChunkUnload(ChunkUnloadEvent event) {
    	event.setCancelled(true);
    }
}