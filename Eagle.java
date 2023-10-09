import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of an eagle.
 * Eagles age, move, eat rabbits, and die.
 */
public class Eagle extends Animal
{
    // Characteristics shared by all eagle (class variables).
    
    // The age at which a eagle can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a eagle can live.
    private static final int MAX_AGE = 43;
    // The likelihood of a eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.24;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single squirrel. In effect, this is the
    // number of steps a eagle can go before it has to eat again.
    private static final int SQUIRREL_FOOD_VALUE = 31;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The eagle's age.
    private int age;
    // The eagle's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create an eagle. An eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SQUIRREL_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = SQUIRREL_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the eagle does most of the time: it hunts for
     * squirrels. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newEagles A list to return newly born eagles.
     */
    public void act(List<Animal> newEagles)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newEagles);            
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
     * Increase the age. This could result in the eagle's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this eagle more hungry. This could result in the eagle's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for squirrels adjacent to the current location.
     * Only the first live squirrel is eaten.
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
            if(animal instanceof Squirrel) {
                Squirrel squirrel = (Squirrel) animal;
                if(squirrel.isAlive()) { 
                    squirrel.setDead();
                    foodLevel = SQUIRREL_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether there is an eagle of opposite gender in an adjacent position
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
            if(animal instanceof Eagle)
            {
                Eagle partner = (Eagle) animal;
                return partner.getGender() != this.getGender();
            }
        }
        return false;
    }
    
    /**
     * Check whether or not this eagle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born eagles.
     */
    private void giveBirth(List<Animal> newFoxes)
    {
        if(meet())
        {
            // New foxes are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Eagle young = new Eagle(false, field, loc);
                newFoxes.add(young);
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
     * A eagle can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
