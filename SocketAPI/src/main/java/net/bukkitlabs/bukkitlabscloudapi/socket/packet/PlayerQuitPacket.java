package net.bukkitlabs.bukkitlabscloudapi.socket.packet;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import net.bukkitlabs.bukkitlabscloudapi.socket.model.BukkitLabsPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitPacket extends Packet {

    private final BukkitLabsPlayer player;

    public PlayerQuitPacket(@NotNull final BukkitLabsPlayer player) {
        this.player = player;
    }

    @NotNull
    public BukkitLabsPlayer getPlayer() {
        return player;
    }
}
