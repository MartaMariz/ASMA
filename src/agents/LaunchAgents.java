package src.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LaunchAgents {
    public Object[] createAgentArguments(){
        List<Object> args = new ArrayList<>();

        //personality
        Random rand = new Random();
        int index = rand.nextInt(src.agents.FarmerAgent.Personality.values().length);
        //args.add(FarmerAgent.Personality.values()[index]);
        args.add(src.agents.FarmerAgent.Personality.greedy);
        return args.toArray();
    }
}
