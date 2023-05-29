package net.bukkitlabs.bukkitlabscloudapi.socket.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;

public record BukkitLabsPlayer(
        @NotNull UUID uuid,
        @NotNull String name,
        @NotNull String ip,
        @Nullable String server
) implements Serializable {
}
