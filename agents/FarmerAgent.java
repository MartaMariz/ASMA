package agents

import jade.core.Agent;
import behaviours.*
import jade.core.AID;

public class FarmerAgent extends Agent {
    // The list of known farmer agents
    private AID[] farmerAgents;

    private enum Personality{greedy, cooperative, adaptive, regulated};

    private int greed;
    private Map <Integer, Integer> cows;
    private Map<Integer, Integer> pastVotes;

    public FarmerAgent(int greed){
        this.greed = greed;
    }


    public void setup() {
        System.out.println("Farmer agent "+getAID().getName()+" is ready.");

        // Register the book-selling service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("farming");
        sd.setName("JADE-farming");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new TickerBehaviour(this, 60000*greed) {
            protected void onTick() {
                // Update the list of farmer agents
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, fdf);
                    farmerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        farmerAgents[i] = result[i].getName();
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                myAgent.addBehaviour(new InitiateVoteBehaviour());
            }
        } );

    }

    public void takeDown(){
        System.out.println("Farmer agent "+getAID().getName()+" terminating.");

        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}