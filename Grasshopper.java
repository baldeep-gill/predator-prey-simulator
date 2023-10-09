import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Grasshopper.
 * Grasshoppers age, move, breed, and die.
 */
public class Grasshopper extends Animal
{
    // Characteristics shared by all grasshopper (class variables).

    // The age at which a grasshopper can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a grasshopper can live.
    private static final int MAX_AGE = 45;
    // The likelihood of a grasshopper breeding.
    private static final double BREEDING_PROBABILITY = 0.61;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Food value for eating a plant
    private static final int PLANT_FOOD_VALUE = 11;
    
    // Individual characteristics (instance fields).
    
    // The grasshopper's age.
    private int age;
    // Grasshopper's foodleve
    private int foodLevel;
    // Field of plants
    private Field plantField;

    /**
     * Create a new grasshopper. A grasshopper may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param plantField A separate field of plants for food
     * @param location The location within the field.
     */
    public Grasshopper(boolean randomAge, Field field, Field plantField, Location location)
    {
        super(field, location);
        this.plantField = plantField;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PLANT_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the grasshopper does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newGrasshoppers A list to return newly born grasshopper.
     */
    public void act(List<Animal> newGrasshoppers)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newGrasshoppers);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the grasshopper's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this grasshopper more hungry. This could result in the grasshopper's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for plants adjacent to the current location.
     * Only the first plant is eaten
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = plantField;
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            
            //If squirrel finds a plant
            if(plant instanceof Plant) {
                Plant food = (Plant) plant;
                if(food.isAlive()) { 
                    food.eat();
                    foodLevel = PLANT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether there is a grasshopper of opposite gender in an adjacent position
     * @return If partner is found
     */
    private boolean meet()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext())
        {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Grasshopper)
            {
                Grasshopper partner = (Grasshopper) animal;
                return partner.getGender() != this.getGender();
            }
        }
        return false;
    }
    
    /**
     * Check whether or not this grasshopper is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGrasshoppers A list to return newly born rabbits.
     */
    private void giveBirth(List<Animal> newGrasshoppers)
    {
        if(meet())
        {
            // New rabbits are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Grasshopper young = new Grasshopper(false, field, plantField, loc);
                newGrasshoppers.add(young);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A grasshopper can breed if it has reached the breeding age.
     * @return true if the grasshopper can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
