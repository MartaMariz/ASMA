package agents;

import jade.core.Agent;
import behaviours.*;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.lang.*;
import java.util.Map;

public class FarmerAgent extends Agent {
    // The list of known farmer agents
    private AID[] farmerAgents;

    public enum Personality {greedy, cooperative, adaptive, regulated};

    Personality personality;

    private int yesVotes = 0;
    private int noVotes = 0;

    private int greed;
    private Map<Integer, Integer> cows;
    private Map<Integer, Integer> pastVotes;

    private int flag = 1;

    public ReceiveVoteBehaviour receiveBehaviour;

    /*
    public FarmerAgent(int greed){
        this.greed = greed;
    }
     */
    public void voteYes(){
        yesVotes++;
    }

    public int getYesVotes(){
        return yesVotes;
    }

    public void voteNo(){
        noVotes++;
    }

    public int getNoVotes(){
        return noVotes;
    }

    public void resetVotes(){
        yesVotes = 0;
        noVotes = 0;
    }

    public int getNumFarmers(){
        if (farmerAgents == null)
                return 0;
        return farmerAgents.length;
    }

    public AID[] getFarmerAgents() {
        return this.farmerAgents;
    }

    public void setup() {
        this.personality = Personality.greedy;//should be parsed FIXME
        // Register the book-selling service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("farming");
        sd.setName("JADE-farming");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        int min = 10000;
        int max = 60000;
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);

        this.addBehaviour(new TickerBehaviour(this, random_int ) {
        //this.addBehaviour(new TickerBehaviour(this, 6000 ) {
            @Override
            protected void onTick() {
                // Update the list of farmer agents
                resetVotes();

                DFAgentDescription template = new DFAgentDescription();
                template.addServices(sd);


                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    farmerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        if(!result[i].getName().equals(myAgent.getName()))
                            farmerAgents[i] = result[i].getName();
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }

                System.out.println(myAgent.getName()+" is asking pasture vote");
                ACLMessage proposal = new ACLMessage(ACLMessage.REQUEST);
                // ir buscar pasto
                //propsal.addReceiver()
                proposal.setContent("vote");
                proposal.setConversationId("voting");
                proposal.setReplyWith("proposal" + System.currentTimeMillis()); // Unique value
                myAgent.send(proposal);
            }
        });
        receiveBehaviour = new ReceiveVoteBehaviour(this);
        this.addBehaviour(receiveBehaviour);

        System.out.println("Farmer agent " + getAID().getName() + " is ready.");

    }

    public void takeDown() {
        System.out.println("Farmer agent " + getAID().getName() + " terminating.");

        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public Personality getPersonality(){
        return this.personality;
    }

}