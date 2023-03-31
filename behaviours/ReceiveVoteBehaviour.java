import jade.core.Agent;
import jade.core.behaviours.Behaviour;

private class ReceiveVoteBehaviour extends CyclicBehaviour {
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            // Message received. Process it
            String proposal = msg.getContent();
            ACLMessage reply = msg.createReply();
            /*Integer price = (Integer) catalogue.get(title);
            if (price != null) {
                // The requested book is available for sale. Reply with the price
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(String.valueOf(price.intValue()));
            }
            else {
                // The requested book is NOT available for sale.
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent(“not-available”);
            }
             */
            myAgent.send(reply);
        }
        else {
            block();
        }
    }
}