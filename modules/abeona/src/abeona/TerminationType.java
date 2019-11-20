package abeona;

/**
 * The different ways an exploration might be ended.
 */
public enum TerminationType {
    /**
     * Signifies that the exploration ended because the frontier was empty when trying to evaluate the next state
     */
    FrontierExhaustion,
    /**
     * Signifies that the exploration ended because the {@link TerminateExplorationSignal} was thrown
     */
    ManualTermination,
    /**
     * Signifies a {@link Throwable} other than {@link TerminateExplorationSignal} was thrown during exploration.
     */
    RuntimeError,
}
