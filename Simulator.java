import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 150;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 150;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    // The probability that a squirrel will be created in any given grid position.
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.31;
    // The probability that a scorpion will be created in any given grid position.
    private static final double SCORPION_CREATION_PROBABILITY = 0.15;
    // The probability that a grasshopper will be created in any given grid position.
    private static final double GRASSHOPPER_CREATION_PROBABILITY = 0.35;
    // The probability that a eagle will be created in any given grid position.
    private static final double EAGLE_CREATION_PROBABILITY = 0.01;
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.65;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current state of the plant field
    private Field plantField;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // A graphical view of the plant simulation
    private SimulatorView plantView;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        animals = new ArrayList<>();
        field = new Field(depth, width);
        
        plants = new ArrayList<>();
        plantField = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Squirrel.class, Color.RED);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Scorpion.class, Color.PINK);
        view.setColor(Grasshopper.class, Color.GREEN);
        view.setColor(Eagle.class, Color.ORANGE);
        
        //plantView = new SimulatorView(depth, width);
        //plantView.setColor(Plant.class, Color.GREEN);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (500 steps).
     */
    public void runLongSimulation()
    {
        simulate(750);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            //delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        
        /*if(step % 35 == 0) {
            plants.clear();
            plantPopulate();
        }*/
        
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        List<Plant> newPlants = new ArrayList<>();
        
        //Let all plants act
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            
            //Plants only act if it is not night
            if(!isNight()) { 
                plant.act(newPlants);
                if(!plant.isAlive()) {
                    it.remove();
                }
            }
        }
        
        // Let all rabbits act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            
            //Animals sleep during the night
            if(!isNight()) {
                animal.act(newAnimals);
                if(! animal.isAlive()) {
                    it.remove();
                }
            }
        }
               
        // Add the newly born animals and plants to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);
        
        //plantView.showStatus(step, plantField);
        view.showStatus(step, field);
    }
    
    /**
     * Every second step is night time - animals do not act at night
     */
    public boolean isNight()
    {
        return step % 2 == 0;
    }
    
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        populate();
        
        // Show the starting state in the view.
        //plantView.showStatus(step, plantField);
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate field with plants
     */
    private void plantPopulate()
    {
        Random rand = Randomizer.getRandom();
        
        plantField.clear();
        for(int row = 0; row < plantField.getDepth(); row++) {
            for(int col = 0; col < plantField.getWidth(); col++) {
                if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY)
                {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, plantField, location);
                    plants.add(plant);
                }
            }
        }
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        plantPopulate();
        
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= EAGLE_CREATION_PROBABILITY)
                {
                    Location location = new Location(row, col);
                    Eagle eagle = new Eagle(true, field, location);
                    animals.add(eagle);
                }
                else if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    animals.add(fox);
                }
                else if(rand.nextDouble() <= SCORPION_CREATION_PROBABILITY)
                {
                    Location location = new Location(row, col);
                    Scorpion scorpion = new Scorpion(true, field, location);
                    animals.add(scorpion);
                }
                else if(rand.nextDouble() <= GRASSHOPPER_CREATION_PROBABILITY)
                {
                    Location location = new Location(row, col);
                    Grasshopper grasshopper = new Grasshopper(true, field, plantField, location);
                    animals.add(grasshopper);
                }
                else if(rand.nextDouble() <= SQUIRREL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Squirrel squirrel = new Squirrel(true, field, plantField, location);
                    animals.add(squirrel);
                }                
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.runLongSimulation();
    }
}
