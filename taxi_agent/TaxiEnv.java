import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.Location;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Logger;

public class TaxiEnv extends Environment {

    // Literals that represent the beliefs of the agent

    public static Literal agentAt = Literal.parseLiteral("at(taxi,0,0)"); // Indicates the location of the taxi
    public static Literal clientAt = Literal.parseLiteral("at(client,4,0)"); // Indicates the location of the client
    public static Literal dropTo = Literal.parseLiteral("drop(client,0,4)"); // Indicates the location in which the
                                                                             // client wants to go
    public static Literal serving = Literal.parseLiteral("serving(taxi)"); // Indicates if the taxi is serving a client
    public static Literal reached = Literal.parseLiteral("hasReached(maxActions)"); // Indicates if the agent has
                                                                                    // reached his maximum actions per
                                                                                    // episode

    // Wall locations
    public static ArrayList<Location> wallsLeft = new ArrayList<Location>();
    public static ArrayList<Location> wallsRight = new ArrayList<Location>();
    public static ArrayList<Location> wallsTop = new ArrayList<Location>();
    public static ArrayList<Location> wallsBottom = new ArrayList<Location>();

    static Logger logger = Logger.getLogger(TaxiEnv.class.getName());
    public Random rand = new Random();
    TaxiModel model; // the model of the grid

    @Override
    public void init(String[] args) {
        model = new TaxiModel();

        if (args.length == 1 && args[0].equals("gui")) {
            TaxiView view = new TaxiView(model);
            model.setView(view);
        }

        updatePercepts();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        System.out.println("[" + ag + "] doing: " + action);
        boolean result = false;

        try {
            // The agent chose to do the action moveTowards
            if (action.getFunctor().equals("moveTowards")) {
                // Convert the action's arguments into a Location
                int x = (int) ((NumberTerm) action.getTerm(0)).solve();
                int y = (int) ((NumberTerm) action.getTerm(1)).solve();
                Location dest = new Location(x, y);
                // Do the action
                result = model.moveTowards(dest);
            }
            // The agent chose to do the action checkForClient
            else if (action.getFunctor().equals("checkForClient")) {
                // Do the action
                result = model.checkForClient();
            }
            // The agent chose to do the action chooseClient
            else if (action.getFunctor().equals("chooseClient")) {
                // Do the action
                result = model.chooseClient();
            }
            // The agent chose to do the action loadClient
            else if (action.getFunctor().equals("loadClient")) {
                // Convert the action's arguments into a Location
                String x = action.getTerm(0).toString();
                String y = action.getTerm(1).toString();
                Location loc = new Location(Integer.parseInt(x), Integer.parseInt(y));
                // Do the action
                result = model.loadClient(loc);

            }
            // The agent chose to do the action unloadClient
            else if (action.getFunctor().equals("unloadClient")) {
                // Convert the action's arguments into a Location
                String x = action.getTerm(0).toString();
                String y = action.getTerm(1).toString();
                Location loc = new Location(Integer.parseInt(x), Integer.parseInt(y));
                // Do the action
                result = model.unloadClient(loc);
            } else {
                logger.info("Failed to execute action " + action);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Total reward is: " + model.reward);
        updatePercepts();

        try {
            // Generate a client only when a boolean condition is true and the number of
            // clients is less than to 2
            if (rand.nextBoolean() && model.totalClients < 2) {
                model.generateRandomClients();
            }
            // Wait for 3 seconds.
            Thread.sleep(4000);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * Function called when an episode is over. Most of the variables on the
     * TaxiModel are re-initialized
     */
    void terminateEpisode() {
        System.out.println("[Env]: Game Over!");
        System.out.println("[Env]: Starting Again...");
        model.isAvailable = true;
        model.hasFoundY = false;
        model.hasFoundX = false;
        model.choose = "nothing";
        model.goTo = "nothing";
        Enumeration<Integer> keys = model.dictionary.keys();
        while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            Location[] locs = model.dictionary.get(key);
            model.remove(model.CLIENT, locs[0]);
        }
        Dictionary<Integer, Location[]> dictionary = new Hashtable<>();
        model.dictionary = dictionary;
        model.clientID = 0;
        model.totalClients = 0;
        model.maxActions = 30;
        model.total_rewards.add(model.reward);
        System.out.println("Rewards on each episode: " + model.total_rewards);
        model.reward = 0;
        Random rand = new Random();
        int randX = rand.nextInt(5);
        int randY = rand.nextInt(5);
        model.setAgPos(0, randX, randY);
        model.clientLocation = model.startingLocation;
        model.dropLocation = model.startingLocation;
    }

    /** creates the agents percepts based on the TaxiModel */
    void updatePercepts() {
        // clear the percepts of the agents
        clearPercepts();

        // if the episode is over, add the belief hasReached(maxActions) and call the
        // function terminateEpisode()
        if (model.maxActions <= 0) {
            addPercept(reached);
            terminateEpisode();
        }

        // Add the taxi's location to the agent's beliefs
        Location lTaxi = model.getAgPos(0);
        agentAt = Literal.parseLiteral("at(taxi," + lTaxi + ")");
        addPercept(agentAt);
        System.out.println("[Ag]: I am at " + lTaxi);

        // Add the client's location and dropping location to the agent's beliefs
        Location clientLoc = model.clientLocation;
        Location dropLoc = model.dropLocation;
        Location startingLocation = new Location(2, 2);
        // If the above locations are 2,2 it means that still there isn't a client in
        // the grid
        if (!clientLoc.equals(startingLocation) && !dropLoc.equals(startingLocation)) {
            Literal clientAt = Literal.parseLiteral("at(client," + clientLoc + ")");
            Literal dropTo = Literal.parseLiteral("drop(client," + dropLoc + ")");
            addPercept(clientAt);
            addPercept(dropTo);
            System.out.println("[Ag]: The client I am serving is at " + clientLoc + " and wants to go to " + dropLoc);
            System.out.println();
        }

        // Add the belief serving(taxi) if there is at least one client in the grid,
        // otherwise remove it
        if (model.totalClients != 0) {
            addPercept(serving);
        } else {
            removePercept(serving);
        }
    }

}
