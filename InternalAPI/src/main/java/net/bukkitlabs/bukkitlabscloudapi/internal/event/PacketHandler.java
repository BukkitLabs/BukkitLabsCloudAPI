package net.bukkitlabs.bukkitlabscloudapi.internal.event;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler {

    private final Map<Method, Listener> methodeList = new HashMap<>();

    public void registerListener(@NotNull final Listener listener) {
        for (final Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PacketCatch.class) ||
                    method.getParameterTypes().length != 1) continue;
            methodeList.put(method, listener);
        }
    }

    public void call(@NotNull final Packet packet) throws PacketCannotBeProcessedException {
        final Method[] methods = this.methodeList.keySet().stream()
                .filter(method -> method.getParameterTypes()[0].equals(packet.getClass()))
                .sorted((methode1, methode2) -> methode2.getAnnotation(PacketCatch.class).priority().getSlot() -
                        methode1.getAnnotation(PacketCatch.class).priority().getSlot())
                .toArray(Method[]::new);
        if (packet instanceof Cancelable cancelable) {
            this.cancelableCall(cancelable, methods);
            return;
        }
        for (final Method method : methods) {
            if (!method.isAnnotationPresent(PacketCatch.class) ||
                    method.getParameterTypes().length != 1 ||
                    !method.getParameterTypes()[0].equals(packet.getClass())) continue;
            try {
                final Listener listener = this.methodeList.get(method);
                if (!method.canAccess(listener)) method.setAccessible(true);
                method.invoke(listener, packet);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                throw new PacketCannotBeProcessedException(packet);
            }
        }
    }

    private void cancelableCall(final @NotNull Cancelable event, final @NotNull Method[] methods) throws PacketCannotBeProcessedException {
        boolean canceled = false;
        for (final Method method : methods) {
            if (!method.isAnnotationPresent(PacketCatch.class) ||
                    method.getParameterTypes().length != 1 ||
                    !method.getParameterTypes()[0].equals(event.getClass()) ||
                    (canceled && !method.getAnnotation(PacketCatch.class).ignoreCancelled())) continue;
            try {
                final Listener listener = this.methodeList.get(method);
                if (!method.canAccess(listener)) method.setAccessible(true);
                method.invoke(listener, event);
                if (event.isCanceled()) canceled = true;
            } catch (IllegalAccessException | InvocationTargetException exception) {
                throw new PacketCannotBeProcessedException((Packet) event);
            }
        }
    }
}
