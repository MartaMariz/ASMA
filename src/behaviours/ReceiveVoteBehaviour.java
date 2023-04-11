package src.behaviours;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import src.agents.FarmerAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import src.agents.FarmerAgent;

public class ReceiveVoteBehaviour extends CyclicBehaviour {
    private final FarmerAgent agent;
    private MessageTemplate mt;

    public ReceiveVoteBehaviour(Agent a){
        this.agent = (FarmerAgent) a;
    }

    public String handleVote(FarmerAgent.Personality personality, float health, int cowNum){
        double score = Math.random();
        boolean value;
        switch(personality){
            case greedy:
                value = score < 0.9;
                break;
            case adaptive:
                value = score < 0.7;
                break;
            case regulated:
                value = score < 0.5;
                break;
            case cooperative:
                value = score < 0.3;
                break;
            default:
                value = score < 0;
        }
        return value ? "yes" : "no";
    }

    public void action() {
        //mt = MessageTemplate.MatchConversationId("voting");
        ACLMessage msg = myAgent.receive();

        int decision = countVotes();
        if(decision > 0){
            agent.resetVotes();
        }
        if(decision == 2){
            System.out.println("cow approved sending inform to pasture of new cow ");
            //sending request for new cow to pasture
            DFAgentDescription pasture_template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("provide-info");
            pasture_template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, pasture_template);
                if (result.length > 0) {
                    AID agentID = result[0].getName();
                    System.out.println("Found agent " + agentID.getLocalName() + " using Yellow Pages");

                    // Send a message to the registered agent
                    // Here we are assuming that the registered agent has a behaviour that can handle this message
                    ACLMessage inform_new_cow = new ACLMessage(ACLMessage.INFORM);
                    inform_new_cow.addReceiver(agentID);
                    int id = this.agent.addCow();
                    inform_new_cow.setContent("cow:" + id);
                    agent.send(inform_new_cow);
                } else {
                    System.out.println("No agents found");
                }
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

        if (msg != null) {
            System.out.println(myAgent.getName() + " received " + msg.getContent() + " from " + msg.getSender().getName());
            ACLMessage reply = msg.createReply();

            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                String[] tokens  = msg.getContent().split("-");
                String vote = this.handleVote(agent.getPersonality(), Float.parseFloat(tokens[1]), Integer.parseInt(tokens[2]));

                if(vote.equals("yes"))
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                else
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

                reply.setContent(vote);
                myAgent.send(reply);
            }
            else if (msg.getPerformative() == ACLMessage.INFORM){
                String[] tokens  = msg.getContent().split("-");

                System.out.println(myAgent.getName()+" is starting vote");
                ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
                AID[] farmerAgents = agent.getFarmerAgents();
                for (int i = 0; i < farmerAgents.length; ++i) {
                    proposal.addReceiver(farmerAgents[i]);
                }
                proposal.setContent("vote" + msg.getContent());
                proposal.setConversationId("voting-" + tokens[1] + "-" + tokens[2]);
                proposal.setReplyWith("proposal" + System.currentTimeMillis()); // Unique value
                myAgent.send(proposal);
            }
            else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                agent.voteYes();
            }
            else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL){
                agent.voteNo();
            }
            msg = null;

        }
        else{
            block();
        }

    }

    private int countVotes() {
        int yes = agent.getYesVotes();
        int no = agent.getNoVotes();
        int res;
        if((yes + no) == agent.getNumFarmers() - 1 && agent.getNumFarmers() != 0){
            System.out.println("All voted");
            if (yes > no)
                res = 2;
            else res = 1;
        }
        else res = 0;
        return res;
    }
}