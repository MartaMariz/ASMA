package behaviours;

import agents.FarmerAgent;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
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
        boolean value = false;
        switch(personality){
            case greedy:
            case adaptive:
            case regulated:
            case cooperative:
            default:
                value = score < 0.5;
        }
        return value ? "yes" : "no";
    }

    public void action() {
        mt = MessageTemplate.MatchConversationId("voting");
        ACLMessage cfp = myAgent.receive(mt);

        if (cfp != null){
            switch (cfp.getContent()){
                case "confirmation":
                    ACLMessage confirmation = new ACLMessage(ACLMessage.CONFIRM);
                    System.out.println("Sending a confirmation message");
                    confirmation.addReceiver(cfp.getSender());
                    confirmation.setContent("confirming");
                    confirmation.setConversationId("confirmation");
                    myAgent.send(confirmation);
                    break;

                case "vote":
                    ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);

                    String answer = handleVote(this.agent.getPersonality());


                    proposal.addReceiver(cfp.getSender());
                    proposal.setContent(answer);
                    proposal.setConversationId("voting-answer");
                    System.out.println("Voting propose ongoing! Sent a " + answer + " answer back");
                    myAgent.send(proposal);
                    break;
                default:
                    break;
            }
        }
        else{
            block();
        }

    }
}