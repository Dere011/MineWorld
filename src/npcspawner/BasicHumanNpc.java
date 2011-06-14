package npcspawner;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import npcspawner.BasicNpc;
import npcspawner.CHumanNpc;

public class BasicHumanNpc extends BasicNpc {

    private CHumanNpc mcEntity;
    private Location start_location;
   
    public BasicHumanNpc(CHumanNpc entity, String uniqueId, String name, Location location) {
        super(uniqueId, name);
        this.mcEntity = entity;
        this.start_location = location;
    }

    public HumanEntity getBukkitEntity() {
        return (HumanEntity) this.mcEntity.getBukkitEntity();
    }

    protected CHumanNpc getMCEntity() {
        return this.mcEntity;
    }

    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        //this.mcEntity.c(x, y, z, yaw, pitch);
        this.mcEntity.setPositionRotation(x, y, z, yaw, pitch);
    }
    
    public void moveTo(Location loc) {
        this.mcEntity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
    
    public Location getStartLocation() {
        return start_location;
    }

    /*public void attackLivingEntity(LivingEntity ent) {
        try {
            this.mcEntity.animateArmSwing();
            Field f = CraftLivingEntity.class.getDeclaredField("entity");
            f.setAccessible(true);
            EntityLiving lEntity = (EntityLiving) f.get(ent);
            this.mcEntity.h(lEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void animateArmSwing()
    {
        this.mcEntity.animateArmSwing();
    }
}
