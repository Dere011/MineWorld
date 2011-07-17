package npcspawner;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import npcspawner.BasicNpc;
import npcspawner.CHumanNpc;

public class BasicHumanNpc extends BasicNpc {

    private CHumanNpc mcEntity;
    private Location start_location;
    private Boolean tueur;
    private String skinurl;
    
    public BasicHumanNpc(CHumanNpc entity, String uniqueId, String name, Location location, Boolean tueur, String skinurl) {
        super(uniqueId, name);
        this.mcEntity = entity;
        this.start_location = location;
        this.tueur = tueur;
        this.skinurl = skinurl;
    }

    public HumanEntity getBukkitEntity() {
        return (HumanEntity) this.mcEntity.getBukkitEntity();
    }

    protected CHumanNpc getMCEntity() {
        return this.mcEntity;
    }

    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        this.mcEntity.setPositionRotation(x, y, z, yaw, pitch);
    }
    
    public void moveTo(Location loc) {
        this.mcEntity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
    
    public Location getStartLocation() {
        return start_location;
    }
    
    public Boolean isTueur() {
        return tueur;
    }
    
    public String skinURL() {
        return skinurl;
    }

    public void animateArmSwing()
    {
        this.mcEntity.animateArmSwing();
    }
}
