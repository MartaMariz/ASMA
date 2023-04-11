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
        return args.toArray();
    }

    public Object[] createPastureArguments() {
        List<Object> args = new ArrayList<>();

        //health
        float health = 100.0f;
        float regenerationRate = 2.0f;
        float decreaseFactor = 2.0f;
        int pastureTickerTime = 2000;
        int cowsHealthTicker = 1000;
        args.add(health);
        args.add(regenerationRate);
        args.add(decreaseFactor);
        args.add(pastureTickerTime);
        args.add(cowsHealthTicker);
        return args.toArray();
    }
}
