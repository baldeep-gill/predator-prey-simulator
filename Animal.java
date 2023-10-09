import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 */
public abstract class Animal
{
    private boolean alive;
    private Field field;
    private Location location;
    // The animal's gender: true for male, false for female
    private boolean male;
    
    private static final Random rand = Randomizer.getRandom();
    
    // Create a new animal at location in field. 
    public Animal(Field field, Location location)
    {
        this.alive = true;
        this.field = field;
        setLocation(location);
        this.male = rand.nextBoolean();
    }
    
    /**
     * Make this animal act
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return this.alive;
    }
    
    /**
     * @return Whether the animal is male or not
     */
    protected boolean getGender()
    {
        return this.male;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        this.alive = false;
        if(this.location != null) {
            this.field.clear(this.location);
            this.location = null;
            this.field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
}
