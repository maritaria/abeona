package abeona;

/**
 * Throwable that forces the exploration to end immediately.
 * It inherits {@link Error} so it can be thrown freely without needing to be declared.
 */
public class TerminateExplorationSignal extends Error {
}
