package net.bukkitlabs.bukkitlabscloudapi.internal.console;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketCannotBeProcessedException;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketHandler;
import net.bukkitlabs.bukkitlabscloudapi.internal.packet.CommandExecuteEvent;
import net.bukkitlabs.bukkitlabscloudapi.internal.packet.UnknownCommandExecuteEvent;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.util.*;

public class CommandHandler {
    private final Map<String, Command> commands;
    private final LineReader reader;
    private final PacketHandler packetHandler;
    private final Logger logger;

    public CommandHandler(@NotNull PacketHandler packetHandler, @NotNull Logger logger) {
        this.packetHandler = packetHandler;
        this.logger = logger;
        commands = new HashMap<>();
        reader = LineReaderBuilder.builder()
                .completer(new CompleterHandler(this))
                .build();
    }

    public void registerCommand(@NotNull final Command command) {
        commands.put(command.getLabel(), command);
    }

    @NotNull
    public List<Command> getAllRegisteredCommands() {
        return new ArrayList<>(commands.values());
    }

    public void startListening() {
        while (true) {
            final String input = reader.readLine();
            if (input.equalsIgnoreCase("exit")) {
                System.exit(0);
                return;
            }
            final String[] inputParts = input.split(" ");
            final String commandLabel = inputParts[0];
            final String[] args = Arrays.copyOfRange(inputParts, 1, inputParts.length);
            handleCommand(commandLabel, args);
        }
    }

    private void handleCommand(@NotNull final String commandLabel, @NotNull final String[] args) {
        final Command command = commands.get(commandLabel);
        if (command == null) {
            try{
                packetHandler.call(new UnknownCommandExecuteEvent(commandLabel,args));
            }catch(PacketCannotBeProcessedException e){
                logger.log(Logger.Level.ERROR,"UnknownCommandExecuteEvent could not be handled.",e);
            }
            logger.log(Logger.Level.WARN, "Unknown Command (" + commandLabel + "). Type help for all Help!");
            return;
        }
        final CommandExecuteEvent commandExecuteEvent= new CommandExecuteEvent(command,args);
        try{
            packetHandler.call(commandExecuteEvent);
        }catch(PacketCannotBeProcessedException e){
            logger.log(Logger.Level.ERROR,"CommandExecuteEvent could not be handled.",e);
        }
        if(!commandExecuteEvent.isCanceled()){
            if (!command.getCloudCommand().onCommand(command, args)){
                logger.log(Logger.Level.INFO, "Usage: " + command.getUsage());
            }
        }else {
            logger.log(Logger.Level.WARN,"Command could not be executed because it was canceled by an event");
        }
    }
}
