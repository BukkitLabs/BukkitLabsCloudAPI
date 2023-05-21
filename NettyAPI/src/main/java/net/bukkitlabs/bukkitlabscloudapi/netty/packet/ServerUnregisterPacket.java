package net.bukkitlabs.bukkitlabscloudapi.netty.packet;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Cancelable;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public class ServerUnregisterPacket extends Packet implements Cancelable {

    private boolean canceled;

    private final String name;
    private final InetSocketAddress address;

    public ServerUnregisterPacket(@NotNull final String name, @NotNull final InetSocketAddress address) {
        this.name = name;
        this.address = address;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }
}
