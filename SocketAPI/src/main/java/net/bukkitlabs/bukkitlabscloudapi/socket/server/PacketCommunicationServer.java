package net.bukkitlabs.bukkitlabscloudapi.socket.server;

import java.io.IOException;
import java.net.ServerSocket;

public class PacketCommunicationServer {

    private final ServerSocket serverSocket;

    public PacketCommunicationServer(final int port) throws IOException {
        serverSocket = new ServerSocket(port);

    }
}
