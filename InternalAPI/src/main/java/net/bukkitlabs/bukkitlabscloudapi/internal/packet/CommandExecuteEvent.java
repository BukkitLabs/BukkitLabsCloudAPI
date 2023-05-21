package net.bukkitlabs.bukkitlabscloudapi.internal.packet;

import net.bukkitlabs.bukkitlabscloudapi.internal.console.Command;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.Cancelable;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.Packet;
import org.jetbrains.annotations.NotNull;

public class CommandExecuteEvent extends Packet implements Cancelable {

    private final Command command;
    private final String[] args;
    private boolean canceled;

    public CommandExecuteEvent(@NotNull final Command command,@NotNull final String[] args){
        this.command=command;
        this.args=args;
    }
    public Command getCommand(){
        return command;
    }

    public String[] getArgs(){
        return args;
    }

    @Override
    public boolean isCanceled(){
        return canceled;
    }

    @Override
    public void setCanceled(final boolean canceled){
        this.canceled=canceled;
    }
}
