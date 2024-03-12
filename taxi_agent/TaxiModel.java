import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import jason.asSyntax.Literal;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.stdlib.queue.add;

/** class that implements the Model of Taxi-Driver application */
public class TaxiModel extends GridWorldModel {

    // constants for the grid objects

    public static final int RED = 8;
    public static final int YELLOW = 16;
    public static final int BLUE = 32;
    public static final int GREEN = 64;
    public final int CLIENT = 2;

    // the grid size
    public static final int GSize = 5;

    int reward = 0; // total reward of actions
    ArrayList<Integer> total_rewards = new ArrayList<Integer>(); // reward earned in each episode
    boolean isAvailable = true; // if taxi is free to pick up a client
    int maxActions = 30; // max actions for every episode
    boolean hasFoundY = false; // indicate if agent has been at the same Column as the client
    boolean hasFoundX = false; // indicate if agent has been at the same Row as the client
    String choose = "nothing"; // takes position values depending on a coin flip
    String goTo = "nothing"; // takes position values depending on goal location

    // Dictionary including clientId (key) and the pair of current and goal location
    // of client (value)
    Dictionary<Integer, Location[]> dictionary = new Hashtable<>();
    int clientID = 0; // the id of the client used in the dictionary
    int bestClient = -1; // the id of the chosen client
    int totalClients = 0; // total number of clients

    // ArrayLists representing the belief state of agent about the walls in the
    // environment
    public ArrayList<Location> wallAtLeft = new ArrayList<Location>();
    public ArrayList<Location> wallAtRight = new ArrayList<Location>();
    public ArrayList<Location> wallAtTop = new ArrayList<Location>();
    public ArrayList<Location> wallAtBottom = new ArrayList<Location>();

    // ArrayLists representing the walls in the environment
    public ArrayList<Location> envWallLeft = new ArrayList<Location>();
    public ArrayList<Location> envWallRight = new ArrayList<Location>();
    public ArrayList<Location> envWallTop = new ArrayList<Location>();
    public ArrayList<Location> envWallBottom = new ArrayList<Location>();

    // Goal Locations
    Location lRed = new Location(0, 0);
    Location lYellow = new Location(0, 4);
    Location lBlue = new Location(3, 4);
    Location lGreen = new Location(4, 0);

    Location startingLocation = new Location(2, 2);
    Location goalLocation = startingLocation; // Current agent's goal location
    Location clientLocation = startingLocation; // Current clent's location
    Location dropLocation = startingLocation; // Current client's drop location

    // Random location of agent
    Random rand = new Random();
    int randX = rand.nextInt(5);
    int randY = rand.nextInt(5);
    Location lTaxi = new Location(randX, randY);

    public TaxiModel() {
        // create a 5x5 grid with one mobile agent
        super(GSize, GSize, 1);
        // initial location of robot
        // ag code 0 means the robot
        setAgPos(0, lTaxi);

        // add goal locations on grid
        add(RED, lRed);
        add(YELLOW, lYellow);
        add(BLUE, lBlue);
        add(GREEN, lGreen);

        // initialize the walls in the environment
        for (int i = 0; i < 5; i++) {
            envWallLeft.add(new Location(0, i));
            envWallTop.add(new Location(i, 0));
            envWallBottom.add(new Location(i, 4));
            envWallRight.add(new Location(4, i));
        }

        envWallLeft.add(new Location(2, 0));
        envWallLeft.add(new Location(2, 1));
        envWallLeft.add(new Location(3, 3));
        envWallLeft.add(new Location(3, 4));
        envWallLeft.add(new Location(1, 3));
        envWallLeft.add(new Location(1, 4));

        envWallRight.add(new Location(1, 0));
        envWallRight.add(new Location(1, 1));
        envWallRight.add(new Location(0, 3));
        envWallRight.add(new Location(0, 4));
        envWallRight.add(new Location(2, 3));
        envWallRight.add(new Location(2, 4));
    }

