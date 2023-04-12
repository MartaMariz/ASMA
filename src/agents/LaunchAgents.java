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

        int min = 10000;
        int max = 60000;
        //int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        int random_int = 4000;
        args.add(random_int);
        int cowsHealthTicker = 2000;
        args.add(cowsHealthTicker);

        float cowHealthDecreaseRate = 10;
        args.add(cowHealthDecreaseRate);

        return args.toArray();
    }

    public Object[] createPastureArguments() {
        List<Object> args = new ArrayList<>();

        //health
        float health = 100.0f;
        float regenerationRate = 2.0f;
        float decreaseFactor = 2.0f;
        int pastureTickerTime = 2000;
        int cowsHealthTicker = 15000;
        args.add(health);
        args.add(regenerationRate);
        args.add(decreaseFactor);
        args.add(pastureTickerTime);
        args.add(cowsHealthTicker);
        return args.toArray();
    }
}
