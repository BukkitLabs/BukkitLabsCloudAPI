package net.bukkitlabs.bukkitlabscloudapi.internal.packet;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import org.jetbrains.annotations.Nullable;

public class LoggerConfigurationLoadEvent extends Packet {

    private final String timeFormat;
    private final String dateFormat;

    public LoggerConfigurationLoadEvent(@Nullable final String timeFormat, @Nullable final String dateFormat) {
        this.timeFormat = timeFormat;
        this.dateFormat = dateFormat;
    }

    @Nullable
    public String getTimeFormat() {
        return timeFormat;
    }

    @Nullable
    public String getDateFormat() {
        return dateFormat;
    }
}
