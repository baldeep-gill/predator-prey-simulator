import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a scorpion.
 * Scorpions age, move, breed, and die.
 */
public class Scorpion extends Animal
{
    // Characteristics shared by all scorpions (class variables).

    // The age at which a scorpion can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a scorpion can live.
    private static final int MAX_AGE = 41;
    // The likelihood of a scorpion breeding.
    private static final double BREEDING_PROBABILITY = 0.35;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // Food level provided by eating a grasshopper
    private static final int GRASSHOPPER_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The scorpion's age.
    private int age;
    // The scorpion's food level
    private int foodLevel;

    /**
     * Create a new scorpion. A scorpion may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the scorpion will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Scorpion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GRASSHOPPER_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GRASSHOPPER_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the scorpion does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newScorpions A list to return newly born scorpions.
     */
    public void act(List<Animal> newScorpions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newScorpions);            
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
     * This could result in the scorpion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this scorpion more hungry. This could result in the scorpion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for grasshoppers adjacent to the current location.
     * Only the first live grasshopper is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Grasshopper) {
                Grasshopper grasshopper = (Grasshopper) animal;
                if(grasshopper.isAlive()) { 
                    grasshopper.setDead();
                    foodLevel = GRASSHOPPER_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether there is a scorpion of opposite gender in an adjacent position
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
            if(animal instanceof Scorpion)
            {
                Scorpion partner = (Scorpion) animal;
                return partner.getGender() != this.getGender();
            }
        }
        return false;
    }
    
    /**
     * Check whether or not this scorpion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newScorpions A list to return newly born scorpions.
     */
    private void giveBirth(List<Animal> newScorpions)
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
                Scorpion young = new Scorpion(false, field, loc);
                newScorpions.add(young);
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
     * A scorpion can breed if it has reached the breeding age.
     * @return true if the scorpion can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
