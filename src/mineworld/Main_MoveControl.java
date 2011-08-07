package mineworld;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Main_MoveControl {
	
	Main plugin;
	
    public Main_MoveControl(Main the) {
    	plugin = the;
    }
	
	public void moveEntity(Entity entity, Location loc)
    {
		((CraftEntity) entity).getHandle().setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
    
    private Location getNextBestLocation(Entity entity, Location from, Location loc)
    {
    // Using our from location, find the next best block to move to from loc
    int minx = -1;
    int miny = -1;
    int minz = -1;
    double x = minx;
    double y = miny;
    double z = minz;
    int maxx = 1;
    int maxy = 1;
    int maxz = 1;

    double myx = entity.getLocation().getX();
    double myy = entity.getLocation().getY();
    double myz = entity.getLocation().getZ();

    double tx = loc.getX();
    double ty = loc.getY();
    double tz = loc.getZ();

    double diffx = myx - tx;
    double diffy = myy - ty;
    double diffz = myz - tz;

    if (diffx <= -1)
    {
    minx = -1;
    x = minx;
    maxx = 0;
    }

    if (diffy <= -1)
    {
    miny = -1;
    y = miny;
    maxy = 0;
    }

    if (diffz <= -1)
    {
    minz = -1;
    z = minz;
    maxz = 0;
    }

    if (diffx >= 1)
    {
    minx = 0;
    x = minx;
    maxx = 1;
    }

    if (diffy >= 1)
    {
    miny = 0;
    y = miny;
    maxy = 1;
    }

    if (diffz >= 1)
    {
    minz = 0;
    z = minz;
    maxz = 1;
    }

    while (x <= maxx)
    {
    while (y <= maxy)
    {
    while (z <= maxz)
    {
    Location targetloc = new Location(entity.getWorld(),from.getX()-x,from.getY()-y,from.getZ()-z,loc.getYaw(),loc.getPitch());
    Block block = entity.getWorld().getBlockAt(targetloc);

    if (x == 0 && y == 0 && z == 0)
    {
    // Skip here, we're already there!
    } else {
    if (block.getType() == Material.AIR && !isBlockOccupied(entity, block) && !isBlockFloatingAir(block) && !isBlockWall(block) && !isLastBlock(entity, block))
    {
    return targetloc;
    }
    }
    z++;
    }
    y++;
    z=minz;

    }
    x++;
    y=miny;
    z=minz;
    }
    return null;
    }
    
    private boolean isBlockFloatingAir(Block block) {
    	Location locbelow = new Location(block.getLocation().getWorld(),block.getLocation().getX(),block.getLocation().getY()-1,block.getLocation().getZ(),block.getLocation().getYaw(),block.getLocation().getPitch());
    	Block blockbelow = block.getWorld().getBlockAt(locbelow);

    	if (block.getType() == Material.AIR && blockbelow.getType() != Material.AIR) {
    		return false;
    	}
    		
    	return true;
    }
    
    public Location getTransformCloserToLocation(Entity entity, Location from, Location to)
    {
    double diffx = to.getX() - from.getX();
    double diffy = to.getY() - from.getY();
    double diffz = to.getZ() - from.getZ();
    if (diffx <= 1 && diffx >= -1 && diffy <= 1 && diffy >= -1 && diffz <= 1 && diffz >= -1) {
    	return to;
    }

    Location loc = new Location(entity.getWorld(),from.getX(),from.getY(),from.getZ(),from.getYaw(),from.getPitch());
    if (diffx > 1)
    {
    // move on pos-x axis
    loc.setX(loc.getX()+1);
    }
    if (diffy > 1)
    {
    // move on pos-x axis
    loc.setY(loc.getY()+1);
    }
    if (diffz > 1)
    {
    // move on pos-x axis
    loc.setZ(loc.getZ()+1);
    }
    //
    if (diffx < -1)
    {
    // move on pos-x axis
    loc.setX(loc.getX()-1);
    }
    if (diffy < -1)
    {
    // move on pos-x axis
    loc.setY(loc.getY()-1);
    }
    if (diffz < -1)
    {
    // move on pos-x axis
    loc.setZ(loc.getZ()-1);
    }

    Block block = entity.getWorld().getBlockAt(loc);
    if (block.getType() == Material.AIR && !isBlockOccupied(entity, block) && !isBlockFloatingAir(block) && !isBlockWall(block) && !isLastBlock(entity, block))
    {
    return loc;
    }

    Location location = getNextBestLocation(entity, entity.getLocation(),loc);

    if (location != null)
    {
    return location;
    }

    return from;
    }
    
    private boolean isBlockOccupied(Entity entity, Block block) {
    	for (Entity e : entity.getWorld().getEntities())
    	{
	    	if (e.getLocation() == block.getLocation()) {
	    		return true;
	    	}
    	}
    	return false;
    }

    private boolean isLastBlock(Entity entity, Block block) {
    	if(plugin.move_last.containsKey(entity) && plugin.move_last.get(entity) == block) {
    		return true;
    	}else{
        	return false;
    	}
    }

    private boolean isBlockWall(Block block)
    {
    	Location above = new Location(block.getWorld(),block.getLocation().getX(), block.getLocation().getY()+1, block.getLocation().getZ(),block.getLocation().getYaw(),block.getLocation().getPitch());
    	Block babove = block.getWorld().getBlockAt(above);
    	if(babove.getType() == Material.AIR) {
    		return false;
    	}
    	return true;
    }
    
    public void moveCloserToLocation(Entity entity, Location loc) {
    	Location newloc = getTransformCloserToLocation(entity, entity.getLocation(), loc);
    	//TEST
    	Material mat = entity.getLocation().getBlock().getType();
    	if((mat == Material.IRON_DOOR || mat == Material.IRON_DOOR_BLOCK) || (mat == Material.WOOD_DOOR || mat == Material.WOODEN_DOOR)) {
    		if(plugin.move_last.get(entity) != null) {
    			Location locforfaces = getFaceLocationFromMe(entity, plugin.move_last.get(entity).getLocation());
    	    	Location modifiedlocs = new Location(plugin.move_last.get(entity).getLocation().getWorld(),plugin.move_last.get(entity).getLocation().getX(),plugin.move_last.get(entity).getLocation().getY(),plugin.move_last.get(entity).getLocation().getZ(),locforfaces.getYaw(),locforfaces.getPitch());
    			moveEntity(entity, modifiedlocs);
    			return;
    		}
    	}
    	//TEST
		if(plugin.move_last.containsKey(entity)) {
			plugin.move_last.remove(entity);
		}
		plugin.move_last.put(entity, entity.getLocation().getBlock());
    	Location locforface = getFaceLocationFromMe(entity, loc);
    	Location modifiedloc = new Location(newloc.getWorld(),newloc.getX(),newloc.getY(),newloc.getZ(),locforface.getYaw(),locforface.getPitch());
    	moveEntity(entity, modifiedloc);
    }
    
    public Location moveCloserToLocation(Entity entity, Location loc, Boolean npc) {
    	Location newloc = getTransformCloserToLocation(entity, entity.getLocation(),loc);
    	Location locforface = getFaceLocationFromMe(entity, loc);
    	Location modifiedloc = new Location(newloc.getWorld(),newloc.getX(),newloc.getY(),newloc.getZ(),locforface.getYaw(),locforface.getPitch());

    	return modifiedloc;
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
    
    public void attireEntity(Entity entity, Location loc) {
    	Vector p_v = new Vector(entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
    	Vector b_v = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	Vector difference = b_v.subtract(p_v);
    	double range = getdistance(entity, loc);
		Vector fApplied = difference.multiply(1 / (range*3));
		entity.setVelocity(fApplied);
    }
    
    public Location getFaceLocationFromMe(Entity entity, Location location) {
	   try {
	   Location loc = entity.getLocation();
	   double xDiff = location.getX() - loc.getX();
	   double yDiff = location.getY() - loc.getY();
	   double zDiff = location.getZ() - loc.getZ();
	
	   double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
	   double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
	   double yaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
	   double pitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
	   if (zDiff < 0.0) {
		   yaw = yaw + (Math.abs(180 - yaw) * 2);
	   }
	   		Location finalloc = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ(),(float)yaw-90,(float)pitch);
	   		return finalloc;
	   } catch (Exception e) {
		   e.printStackTrace();
		   return null;
	   }
    }
}