package net.bukkitlabs.bukkitlabscloudapi.socket.client;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class PacketCommunicationClient {

    private Socket socket;
    private PacketHandler packetHandler;
    private SocketServerHandler handler;

    public InetAddress initialize(@NotNull String ip, final int port, final @NotNull PacketHandler packetHandler) throws IOException {
        this.packetHandler = packetHandler;
        this.socket = new Socket(ip, port);
        return socket.getInetAddress();
    }

    public void start() {
        handler = new SocketServerHandler(this.socket, this);
        handler.start();
    }

    public void stop() {
        if (handler != null) handler.setRunning(false);
    }

    public void sendPacket(@NotNull Packet packet) throws IOException {
        if (handler != null) handler.sendPacket(packet);
    }

    public void stopServer() throws IOException {
        if (handler != null) handler.stopServer();
    }

    @NotNull
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }
}
