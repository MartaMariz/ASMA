package src.agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import src.behaviours.PastureBehaviour;

public class PastureAgent extends Agent {

    private static final String AGENT_NAME = "PASTURE";
    private float health;
    private float regenerationRate;
    private float decreaseFactor;
    private int cowNumber;

    public PastureAgent(int health, float regenerationRate){
        this.health = health;
        this.regenerationRate = regenerationRate;
        this.cowNumber = 0;
    }
    public PastureAgent(){}


    public void setup() {
        System.out.println("Hello world!");
        Object[] args = this.getArguments();
        this.health = (float) args[0];
        this.regenerationRate = (float) args[1];
        this.decreaseFactor = (float) args[2];
        int pastureTickerTime = (int) args[3];
        int cowsHealthTicker = (int) args[4];

        // Register the agent with the Yellow Pages service
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("provide-info");
        sd.setName("provide-info-pasture");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("Agent " + AGENT_NAME + " registered with Yellow Pages");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Add agent behaviours here
        addBehaviour(new PastureBehaviour(this));


        addBehaviour(new TickerBehaviour(this, pastureTickerTime) {
            @Override
            protected void onTick() {
                updatePastureHealth();
                System.out.println("Current Pasture Health: " + getPastureHealth());
            }
        });

        addBehaviour(new TickerBehaviour(this, cowsHealthTicker) {
            @Override
            protected void onTick() {
                updateCowHealth();
            }
        });
    }
    public float getPastureHealth(){
        return this.health;
    }

    public int getCowNumber(){
        return this.cowNumber;
    }
    public void addCow(){
        this.cowNumber ++;
        System.out.println("New number of cows " + this.cowNumber);
    }

    public void updatePastureHealth(){
        float toDecrease = this.decreaseFactor * this.cowNumber;
        System.out.println("Current pasture update value :" + (regenerationRate - toDecrease));
        this.health += regenerationRate - toDecrease;
    }

    public void updateCowHealth() {
        if(this.cowNumber <= 0)
            return;
        this.cowNumber--;
    }
}