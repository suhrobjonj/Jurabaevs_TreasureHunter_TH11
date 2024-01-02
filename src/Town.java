/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private final Shop shop;
    private final Terrain terrain;
    private final String[] treasures;
    boolean townSearched;
    private String printMessage;
    private boolean toughTown;
    private boolean dugGold = false;
    private boolean itemCanBreak = true;
    private boolean samuraiMode = false;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, String mode) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        if (mode.equals("e")) {
            itemCanBreak = false;
        }
        if (mode.equals("s")) {
            samuraiMode = true;
        }

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        //a random treasure will be selected for each town
        treasures = new String[]{"a crown", "a trophy", "a gem", "dust"};
        townSearched = false;
    }


    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak() && itemCanBreak) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, your " + item + " broke.";
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }



    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (!hunter.hasItemInKit("sword")) {
                printMessage = "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
                if (Math.random() > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += "\nYou won the brawl and receive " + goldDiff + " gold.";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                    hunter.changeGold(-goldDiff);
                }
            } else {
                printMessage = "You want trouble, stranger!  You got it!\nOh um... no...not the samurai!\n..here's all of my gold\n";
                hunter.changeGold(goldDiff);
            }
        }
    }

    public String lookForTreasure() {
        int treasureIndx = (int) (Math.random() * 3);
        if (townSearched) {
            return "You have already searched this town.";
        } else if (hunter.addTreasure(treasures[treasureIndx], treasureIndx)) {
            townSearched = true;
            return "You found " + treasures[treasureIndx] + "!";
        } else {
            townSearched = true;
            return "You found " + treasures[treasureIndx] + " but you already have one so you throw it away.";
        }
    }

    public void digForGold() {
        if (hunter.hasItemInKit("shovel")) {
            if (!dugGold) {
                int chance = (int) (Math.random() * 100) + 1;
                if (chance < 50) {
                    int gold = (int) (Math.random() * 20) + 1;
                    hunter.changeGold(gold);
                    printMessage = "\nYou dug up " + gold + " gold!";
                } else {
                    printMessage = "\nYou dug but only found dirt";
                }
                dugGold = true;
            } else {
                printMessage = "\nYou already dug for gold in this town.";
            }
        } else {
            printMessage = "\nYou can't dig for gold without a shovel";
        }

    }


    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = (int) (Math.random() * 6) + 1;
        if (rnd == 1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            return new Terrain("Desert", "Water");
        } else if (rnd == 5){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}