    /**
     * Function generating clients at random location
     * <p>
     * The function generates the initial and the dropping location of the client in
     * one of the four
     * goal location ({@link #lBlue}, {@link #lYellow}, {@link #lRed},
     * {@link #lGreen})
     */
    void generateRandomClients() {

        // Adding a client in one of the four goal locations
        Location[] locations = { lYellow, lBlue, lGreen, lRed };
        Random rand = new Random();
        int randLoc = rand.nextInt(4);
        int randLoc2 = rand.nextInt(4);
        // Add client to grid
        add(CLIENT, locations[randLoc]);
        Location currentLocation = locations[randLoc];
        Location dropLoc = locations[randLoc2];
        // Putting the pair of client's current and drop location on an array
        Location[] clientLocations = { currentLocation, dropLoc };
        System.out.println("[Env]: A client was generated at " + clientLocations[0] + ", " + clientLocations[1]);
        while (dictionary.get(clientID) != null) {
            // In case there is already an entry with the same clientId, increment the
            // clientId by one
            if (clientID == 2) {
                // If client id = 2 go back to 0
                clientID = 0;
            } else {
                clientID++;
            }
        }
        // Adding the elements to the dictionary
        // System.out.println("ClientId " + clientID);
        dictionary.put(clientID, clientLocations);
        totalClients++;
        clientID++;

    }

    /**
     * Agent action allowing the agent to check for clients in grid
     * 
     * @return true
     */
    boolean checkForClient() {
        System.out.println("[Ag]: Checking for clients...");
        return true;
    }

    /**
     * Agent action, helping the agent to choose which client is best to serve
     * <p>
     * Utilizes the Manhattan distance between two points in the grid
     * 
     * @return true if the action is valid
     */
    boolean chooseClient() {
        System.out.println("[Ag]: Choosing which client should I serve...");
        Location agPos = getAgPos(0);
        Location[] bestClientLoc;
        Enumeration<Integer> keys = dictionary.keys();
        int bestDist = 25;

        if (dictionary.size() > 1) {
            // Iterate through the dictionary
            while (keys.hasMoreElements()) {
                int key = keys.nextElement();
                Location[] dists = dictionary.get(key);
                // Calculate the Manhattan distance between agent's location and client's
                // location
                int dist1 = agPos.distanceManhattan(dists[0]);
                // Calculate the Manhattan distance between client's location and goal location
                int dist2 = dists[0].distanceManhattan(dists[1]);
                // Add the two distances to get total distance of agent's route
                int totalDist = dist1 + dist2;
                System.out.println("[Ag]: Total Dist of client " + key + " is " + totalDist);
                // If a route has less distance than another the this client is the best
                if (totalDist < bestDist) {
                    bestDist = totalDist;
                    bestClient = key;
                }
            }
            bestClientLoc = dictionary.get(bestClient);
            System.out.println("[Ag]: Best client is: " + bestClient + " with dist " + bestDist);
            clientLocation = bestClientLoc[0];
            dropLocation = bestClientLoc[1];
            goalLocation = clientLocation;
        } else {
            // If there is only one client, serve this client
            while (keys.hasMoreElements()) {
                int key = keys.nextElement();
                bestClient = key;
                bestClientLoc = dictionary.get(key);
                clientLocation = bestClientLoc[0];
                dropLocation = bestClientLoc[1];
                // Agent's goal s to go to client location
                goalLocation = clientLocation;
                System.out.println("[Ag]: Only one client so I choose him");
            }
        }
        return true;
    }

    /**
     * Agent action allowing the agent to move through the grid
     * <p>
     * Uses the function {@link #moveTo(String)}
     * 
     * @param dest the location in which the agent wishes to go
     * @return true if the action is valid
     */
    boolean moveTowards(Location dest) {
        // Get agent's location
        Location r1 = getAgPos(0);
        // Everytime the agent should go to the same Column as the destination and then
        // to the same Row
        if (r1.x <= dest.x && r1.x != dest.x) {
            r1 = moveTo("right");
        } else if (r1.x >= dest.x && r1.x != dest.x) {
            r1 = moveTo("left");
        } else if (r1.y < dest.y && r1.x == dest.x) {
            r1 = moveTo("down");
        } else if (r1.y > dest.y && r1.x == dest.x) {
            r1 = moveTo("up");
        }

        setAgPos(0, r1); // move the agent in the grid

        return true;
    }

