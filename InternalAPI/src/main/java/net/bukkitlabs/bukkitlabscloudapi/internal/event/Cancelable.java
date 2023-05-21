package net.bukkitlabs.bukkitlabscloudapi.internal.event;

public interface Cancelable {

    boolean isCanceled();

    void setCanceled(boolean canceled);
}
