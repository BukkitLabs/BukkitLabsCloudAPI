package net.bukkitlabs.bukkitlabscloudapi.socket;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Listener;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketCatch;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketHandler;
import net.bukkitlabs.bukkitlabscloudapi.socket.client.PacketCommunicationClient;
import net.bukkitlabs.bukkitlabscloudapi.socket.model.BukkitLabsPlayer;
import net.bukkitlabs.bukkitlabscloudapi.socket.packet.PlayerJoinPacket;
import net.bukkitlabs.bukkitlabscloudapi.socket.server.PacketCommunicationServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientServerTest implements Listener {

    private final CountDownLatch lock = new CountDownLatch(1);
    private PacketHandler packetHandler;
    private String testString;

    @BeforeEach
    void beforeEach() {
        testString = "FALSE";
        packetHandler = new PacketHandler();
        packetHandler.registerListener(this);
    }

    @Test
    void onServerSendSocketPackage() throws IOException, InterruptedException {
        final PacketCommunicationServer server = new PacketCommunicationServer();
        final PacketCommunicationClient client = new PacketCommunicationClient();

        System.out.println("INITIALIZED");

        server.initialize(8888, packetHandler);
        new Thread(() -> {
            try {
                server.start();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).start();

        System.out.println("SERVER STARTED");

        lock.await(500, TimeUnit.MILLISECONDS);

        client.initialize("localhost", 8888, packetHandler);
        client.start();

        System.out.println("CLIENT STARTED");

        lock.await(500, TimeUnit.MILLISECONDS);

        server.broadcast(new PlayerJoinPacket(new BukkitLabsPlayer(UUID.randomUUID(), "Server", "sd", "sd")));

        System.out.println("BROADCAST");

        lock.await(500, TimeUnit.MILLISECONDS);

        assertEquals("SERVER", testString);

        server.stop();
        lock.await(500, TimeUnit.MILLISECONDS);
    }

    @Test
    void onClientSendSocketPackage() throws IOException, InterruptedException {
        final PacketCommunicationServer server = new PacketCommunicationServer();
        final PacketCommunicationClient client = new PacketCommunicationClient();

        System.out.println("INITIALIZED");

        server.initialize(8888, packetHandler);
        new Thread(() -> {
            try {
                server.start();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).start();

        System.out.println("SERVER STARTED");

        lock.await(500, TimeUnit.MILLISECONDS);

        client.initialize("localhost", 8888, packetHandler);
        client.start();

        System.out.println("CLIENT STARTED");

        lock.await(500, TimeUnit.MILLISECONDS);

        client.sendPacket(new PlayerJoinPacket(new BukkitLabsPlayer(UUID.randomUUID(), "Client", "sd", "sd")));

        System.out.println("SEND");

        lock.await(500, TimeUnit.MILLISECONDS);

        assertEquals("CLIENT", testString);

        server.stop();
        lock.await(500, TimeUnit.MILLISECONDS);
    }

    @PacketCatch
    public void onServerRegister(@NotNull final PlayerJoinPacket packet) {
        System.out.println(packet.getPlayer().uuid());
        if (packet.getPlayer().name().equals("Server")) testString = "SERVER";
        if (packet.getPlayer().name().equals("Client")) testString = "CLIENT";
    }
}
