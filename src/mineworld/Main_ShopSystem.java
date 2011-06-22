package mineworld;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class Main_ShopSystem {

	Main plugin;
	Main_MessageControl msg;
	
    public Main_ShopSystem(Main parent) {
    	plugin = parent;
    	msg = plugin.Main_MessageControl;
    }
    
    public Map<Player, Integer> player_wait = new HashMap<Player, Integer>();
    
    private void buy_couleur(Player player, int couleur) {
    	
    }
    
    private void wait_confirm(Player player, int aid) {
        player_wait.put(player, aid);
    	msg.sendTaggedMessage(player, "Merci de confirmer votre demande d'achat avec la commande /buy ou /cancel pour annuler.", 1, "[MN-SHOP]");
    }
    
    private void buy(Player player, int aid) {
    	
    }
    
    private void cancel(Player player) {
    	player_wait.remove(player);
    	msg.sendTaggedMessage(player, "Demande d'achat annuler.", 1, "[MN-SHOP]");
    }
    
	public void shop(Player player, String[] args) {
		if(args[1].equals("color")) {
			if(args[2].equals("rouge")) {
				msg.sendTaggedMessage(player, "Impossible, cette couleur est indisponible pour l'achat.", 1, "[MN-SHOP]");	
			}else if(args[2].equals("bleu")) {
				if(canafford(player, 1, 20, true)) {
					wait_confirm(player, 1);
				}
			}else if(args[2].equals("vert")) {
				if(canafford(player, 1, 20, true)) {
					wait_confirm(player, 2);
				}
			}else if(args[2].equals("jaune")) {
				if(canafford(player, 1, 20, true)) {
					wait_confirm(player, 3);
				}
			}
		}
	}
    
    private Boolean canafford(Player player, Integer type, Integer value, Boolean ismsg) {
    	if(type == 1) {
    		int total = getTotal_PPoint(player);
    		if(total >= value) {
    			return true;
    		}
    	}
    	if(ismsg) {
    		msg.sendTaggedMessage(player, "Vous n'avez pas assez pour faire cette achat.", 1, "[MN-SHOP]");
    	}
    	return false;
    }
    
    private Integer getTotal_PPoint(Player player) {
		return (Integer) plugin.getPlayerConfig(player, "money.ppoints", "integer");
    }
}