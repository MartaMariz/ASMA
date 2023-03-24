package agents

import jade.core.Agent;
import behaviours.*
public class FarmerAgent extends Agent {
    private enum Personality{greedy, cooperative, adaptive, regulated};

    private int greed;
    private Map <Integer, Integer> cows;
    private Map<Integer, Integer> pastVotes;


    public void setup() {


        addBehaviour(new FarmerBehaviour())

        System.out.println("Hello world!");
    }
}