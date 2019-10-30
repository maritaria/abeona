package abeona.behaviours;

import abeona.ExplorationQuery;
import abeona.State;

public interface ExplorationBehaviour<StateType extends State> {
    void attach(ExplorationQuery<StateType> explorationQuery);
    void detach(ExplorationQuery<StateType> explorationQuery);
}
