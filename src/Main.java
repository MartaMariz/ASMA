package src;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import src.agents.LaunchAgents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws StaleProxyException, IOException {



        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");
        ContainerController containerController = runtime.createMainContainer(profile);

        AgentController pastureController;

        List<AgentController> farmerAgents = new ArrayList();
        LaunchAgents launchAgents = new LaunchAgents();

        Object[] pastureArgs = launchAgents.createPastureArguments();
        for(int i = 0; i < 3; i++){
            Object[] farmerArgs = launchAgents.createAgentArguments();
            AgentController agentController = containerController.createNewAgent("FarmerAgent" + i , "src.agents.FarmerAgent", farmerArgs);
            farmerAgents.add(agentController);
            agentController.start();
        }

        pastureController = containerController.createNewAgent("pastureController", "src.agents.PastureAgent", pastureArgs);
        //launchAgents.setPastureController(pastureController);
        pastureController.start();



    }

}
