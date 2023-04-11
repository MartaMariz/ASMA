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
                System.out.println("pasture recieves request");
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("bue bom pasto");//info sobre o estado do pasture que depois se dá parse do outro lado
                this.agent.send(reply);
            }

            // ver acl messages
            if (msg.getPerformative() == ACLMessage.INFORM) { //answer from farmer
                // por ou nao vaca no pasto
                System.out.println(msg.getContent());
                this.agent.addCow();
            }
        }
        else{
            block();
        }
    }
}
