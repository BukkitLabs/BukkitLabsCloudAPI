package net.bukkitlabs.bukkitlabscloudapi.internal.console;

import org.jetbrains.annotations.NotNull;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;

public class CompleterHandler implements Completer {

    private final CommandHandler commandHandler;

    public CompleterHandler(@NotNull CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void complete(final LineReader lineReader, final ParsedLine parsedLine, final List<Candidate> list) {
        final List<String> buffer = parsedLine.words();

        if (buffer.size() == 1) {
            final StringsCompleter delegate = new StringsCompleter(commandHandler.getAllRegisteredCommands()
                                                                           .stream()
                                                                           .map(Command::getLabel)
                                                                           .toArray(String[]::new));
            delegate.complete(lineReader, parsedLine, list);
            return;
        }

        if (buffer.isEmpty()) {
            commandHandler.getAllRegisteredCommands()
                    .stream()
                    .map(Command::getLabel)
                    .forEach(command -> list.add(new Candidate(command)));
            return;
        }

        final Command command = commandHandler.getAllRegisteredCommands()
                .stream()
                .filter(iterator -> iterator.getLabel().equalsIgnoreCase(buffer.get(0)))
                .findFirst()
                .orElse(null);

        if (command == null) return;

        final StringsCompleter delegate = new StringsCompleter(command.getCloudCommand().onTab(
                command,
                buffer.subList(1, buffer.size()).toArray(String[]::new)
        ));
        delegate.complete(lineReader, parsedLine, list);
    }
}