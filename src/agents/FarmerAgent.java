package src.agents;

import agents.Cow;
import jade.core.Agent;

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
import src.behaviours.ReceiveVoteBehaviour;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FarmerAgent extends Agent {
    // The list of known farmer agents
    private AID[] farmerAgents;

    public enum Personality {greedy, cooperative, adaptive, regulated};

    Personality personality;

    private int yesVotes = 0;
    private int noVotes = 0;

    private int greed;
    private Map<String, Integer> cows;
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

    public int addCow(){
        return 0;
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
        Object[] args = this.getArguments();
        this.personality = (Personality) args[0];
        int tickerRate = (int) args[1];

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

        this.addBehaviour(new TickerBehaviour(this, tickerRate ) {
            @Override
            protected void onTick() {
                // Update the list of farmer agents
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
                DFAgentDescription pasture_template = new DFAgentDescription();
                ServiceDescription sd_pasture = new ServiceDescription();
                sd_pasture.setType("provide-info");
                pasture_template.addServices(sd_pasture);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, pasture_template);
                    if (result.length > 0) {
                        AID agentID = result[0].getName();
                        System.out.println( myAgent.getName() + " Found agent " + agentID.getName() + " using Yellow Pages");

                        // Send a message to the registered agent
                        // Here we are assuming that the registered agent has a behaviour that can handle this message
                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.addReceiver(agentID);
                        msg.setReplyWith("pasture info" + System.currentTimeMillis()); // Unique value
                        msg.setContent("Hello pasture from AnotherAgent");
                        myAgent.send(msg);
                    } else {
                        System.out.println("No agents found");
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }


                ACLMessage msg = myAgent.receive();

                if (msg != null){
                    System.out.println(myAgent.getName() + " received " + msg.getContent() + " from " + msg.getSender().getName());
                    ACLMessage reply = msg.createReply();

                    if(msg.getPerformative() == ACLMessage.INFORM) {
                        System.out.println(myAgent.getName()+" is asking pasture vote");
                        ACLMessage proposal = new ACLMessage(ACLMessage.REQUEST);
                        // ir buscar pasto
                        for (int i = 0; i < farmerAgents.length; i++){
                            proposal.addReceiver(farmerAgents[i]);
                        }
                        proposal.setContent("vote");
                        proposal.setConversationId("voting");
                        proposal.setReplyWith("proposal" + System.currentTimeMillis()); // Unique value
                        myAgent.send(proposal);
                    }
                }


        }});
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