    /**
     * Supplementary function for {@link #moveTowards(Location)}
     * <p>
     * Indicates if the agent should move right, left, up or down
     * 
     * @param position the direction in which the agent will move. It takes the
     *                 values: up,
     *                 down, left or right
     * @return true if the action is valid
     */
    Location moveTo(String position) {
        Location from = getAgPos(0);

        if (from.y == goalLocation.y) {
            hasFoundY = true;
        }
        if (from.x == goalLocation.x) {
            hasFoundX = true;
        }
        // The agent chooses up and the env is clear of walls
        if (position.equals("up") && !wallAtTop.contains(from) && from.y != 0 && !envWallTop.contains(from)) {
            from.y--;
            // If the agent is in the same Row as the client then it has found the Row
            if (from.y == goalLocation.y) {
                hasFoundY = true;
            }
            reward = reward - 1;
            System.out.println("[Ag]: Going up, no wall...");
        }
        // The agent chooses up but there is a wall that it hasn't found
        else if (position.equals("up") && !wallAtTop.contains(from) && envWallTop.contains(from)) {
            reward = reward - 100;
            // Add wall's location to 'beliefs'
            wallAtTop.add(from);
            System.out.println("[Ag]: I bumped into a wall...");
        }
        // The agent chooses up, knows there is a wall there and hasn't found the Column
        // of the goal
        else if (position.equals("up") && wallAtTop.contains(from) && !hasFoundX) {
            System.out.println("[Ag]: There is a wall here...");
            // Depending on the goal's column move right or left
            if (from.x < goalLocation.x) {
                System.out.println("[Ag]: Going right");
                from = moveTo("right");
            } else if (from.x > goalLocation.x) {
                System.out.println("[Ag]: Going left");
                from = moveTo("left");
            }
        }
        // The agent chooses up, knows there is a wall there and has found the Column
        else if (position.equals("up") && wallAtTop.contains(from) && hasFoundX) {
            // If the agent is at first column move only right
            if (from.x == 0) {
                goTo = "right";
                from = moveTo(goTo);
                System.out.println("[Ag]: I have found the column and I'm on edge so I go right");
            }
            // If the agent is at last column move only left
            else if (from.x == 4) {
                goTo = "left";
                from = moveTo(goTo);
                System.out.println("[Ag]: I have found the column and I'm on edge so I go left");
            }
            // The agent is free to move either left or right
            else if (wallAtTop.contains(from)) {
                // If agent hasn't deicided where to go, choose left or right randomly and stick
                // to that
                if (goTo.equals("nothing")) {
                    Random rand = new Random();
                    int coinFlip = rand.nextInt(2);
                    if (coinFlip == 0) {
                        goTo = "right";
                    } else {
                        goTo = "left";
                    }
                }
                System.out.println("[Ag]: I have found the column and I will go " + goTo);
                from = moveTo(goTo);
            }

        }
        // The agent chooses down and the env is clear of walls
        else if (position.equals("down") && !wallAtBottom.contains(from) && !envWallBottom.contains(from)) {
            from.y++;
            // If the agent is in the same Row as the client then it has found the Row
            if (from.y == goalLocation.y) {
                hasFoundY = true;
            }
            reward = reward - 1;
            System.out.println("[Ag]: Going down, no wall...");
        }
        // The agent chooses down but there is a wall that it hasn't found
        else if (position.equals("down") && !wallAtBottom.contains(from) && envWallBottom.contains(from)) {
            System.out.println("[Ag]: I bumped into a wall...");
            reward = reward - 100;
            // Add wall's location to 'beliefs'
            wallAtBottom.add(from);
        }
        // The agent chooses down, knows there is a wall there and hasn't found the
        // Column of the goal
        else if (position.equals("down") && wallAtBottom.contains(from) &&
                !hasFoundX) {
            System.out.println("[Ag]: There is a wall here...");
            // Depending on the goal's column move right or left
            if (from.x < goalLocation.x) {
                System.out.println("[Ag]: Going right");
                from = moveTo("right");
            } else if (from.x > goalLocation.x) {
                System.out.println("[Ag]: Going left");
                from = moveTo("left");
            }
        }
        // The agent chooses down, knows there is a wall there and has found the Column
        else if (position.equals("down") && wallAtBottom.contains(from) &&
                hasFoundX) {
            // If the agent is at first column move only right
            if (from.x == 0) {
                goTo = "right";
                from = moveTo(goTo);
                System.out.println("[Ag]: I have found the column and I'm on edge so I go right");
            }
            // If the agent is at fourth column move only left
            else if (from.x == 4) {
                goTo = "left";
                from = moveTo(goTo);
                System.out.println("[Ag]: I have found the column and I'm on edge so I go left");
            }
            // The agent is free to move either left or right
            else if (wallAtBottom.contains(from)) {
                // If agent hasn't deicided where to go, choose left or right randomly and stick
                // to that
                if (goTo.equals("nothing")) {
                    Random rand = new Random();
                    int coinFlip = rand.nextInt(2);
                    if (coinFlip == 0) {
                        goTo = "right";
                    } else {
                        goTo = "left";
                    }
                }
                System.out.println("[Ag]: I have found the column and I will go " + goTo);
                from = moveTo(goTo);
            }

        }
        // The agent chooses right and the env is clear of walls
        else if (position.equals("right") && !wallAtRight.contains(from) && !envWallRight.contains(from)) {
            from.x++;
            System.out.println("[Ag]: Going right, no wall...");
            // If the agent is in the same Col as the client then it has found the Col
            if (from.x == goalLocation.x) {
                hasFoundX = true;
            }
            reward = reward - 1;
        }
        // The agent chooses right but there is a wall that it hasn't found
        else if (position.equals("right") && !wallAtRight.contains(from) && envWallRight.contains(from)) {
            System.out.println("[Ag]: I bumped into a wall...");
            reward = reward - 100;
            // Add wall's location to 'beliefs'
            wallAtRight.add(from);
        }
        // The agent chooses right, knows there is a wall there and hasn't found the
        // Column of the goal
        else if (position.equals("right") && wallAtRight.contains(from) &&
                !hasFoundY) {
            // Depending on the goal's row move up or down
            System.out.println("[Ag]: There is a wall here...");
            if (from.y > goalLocation.y) {
                System.out.println("[Ag]: Going up");
                from = moveTo("up");
            } else if (from.y < goalLocation.y) {
                System.out.println("[Ag]: Going down");
                from = moveTo("down");
            }
        }
        // The agent chooses right, knows there is a wall there and has found the Row
        else if (position.equals("right") && wallAtRight.contains(from) &&
                hasFoundY) {
            // If the agent is at first row move only down
            if (from.y == 0) {
                System.out.println("[Ag]: I have found row and I'm on edge so I go down");
                goTo = "down";
                from = moveTo(goTo);
            }
            // If the agent is at fourth row move only up
            else if (from.y == 4) {
                System.out.println("[Ag]: I have found the row and I'm on edge so I go up");
                goTo = "up";
                from = moveTo(goTo);
            }
            // The agent is free to move either left or right
            else if (wallAtRight.contains(from)) {
                // If agent hasn't deicided where to go, choose up or down randomly and stick to
                // that
                if (goTo.equals("nothing")) {
                    Random rand = new Random();
                    int coinFlip = rand.nextInt(2);
                    if (coinFlip == 0) {
                        goTo = "up";
                    } else {
                        goTo = "down";
                    }
                }
                System.out.println("[Ag]: I have found row and I will go " + goTo);
                from = moveTo(goTo);
            }
        }
        // The agent chooses left and the env is clear of walls
        else if (position.equals("left") && !wallAtLeft.contains(from) && !envWallLeft.contains(from)) {
            from.x--;
            System.out.println("[Ag]: Going left, no wall...");
            // If the agent is in the same Col as the client then it has found the Col
            if (from.x == goalLocation.x) {
                hasFoundX = true;
            }

            reward = reward - 1;
        }
        // The agent chooses left but there is a wall that it hasn't found
        else if (position.equals("left") && !wallAtLeft.contains(from) && envWallLeft.contains(from)) {
            reward = reward - 100;
            System.out.println("[Ag]: I bumped into a wall...");
            // Add wall's location to 'beliefs'
            wallAtLeft.add(from);
        }
        // The agent chooses left, knows there is a wall there and hasn't found the
        // Column of the goal
        else if (position.equals("left") && wallAtLeft.contains(from) && !hasFoundY) {
            System.out.println("[Ag]: There is a wall here...");
            // Depending on the goal's row move up or down
            if (from.y > goalLocation.y) {
                System.out.println("[Ag]: Going up");
                from = moveTo("up");
            } else if (from.y < goalLocation.y) {
                System.out.println("[Ag]: Going down");
                from = moveTo("down");
            }
        }
        // The agent chooses left, knows there is a wall there and has found the Row
        else if (position.equals("left") && wallAtLeft.contains(from) && hasFoundY) {
            // If the agent is at first row move only down
            if (from.y == 0) {
                System.out.println("[Ag]: I have found the row and I'm on edge so I go down");
                goTo = "down";
                from = moveTo(goTo);
            }
            // If the agent is at fourth row move only up
            else if (from.y == 4) {
                System.out.println("[Ag]: I have found the row and I'm on edge so I go up");
                goTo = "up";
                from = moveTo(goTo);
            }
            // The agent is free to move either left or right
            else if (wallAtLeft.contains(from)) {
                // If agent hasn't deicided where to go, choose up or down randomly and stick to
                // that
                if (goTo.equals("nothing")) {
                    Random rand = new Random();
                    int coinFlip = rand.nextInt(2);
                    if (coinFlip == 0) {
                        goTo = "up";
                    } else {
                        goTo = "down";
                    }
                }
                System.out.println("[Ag]: I have found the row and I will go " + goTo);
                from = moveTo(goTo);
            }
        }

        maxActions--;
        return from;
    }

