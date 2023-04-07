import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.List;
import agents.*;



public class Main {

    public static void main(String[] args) throws StaleProxyException, IOException {


        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");
        ContainerController containerController = runtime.createMainContainer(profile);

        AgentController pastureController;

        List<AgentController> farmerAgents = null;

        Object[] agentArgs = new Object[0];
        for(int i = 0; i < 3; i++){
            //AgentController agentController = containerController.createNewAgent("FarmerAgent" + i , "agents.FarmerAgent", launchAgents.createFarmerArguments());
            AgentController agentController = containerController.createNewAgent("FarmerAgent" + i , "agents.FarmerAgent", agentArgs);
            farmerAgents.add(agentController);
            agentController.start();
        }

        pastureController = containerController.createNewAgent("pastureController", "agents.PastureAgent", agentArgs);
        //launchAgents.setPastureController(pastureController);
        pastureController.start();



    }

}