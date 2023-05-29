package net.bukkitlabs.bukkitlabscloudapi.socket.server;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketCannotBeProcessedException;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketHandler;
import net.bukkitlabs.bukkitlabscloudapi.socket.event.ClientConnectEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class PacketCommunicationServer {

    private ServerSocket serverSocket;
    private PacketHandler packetHandler;
    private boolean running = true;
    private final List<SocketClientHandler> clients = new LinkedList<>();

    public InetAddress initialize(final int port, final @NotNull PacketHandler packetHandler) throws IOException {
        this.packetHandler = packetHandler;
        this.serverSocket = new ServerSocket(port);
        return this.serverSocket.getInetAddress();
    }

    public void start() throws IOException {
        while (running) {
            final Socket socket = serverSocket.accept();
            final SocketClientHandler client = new SocketClientHandler(socket, this);
            clients.add(client);
            client.start();
            try {
                this.packetHandler.call(new ClientConnectEvent(socket.getInetAddress()));
            } catch (PacketCannotBeProcessedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        this.running = false;
        for (SocketClientHandler client : this.clients) client.setRunning(false);
        this.serverSocket.close();
    }

    public void broadcast(@NotNull Packet packet) throws IOException {
        for (SocketClientHandler client : this.clients) client.sendPacket(packet);
    }

    @NotNull
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @NotNull
    public List<SocketClientHandler> getClients() {
        return clients;
    }
}
