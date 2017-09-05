package org.netarch;

public interface NetworkEventListener {
    /**
     * Process the network event.
     * @param event network event
     */
    void process(NetworkEvent event);

    /**
     * Decide whether the listener will process events.
     * @param type Network event type.
     * @return
     */
    boolean typeFilter(NetworkEvent.NetworkEventType type);
}
