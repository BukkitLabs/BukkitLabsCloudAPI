package net.bukkitlabs.bukkitlabscloudapi.socket.server;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketCannotBeProcessedException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

public class SocketClientHandler extends Thread {

    private final Socket socket;
    private final PacketCommunicationServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running = true;

    public SocketClientHandler(@NotNull Socket socket, @NotNull PacketCommunicationServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            Object inputLine;
            while ((inputLine = in.readObject()) != null && running) {
                if (inputLine instanceof String string && string.equals("stop")) break;
                if (inputLine instanceof Packet packet) server.getPacketHandler().call(packet);
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException | ClassNotFoundException | PacketCannotBeProcessedException exception) {
            exception.printStackTrace();
        }
    }

    public void sendPacket(@NotNull Packet packet) throws IOException {
        out.writeObject(packet);
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }
}
