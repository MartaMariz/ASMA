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

    private int greed;
    private Map<Integer, Integer> cows;
    private Map<Integer, Integer> pastVotes;

    public ReceiveVoteBehaviour receiveBehaviour;

    /*
    public FarmerAgent(int greed){
        this.greed = greed;
    }
     */


    public void setup() {
        this.greed = 1; //temporary
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
        int min = 5000;
        int max = 15000;
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        this.addBehaviour(new TickerBehaviour(this, random_int ) {
        //this.addBehaviour(new TickerBehaviour(this, 6000 ) {
            @Override
            protected void onTick() {
                // Update the list of farmer agents
                System.out.println("sending");

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
                myAgent.addBehaviour(new InitiateVoteBehaviour());
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

    public void removeBehaviour(){
        this.removeBehaviour(receiveBehaviour);
    }


    public class InitiateVoteBehaviour extends Behaviour {
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt;
        private int step = 0;
        private int yesVotes = 0;
        private int noVotes = 0;

        public void action() {
            ACLMessage msg = myAgent.receive();
            switch (step) {
                case 0:
                    //removeBehaviour(receiveBehaviour);
                    // Send the cfp to all sellers

                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < farmerAgents.length; ++i) { //temos de dar acesso
                        cfp.addReceiver(farmerAgents[i]);
                    }
                    cfp.setContent("confirmation");
                    cfp.setConversationId("voting");
                    //cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.MatchConversationId("confirmation");
                    //mt = MessageTemplate.and(MessageTemplate.MatchConversationId("confirmation"),
                    //                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                    step = 1;
                    break;

                case 1:
                    // Receive all proposals/refusals from agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        System.out.println(myAgent.getName() + "received msg" + reply.getContent() + "from" + reply.getSender()  );
                        // Reply received
                        //repliesCnt++;
                        if (reply.getPerformative() == ACLMessage.CONFIRM) {
                            repliesCnt++;
                            System.out.println("Current number of replies gotten " + repliesCnt);
                        }

                        if (repliesCnt - 1 >= farmerAgents.length) {
                            // We received all replies
                            System.out.println("received all replies");
                            repliesCnt = 0;
                            step = 2;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    // send msg to pasture asking for state
                    step = 3;
                    break;

                case 3:
                    ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
                    for (int i = 0; i < farmerAgents.length; ++i) { //temos de dar acesso
                        proposal.addReceiver(farmerAgents[i]);
                    }
                    proposal.setContent("vote");
                    proposal.setConversationId("voting");
                    proposal.setReplyWith("proposal" + System.currentTimeMillis()); // Unique value
                    myAgent.send(proposal);
                    break;
                case 4:
                    ACLMessage vote = myAgent.receive(mt);
                    if (vote != null) {
                        // Reply received
                        repliesCnt++;

                        if (vote.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            yesVotes++;
                        }
                        else if(vote.getPerformative() == ACLMessage.REJECT_PROPOSAL){
                            noVotes++;
                        }

                        if (repliesCnt >= farmerAgents.length) {
                            // We received all replies
                            repliesCnt = 0;
                            step = 5;
                        }
                    } else {
                        block();
                    }
                    break;
                case 5:
                    // send msg to pasture saying result of voting
                    step = 0;
                    break;

            }

        }

        @Override
        public boolean done() {
            return false;
        }
    }
}