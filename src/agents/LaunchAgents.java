package src.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import src.agents.FarmerAgent;

public class LaunchAgents {
    public Object[] createAgentArguments(){
        List<Object> args = new ArrayList<>();

        //personality
        Random rand = new Random();
        int index = rand.nextInt(src.agents.FarmerAgent.Personality.values().length);
        args.add(FarmerAgent.Personality.values()[index]);
        int random_int = 2000;//voting time
        args.add(random_int);
        int cowsHealthTicker = 1000;
        args.add(cowsHealthTicker);

        float cowHealthDecreaseRate = 10;
        args.add(cowHealthDecreaseRate);

        return args.toArray();
    }

    public Object[] createPastureArguments() {
        List<Object> args = new ArrayList<>();

        float health = 2000.0f;
        float regenerationRate = 20.0f; 
        float decreaseFactor = 10.0f; 
        int pastureTickerTime = 1000;
        int cowsHealthTicker = 2000;
        args.add(health);
        args.add(regenerationRate);
        args.add(decreaseFactor);
        args.add(pastureTickerTime);
        args.add(cowsHealthTicker);
        return args.toArray();
    }
}
