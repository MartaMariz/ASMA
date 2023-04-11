package src.behaviours;

import src.agents.FarmerAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveVoteBehaviour extends CyclicBehaviour {
    private final FarmerAgent agent;
    private MessageTemplate mt;

    public ReceiveVoteBehaviour(Agent a){
        this.agent = (FarmerAgent) a;
    }

    public String handleVote(FarmerAgent.Personality personality){
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

        if (msg != null) {
            System.out.println(myAgent.getName() + " received " + msg.getContent() + " from " + msg.getSender().getName());
            ACLMessage reply = msg.createReply();

            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                String vote = this.handleVote(agent.getPersonality());

                if(vote.equals("yes"))
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                else
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

                reply.setContent(vote);
                myAgent.send(reply);
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
        if((yes + no) == agent.getNumFarmers() - 1 && agent.getNumFarmers() != 0){
            System.out.println("All voted");
            if (yes > no)
                return 2;
            else return 1;
        }
        else return 0;
    }
}