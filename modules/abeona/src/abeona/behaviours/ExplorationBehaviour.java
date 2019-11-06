package abeona.behaviours;

import abeona.Query;
import abeona.State;

public interface ExplorationBehaviour<StateType extends State> {
    void attach(Query<StateType> query);
    void detach(Query<StateType> query);
}
