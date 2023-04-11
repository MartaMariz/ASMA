package src.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import src.behaviours.PastureBehaviour;

public class PastureAgent extends Agent {
    private static final String AGENT_NAME = "PASTURE";
    private int health;
    private int regenerationRate;
    private int cowNumber;


    public void setup() {
        System.out.println("Hello world!");

        // Register the agent with the Yellow Pages service
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("provide-info");
        sd.setName("provide-info-pasture");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("Agent " + AGENT_NAME + " registered with Yellow Pages");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Add agent behaviours here
        addBehaviour(new PastureBehaviour(this));
    }

    public void addCow(){
        this.cowNumber ++;
        System.out.println("New number of cows " + this.cowNumber);
    }
}