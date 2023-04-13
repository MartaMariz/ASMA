package agents;

public class Cow {
    private float health;
    public Cow(float health){
        this.health = health;
    }

    public void decreaseCowHealth(float decreaseRate) {
        this.health -= decreaseRate;
    }

    public float getHealth(){
        return this.health;
    }
    @Override
    public String toString() {
        return "Cow with " + this.health + "health.";
    }
}
