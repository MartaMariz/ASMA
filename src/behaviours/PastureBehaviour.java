package src.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import src.agents.FarmerAgent;
import src.agents.PastureAgent;

public class PastureBehaviour extends CyclicBehaviour {
    private final PastureAgent agent;
    public PastureBehaviour(Agent a){
        this.agent = (PastureAgent) a;
    }

    public void action() {
        ACLMessage msg = myAgent.receive();


        if (msg != null) {
            System.out.println(this.agent.getName() + " received " + msg.getContent() + " from " + msg.getSender().getName());
            ACLMessage reply = msg.createReply();

            if (msg.getPerformative() == ACLMessage.REQUEST) {
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("pasture status/" + this.agent.getPastureHealth() + "/" + this.agent.getCowNumber());
                this.agent.send(reply);
            }

            if (msg.getPerformative() == ACLMessage.INFORM) {
                System.out.println(msg.getContent());
                this.agent.addCow(msg.getSender().getName());
            }
        }
        else{
            block();
        }
    }
}