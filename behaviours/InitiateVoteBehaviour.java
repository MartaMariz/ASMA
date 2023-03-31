import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class InitiateVoteBehaviour extends Behaviour {
    private int step = 0;

    public void action() {
        ACLMessage msg = myAgent.receive();
        switch (step):
        case 0:
            // Send the cfp to all sellers
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            for (int i = 0; i < farmerAgents.length; ++i) { //temos de dar acesso
                cfp.addReceiver(farmerAgents[i]);
            }
            cfp.setContent();
            cfp.setConversationId("voting");
            cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
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
            }
            else {
                block();
            }
            break;
        case 2:
    }
}
