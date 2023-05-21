package net.bukkitlabs.bukkitlabscloudapi.socket.client;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketCannotBeProcessedException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketServerHandler extends Thread {

    private final Socket socket;
    private final PacketCommunicationClient client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running = true;

    public SocketServerHandler(@NotNull Socket socket, @NotNull PacketCommunicationClient server) {
        this.socket = socket;
        this.client = server;
    }

    @Override
    public void run() {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            Object inputLine;
            while ((inputLine = in.readObject()) != null && running) {
                if (inputLine instanceof String string && string.equals("stop")) break;
                if (inputLine instanceof Packet packet) client.getPacketHandler().call(packet);
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
