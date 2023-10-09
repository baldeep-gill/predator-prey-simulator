import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * A simple model of plants.
 * Plants do not move but they can spread to adjacent tiles.
 * Plants provide a source of food to certain animals in the foodchain.
 */
public class Plant
{
    // Characteristics shared by all plants
    
    // The field the plant is in - separate to the field that animals are in
    private Field field;
    // The location of the plant in the field
    private Location location;
    // Whether or not the plant is "alive" or not - the plant "dies" if it is eaten
    private boolean alive;
    // The growth size of a plant
    private int size;
    // The minimum age a plant must be in order to spread to adjacent tiles
    private static final int GROWTH_AGE = 2;
    // Maximum size a plant can grow to
    private static final int MAX_GROWTH = 350;
    // Probabilty that a plant spreads   
    private static final double GROWTH_PROBABILITY = 0.91;
    
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new plant object. Plants can either be created with a random size or size of 0.
     * 
     * @param randomSize If true, the plant will spawn with a random age
     * @param field The field currently occupied
     * @param location The location within the field
     */
    public Plant(boolean randomSize, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomSize)
            size = rand.nextInt(50);
        else
            size = 0;
    }
    
    /**
     * Method for the plant to "act". Plant gets bigger every steps and tries to spread.
     */
    public void act(List<Plant> newPlants)
    {
        incrementGrowth();
        if(isAlive())
        {
            spread(newPlants);
        }
    }
    
    /**
     * Check whether the plant can spread. New plants will spawn into free adjacent tiles.
     * 
     * @param newPlants A list to return newly spawned plants
     */
    public void spread(List<Plant> newPlants)
    {
        if(size >= GROWTH_AGE && rand.nextDouble() <= GROWTH_PROBABILITY)
        {
            Field nField = getField();
            List<Location> free = nField.getFreeAdjacentLocations(getLocation());
            int number = rand.nextInt(5);
            for(int b = 0; b < number && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Plant sapling = new Plant(false, field, loc);
                newPlants.add(sapling);
            } 
        }
    }
    
    /**
     * Place the plant at a new location in the field
     * @param newLocation new location for the plant
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * @return The plant's location 
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * @return The plant's field
     */
    public Field getField()
    {
        return field;
    }
    
    /**
     * @return If the plant is alive or not
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void eat()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Increase the size of the plant. If it grows bigger than its max size it will die.
     */
    public void incrementGrowth()
    {
        size++;
        if(size > MAX_GROWTH)
        {
            eat();
        }
    }
}