    /**
     * Agent action "loading" a client to the taxi.
     * 
     * @param clientLoc the location in which the agent is doing the action
     * @return true if the action is valid, otherwise false
     */
    boolean loadClient(Location clientLoc) {
        System.out.println("[Ag]: I loaded the client!");
        Location agLoc = getAgPos(0);
        // Agent must be available and at the same position as the client
        if (isAvailable && agLoc.equals(clientLoc)) {
            isAvailable = false; // agent is no longer available
            reward = reward - 1;
            maxActions--;
            // re-initialize variables that are used in moveTo
            hasFoundY = false;
            hasFoundX = false;
            goTo = "nothing";
            goalLocation = dropLocation;
            remove(CLIENT, agLoc); // remove client from grid
            return true;
        } else {
            reward = reward - 10;
            maxActions--;
            return false;
        }
    }

    /**
     * Agent action "unloading" a client from taxi.
     * 
     * @param destLoc the location in which the agent is doing the action
     * @return true if the action is valid, otherwise false
     */
    boolean unloadClient(Location destLoc) {
        System.out.println("[Ag]: I unloaded the client!");
        Location agLoc = getAgPos(0);
        // Agent must not be available and at the same position as the client
        if (!isAvailable && agLoc.equals(destLoc)) {
            isAvailable = true;
            reward = reward + 20;
            maxActions--;
            // re-initialize variables that are used in moveTo
            hasFoundY = false;
            hasFoundX = false;
            goTo = "nothing";
            dictionary.remove(bestClient); // remove client from dictionary
            clientID = bestClient;
            totalClients--;
            return true;
        } else {
            reward = reward - 10;
            maxActions--;
            return false;
        }
    }
}
