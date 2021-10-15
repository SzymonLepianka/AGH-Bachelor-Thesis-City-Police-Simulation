package entities;

public interface IAgent {
    // TODO Add methods Perform Action, Think etc

    void updateStateSelf() throws Exception;

    void performAction() throws Exception;

    void takeOrder(Patrol.Action action);
}
