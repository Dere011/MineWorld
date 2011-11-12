package mineworld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.CustomBlock;

public class Main_Blocks {
	Main plugin;
	SpoutManager sp;
	Map<String,CustomBlock> CustomBlocksList = new HashMap<String,CustomBlock>();

    public Main_Blocks(Main parent) {
    	plugin = parent;
    }
    
    public void load_blocs() {
    	try {
	    	Configuration conf = plugin.conf_blocs;
			if(plugin.Bloc_configFile.exists()){
	    		conf.load();
				List<String> bloclist = conf.getKeys("load-blocs");
				if(!bloclist.isEmpty()) {
					ConfigurationNode node = conf.getNode("load-blocs");
					for(String bloc : bloclist){
						int id = node.getInt(bloc + ".id", 0);
						String name = node.getString(bloc + ".name");
						int reso = node.getInt(bloc + ".reso", 0);
						String url = node.getString(bloc + ".url");
						Boolean craft = node.getBoolean(bloc + ".CanBeCrafter", false);
						Boolean trans = node.getBoolean(bloc + ".isTransparant", false);
						CustomBlock temp = new CBlock(plugin, url, name, trans, reso);
			            CustomBlocksList.put(temp.getName(), temp);
				    	plugin.logger.log(Level.INFO, "[MwITEM] Chargement du bloc "+ id +" ("+name+")");
				    	if(craft)  {
				    		// TODO
				    	}	
					}
				}
			}
		} catch (Exception e) {
		      e.printStackTrace();
		}
    }
}