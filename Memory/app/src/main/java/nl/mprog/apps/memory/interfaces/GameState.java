package nl.mprog.apps.memory.interfaces;

public abstract class GameState {

    protected Integer current_level;

    protected Integer timeLimit;

    protected Integer maximumChances;

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setMaximumChances(Integer maximumChances) {
        this.maximumChances = maximumChances;
    }

    public abstract Integer getNumberOfCards();

    public abstract void nextStage();

    /**
     * Reset to the first stage from the set
     */
    public abstract void resetStages();

    /**
     * Reset to the initial stage
     */
    public abstract void reset();

    public Integer sleep() {
        return this.current_level;
    }

    public void wakeup(Integer current_level) {
        this.current_level = current_level;
    }
}