package src.agents;

import agents.Cow;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import src.behaviours.PastureBehaviour;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PastureAgent extends Agent {

    private static final String AGENT_NAME = "PASTURE";
    private float health;
    private float regenerationRate;
    private float decreaseFactor;



    private Hashtable<String, List<Cow>> cowDict;


    public void setup() {
        System.out.println("Hello world!");
        Object[] args = this.getArguments();
        this.health = (float) args[0];
        this.regenerationRate = (float) args[1];
        this.decreaseFactor = (float) args[2];
        int pastureTickerTime = (int) args[3];
        int cowsHealthTicker = (int) args[4];
        this.cowDict = new Hashtable<String, List<Cow>>();


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
                try {
                    FileWriter writer = new FileWriter("output.txt", true);
                    writer.write("Current Pasture Health: " + getPastureHealth() + "\n");
                    System.out.println("Current Pasture Health: " + getPastureHealth() + "\n");
                    for (Map.Entry<String, List<Cow>> entry : cowDict.entrySet()) {
                        String farmer = entry.getKey();
                        List<Cow> cowList = entry.getValue();
                        writer.write("Cows for farmer " + farmer + ":\n");
                        System.out.println("Cows for farmer " + farmer + ":\n");
                        for (Cow cow : cowList) {
                            writer.write(cow.toString() + "\n");
                            System.out.println(cow.toString() + "\n");
                        }
                    }
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }
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
        int totalCows = 0;
        for (List<Cow> cowList: cowDict.values()){
            totalCows += cowList.size();
        }
        return totalCows;
    }
    public void addCow(String farmer){
        Cow cow = new Cow(100);
        List<Cow> cowList = cowDict.get(farmer);
        if (cowList == null){
            cowList = new ArrayList<>();
            cowDict.put(farmer, cowList);
        }
        cowList.add(cow);
    }

    public void updatePastureHealth(){
        float toDecrease = this.decreaseFactor * this.getCowNumber();
        System.out.println("Current pasture update value :" + (regenerationRate - toDecrease));
        this.health += regenerationRate - toDecrease;
        if(this.health < 0){
            this.health = 0;
        }
    }


    public void updateCowHealth() {
        for (List<Cow> cowList : cowDict.values()) {
            Iterator<Cow> iterator = cowList.iterator();
            while (iterator.hasNext()) {
                Cow cow = iterator.next();
                cow.decreaseCowHealth(20); //cows health decay
                if (cow.getHealth() <= 0) {
                    iterator.remove();
                }
            }
        }
    }

    public int getTotalCowsByFarmer(String farmer){
        if(this.cowDict.get(farmer) == null){
            return 0;
        }
        return this.cowDict.get(farmer).size();
    }
}