package net.bukkitlabs.bukkitlabscloudapi.socket.event;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class PacketSendEvent extends Packet {

    private final InetAddress inetAddress;

    public PacketSendEvent(@NotNull InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    @NotNull
    public InetAddress getInetAddress() {
        return inetAddress;
    }
}
