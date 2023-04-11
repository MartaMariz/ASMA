package src.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LaunchAgents {
    public Object[] createAgentArguments(){
        List<Object> args = new ArrayList<>();

        //personality
        Random rand = new Random();
        int index = rand.nextInt(FarmerAgent.Personality.values().length);
        args.add(FarmerAgent.Personality.values()[index]);
        return args.toArray();
    }
}
