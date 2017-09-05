package org.netarch;

import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.Link;
import org.onosproject.net.device.DeviceListener;

public abstract class NetworkEvent {
    public enum NetworkEventType {
        LINK_DOWN,
        LINK_UP,
        NODE_DOWN,
        NODE_UP,
        MIGRATION
    }


    NetworkEventType type;

    public NetworkEventType getType() {
        return type;
    }


    /**
     *
     * @param link
     * @return
     */
    public static NetworkEvent createLinkUpEvent(Link link) {
        return new LinkUpEvent(link);
    }


    /**
     *
     * @param link
     * @return
     */
    public static NetworkEvent createLinkDownEvent(Link link) {
        return new LinkDownEvent(link);
    }

    /**
     *
     * @param device
     * @return
     */
    public static NetworkEvent createNodeDownEvent(Device device) {
        return new NodeDownEvent(device);
    }


    /**
     *
     * @param device
     * @return
     */
    public static NetworkEvent createNodeUpEvent(Device device) {
        return new NodeUpEvent(device);
    }


    /**
     *
     * @param point1
     * @param point2
     * @return
     */
    public static NetworkEvent createMigrationEvent(ConnectPoint point1, ConnectPoint point2) {
        return new MigrationEvent(point1, point2);
    }
}


class LinkUpEvent extends NetworkEvent {
    private Link link;

    LinkUpEvent(Link link) {
        this.type = NetworkEventType.LINK_UP;
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}


class LinkDownEvent extends NetworkEvent {
    private Link link;
    LinkDownEvent(Link link) {
        this.type = NetworkEventType.LINK_DOWN;
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}

class NodeUpEvent extends NetworkEvent {
    private Device device;
    NodeUpEvent(Device device) {
        this.type = NetworkEventType.NODE_UP;
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }
}

class NodeDownEvent extends NetworkEvent {
    private Device deivce;
    NodeDownEvent(Device device) {
        this.type = NetworkEventType.NODE_DOWN;
        this.deivce = device;
    }

    public Device getDeivce() {
        return deivce;
    }
}

class MigrationEvent extends NetworkEvent {
    ConnectPoint newPoint;
    ConnectPoint oldPoint;
    MigrationEvent(ConnectPoint newPoint, ConnectPoint oldPoint) {
        this.type = NetworkEventType.MIGRATION;
        this.newPoint = newPoint;
        this.oldPoint = oldPoint;
    }

    public ConnectPoint getOldPoint() {
        return oldPoint;
    }

    public ConnectPoint getNewPoint() {
        return newPoint;
    }
}