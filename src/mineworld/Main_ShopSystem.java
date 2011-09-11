package mineworld;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class Main_ShopSystem {

	Main plugin;
	
    public Main_ShopSystem(Main parent) {
    	plugin = parent;
    }
    
    public Map<Player, Integer> player_wait = new HashMap<Player, Integer>();
    
    private void buy_couleur(Player player, int couleur) {
    	plugin.setPlayerConfig(player, "plus.color", couleur);
    	plugin.Main_MessageControl.sendTaggedMessage(player, "Merci de votre achat.", 1, "[MN-SHOP]");
    }
    
    private void wait_confirm(Player player, int aid) {
        player_wait.put(player, aid);
        plugin.Main_MessageControl.sendTaggedMessage(player, "Merci de confirmer votre demande d'achat avec la commande /buy ou /cancel pour annuler.", 1, "[MN-SHOP]");
    }
    
    public void remove_money(Player player, int type, int value) {
    	if(type == 1) {
	    	int total = getTotal_PPoint(player);
	    	int totaltotal = total-value;
	    	plugin.setPlayerConfig(player, "money.ppoints", totaltotal);
	    	plugin.Main_ContribControl.sendNotification(player, "MONEY Transaction", "-"+ value +" PPoint(s)");
	    	plugin.Main_MessageControl.sendTaggedMessage(player, "Il vous reste "+totaltotal+" PPoint(s).", 1, "[MN-SHOP]");
	    	plugin.Main_ContribControl.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/money.wav");
    	}
    }
    
    public void buy(Player player) {
    	if(player_wait.containsKey(player)) {
    		int aid = player_wait.get(player);
    		if(aid > 0 && aid < 4) {
    			if(canafford(player, 1, 20, true)) {
    				buy_couleur(player, aid);
    				remove_money(player, 1, 20);
    			}
    		}
    	}
    }
    
    public void cancel(Player player) {
    	player_wait.remove(player);
    	plugin.Main_MessageControl.sendTaggedMessage(player, "Demande d'achat annuler.", 1, "[MN-SHOP]");
    }
    
    public void getmoney(Player player) {
    	String[] anTxt = plugin.Main_MessageControl.createstrings(2);
   	 	anTxt[0] = "Voici la solde de vos comptes :";
   	 	anTxt[1] = "(1) PPoint(s) : "+ (0+getTotal_PPoint(player)) + " | (2) BitCoin(s) : 0";
   	 	plugin.Main_MessageControl.sendTaggedMessage(player, anTxt, 2, "MN-MONEY");
    }
    
	public void shop(Player player, String[] args) {
		if(args[1] != null && args[1].equals("color")) {
			if(args[2] != null) {
				if(args[2].equals("bleu")) {
					if(canafford(player, 1, 100, true)) {
						wait_confirm(player, 1);
					}
				}else if(args[2].equals("vert")) {
					if(canafford(player, 1, 100, true)) {
						wait_confirm(player, 2);
					}
				}else if(args[2].equals("jaune")) {
					if(canafford(player, 1, 100, true)) {
						wait_confirm(player, 3);
					}
				}else{
					plugin.Main_MessageControl.sendTaggedMessage(player, "Couleur disponible : bleu, vert, jaune", 1, "[MN-SHOP]");
					plugin.Main_MessageControl.sendTaggedMessage(player, "/mineworld shop color <couleur>", 1, "[MN-SHOP]");
				}
			}else{
				plugin.Main_MessageControl.sendTaggedMessage(player, "Couleur disponible : bleu vert jaune", 1, "[MN-SHOP]");
				plugin.Main_MessageControl.sendTaggedMessage(player, "/mineworld shop color <type>", 1, "[MN-SHOP]");
			}
		}else{
			plugin.Main_MessageControl.sendTaggedMessage(player, "Achat disponible : color (Couleur sur le pseudonyme)", 1, "[MN-SHOP]");	
			plugin.Main_MessageControl.sendTaggedMessage(player, "/mineworld shop <type>", 1, "[MN-SHOP]");
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
    		plugin.Main_ContribControl.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
    		plugin.Main_MessageControl.sendTaggedMessage(player, "Vous n'avez pas assez pour faire cette achat (Money N°"+type+").", 1, "[MN-SHOP]");
    	}
    	return false;
    }
    
    private Integer getTotal_PPoint(Player player) {
		return (Integer) plugin.getPlayerConfig(player, "money.ppoints", "int");
    }
}