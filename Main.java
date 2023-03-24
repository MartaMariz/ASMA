import agents.LaunchAgents;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Logger;

import java.io.IOException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws StaleProxyException, IOException {

        LaunchAgents launchAgents = LaunchAgents.getInstance();
        Logger.getInstance();

        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");
        ContainerController containerController = runtime.createMainContainer(profile);

        AgentController controlPastureController;

        List<AgentController> farmerAgents;

        airplaneAgents = launchAgents.getAirplaneAgents();


        for(int i = 0; i < 20; i++){
            AgentController agentController = containerController.createNewAgent("AirplaneAgent" + i , "agents.AirplaneAgent", launchAgents.createAirplaneArguments());
            airplaneAgents.add(agentController);
            agentController.start();
        }

        controlTowerController = containerController.createNewAgent("ControlTowerAgent", "agents.ControlTowerAgent", launchAgents.createControlTowerArguments());
        launchAgents.setControlTowerController(controlTowerController);
        controlTowerController.start();



    }

}