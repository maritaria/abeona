package abeona.aspects;

/**
 * The base interface for dynamic behaviours.
 * This interface defines a modifiable piece of behaviour that can be extended by registering handlers.
 * The handlers can also be unregistered at a later point in time.
 *
 * Once a handler is registered it may be invoked up until the moment it is unregistered.
 * The owner of the tap instance has control over the moment at which the handlers are invoked.
 *
 * The invocation order of the handlers is in the same sequence (or the complete reverse) of the registration order.
 * The direction (original order or reverse order) depends on the specific behaviour type.
 * Monitor tappables (such as events) ensure the handler invoke order is equal to the registration order.
 * Interception tappables (such as custom implementations) ensure the handlers are invoked in reverse registration order.
 *
 * Classes implementing this interface should clearly indicate what the order of invocation of the handlers is.
 *
 * @param <Handler> The signature of the callback handlers
 */
public interface Tap<Handler> {
    /**
     * Adds a handler to the callback list.
     * A callback handler may appear multiple times on the list.
     * For convenience the registered handler is returned in case you write one as a lambda.
     *
     * @param handler The handler to register for the tap
     * @return The handler registered in the tap by the invocation. To unregister the handler this value has to be passed to {@link Tap#unTap(Handler)}.
     */
    Handler tap(Handler handler);

    /**
     * Unregisters a given handler from the callback list.
     * After the method returns the handler is guaranteed to not be invoked by this tap until it is registered again.
     * While a handler may be registered multiple times with {@link Tap#tap(Handler)}, a single call to untap removes all registered instances of the given handler.
     * @param handler The handler to unregister, this must be the value returned by {@link Tap#tap(Handler)} for the original handler.
     */
    void unTap(Handler handler);
}
