import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Squirrel.
 * Squirrel age, move, breed, and die.
 */
public class Squirrel extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a squirrel can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a squirrel can live.
    private static final int MAX_AGE = 35;
    // The likelihood of a squirrel breeding.
    private static final double BREEDING_PROBABILITY = 0.51;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Food level provided by eating a plant
    private static final int PLANT_FOOD_VALUE = 8;
    
    // Individual characteristics (instance fields).
    
    // The squirrel's age.
    private int age;
    // Squirrel's food level
    private int foodLevel;
    // Field of plants
    private Field plantField;

    /**
     * Create a new squirrel. A squirrel may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the squirrel will have a random age.
     * @param field The field currently occupied.
     * @param plantField A separate field of plants for food
     * @param location The location within the field.
     */
    public Squirrel(boolean randomAge, Field field, Field plantField, Location location)
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
     * This is what the squirrel does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newSquirrel A list to return newly born squirrels.
     */
    public void act(List<Animal> newSquirrels)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSquirrels);            
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
     * This could result in the squirrel's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this squirrel more hungry. This could result in the squirrel's death.
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
     * Check whether there is a squirrel of opposite gender in an adjacent position
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
            if(animal instanceof Squirrel)
            {
                Squirrel partner = (Squirrel) animal;
                return partner.getGender() != this.getGender();
            }
        }
        return false;
    }
    
    /**
     * Check whether or not this squirrel is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSquirrels A list to return newly born squirrels.
     */
    private void giveBirth(List<Animal> newSquirrels)
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
                Squirrel young = new Squirrel(false, field, plantField, loc);
                newSquirrels.add(young);
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
     * A squirrel can breed if it has reached the breeding age.
     * @return true if the squirrel can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
