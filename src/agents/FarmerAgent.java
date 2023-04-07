package agents;

import jade.core.Agent;
import behaviours.*;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
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

    private enum Personality {greedy, cooperative, adaptive, regulated};

    private int greed;
    private Map<Integer, Integer> cows;
    private Map<Integer, Integer> pastVotes;

    /*
    public FarmerAgent(int greed){
        this.greed = greed;
    }
     */


    public void setup() {
        this.greed = 1; //temporary

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

        this.addBehaviour(new TickerBehaviour(this, 6000 ) {
            @Override
            protected void onTick() {
                // Update the list of farmer agents
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, dfd);
                    farmerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        farmerAgents[i] = result[i].getName();
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                myAgent.addBehaviour(new InitiateVoteBehaviour());

            }
        });

        this.addBehaviour(new ReceiveVoteBehaviour());

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


    public class InitiateVoteBehaviour extends Behaviour {
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt;
        private int step = 0;

        public void action() {
            ACLMessage msg = myAgent.receive();
            switch (step) {
                case 0:
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < farmerAgents.length; ++i) { //temos de dar acesso
                        cfp.addReceiver(farmerAgents[i]);
                    }
                    cfp.setContent("vote");
                    cfp.setConversationId("voting");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                    step = 1;
                    break;

                case 1:
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                /*
                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                    // This is an offer

                    int price = Integer.parseInt(reply.getContent());
                    if (bestSeller == null || price < bestPrice) {
                        // This is the best offer at present
                        bestPrice = price;
                        bestSeller = reply.getSender();
                    }
                }

                 */
                        repliesCnt++;
                        if (repliesCnt >= farmerAgents.length) {
                            // We received all replies
                            step = 2;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
            }

        }

        @Override
        public boolean done() {
            return false;
        }
    }
}