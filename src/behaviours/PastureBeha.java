package behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PastureBeha extends CyclicBehaviour {
    public void action() {
        ACLMessage msg = myAgent.receive();


        if (msg != null) {
            System.out.println(myAgent.getName() + " received " + msg.getContent() + " from " + msg.getSender().getName());
            ACLMessage reply = msg.createReply();

            if (msg.getPerformative() == ACLMessage.REQUEST) {
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("");
                myAgent.send(reply);
            }

            if (msg.getPerformative() == ACLMessage.PROPAGATE) {
                // por ou nao vaca no pasto
            }
        }
        else{
            block();
        }
    }
}
