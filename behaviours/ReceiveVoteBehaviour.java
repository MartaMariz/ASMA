package behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveVoteBehaviour extends CyclicBehaviour {
    private MessageTemplate mt;
    public void action() {
        mt = MessageTemplate.MatchConversationId("voting");
        ACLMessage cfp = myAgent.receive(mt);

        if (cfp != null){
            switch (cfp.getContent()){
                case "confirmation":
                    System.out.println("Received a confirmation");
                    ACLMessage confirmation = new ACLMessage(ACLMessage.CONFIRM);
                    System.out.println("Sending a confirmation message");
                    confirmation.addReceiver(cfp.getSender());
                    break;

                case "vote":
                    ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
                    //this should be with personality, based on random for now

                    boolean value = Math.random() < 0.5;

                    proposal.addReceiver(cfp.getSender());
                    proposal.setContent(value ? "yes" : "no");
                    proposal.setConversationId("voting-answer");
                    System.out.println("Voting propose ongoing! Sent a " + (value ? "yes" : "no") + " answer back");
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