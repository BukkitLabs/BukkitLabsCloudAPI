package net.bukkitlabs.bukkitlabscloudapi.internal.console;

import net.bukkitlabs.bukkitlabscloudapi.internal.event.Listener;
import net.bukkitlabs.bukkitlabscloudapi.internal.event.PacketCatch;
import net.bukkitlabs.bukkitlabscloudapi.internal.packet.CommandExecuteEvent;
import net.bukkitlabs.bukkitlabscloudapi.internal.packet.LoggerConfigurationLoadEvent;
import net.bukkitlabs.bukkitlabscloudapi.internal.packet.UnknownCommandExecuteEvent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class Logger implements Listener {

    private final String logFolder = "logs";
    private final String logFileExtension = ".log";
    private final String logFileCompressedExtension = ".log.gz";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private PrintWriter writer;
    private String currentLogFileName;
    private int currentLogId;

    public Logger() {
        createLogFolder();
        createLogFile();
        compressLogFiles();
    }

    @PacketCatch
    private void onConfigurationLoad(final LoggerConfigurationLoadEvent event) {
        try {
            if (event.getTimeFormat() != null) timeFormat = new SimpleDateFormat(event.getTimeFormat());
            if (event.getDateFormat() != null) dateFormat = new SimpleDateFormat(event.getDateFormat());
        } catch (IllegalArgumentException | NullPointerException exception) {
            log(Level.ERROR, "Invalid time/date format in configuration" + exception);
        }
    }

    @PacketCatch
    private void onCommandExecute(final CommandExecuteEvent event){
        final StringBuilder stringBuilder = new StringBuilder();
        if(!event.isCanceled()){
            stringBuilder.append("Command executed: ")
                    .append(event.getCommand().getLabel());
            for (String arg : event.getArgs()){
                stringBuilder.append(" ")
                        .append(arg);
            }
            softLog(Level.INFO, stringBuilder.toString());
        }else {
            stringBuilder.append("Command canceled: ")
                    .append(event.getCommand().getLabel());
            for (String arg : event.getArgs()){
                stringBuilder.append(" ")
                        .append(arg);
            }
            softLog(Level.WARN, stringBuilder.toString());
        }
    }

    @PacketCatch
    private void onUnknownCommandExecute(final UnknownCommandExecuteEvent event){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown Command executed: ")
                .append(event.getCommandLabel());
        for (String arg : event.getArgs()){
            stringBuilder.append(" ")
                    .append(arg);
        }
        softLog(Level.INFO, stringBuilder.toString());
    }

    @NotNull
    private String getDate() {
        final Date now = new Date();
        return dateFormat.format(now);
    }

    @NotNull
    private String getTime() {
        final Date now = new Date();
        return timeFormat.format(now);
    }

    private void writeLog(@NotNull final String logMessage) {
        writer.println(logMessage);
    }

    private void printLog(@NotNull final String consoleMessage) {
        System.out.println(consoleMessage);
    }

    public void close() {
        writer.close();
    }

    private void createLogFolder() {
        final File file = Paths.get(this.logFolder).toFile();
        if (!file.exists()) file.mkdirs();
    }

    private void createLogFile() {
        currentLogFileName = getDate() + "-" + generateLogId() + logFileExtension;
        final File file = Paths.get(this.logFolder, currentLogFileName).toFile();
        try {
            file.createNewFile();
            final FileWriter fileWriter = new FileWriter(file, true);
            writer = new PrintWriter(fileWriter, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int generateLogId() {
        currentLogId=1;
        final File[] allLogFiles = new File(logFolder).listFiles((dir, name) -> name.contains(getDate()));

        final List<File> matchingFiles = new ArrayList<>();
        if (allLogFiles != null) {
            for (File file : allLogFiles) {
                if (file.isFile()) {
                    matchingFiles.add(file);
                }
            }
        }
        final File[] currentLogFiles = matchingFiles.toArray(new File[0]);

        if (currentLogFiles.length > 0) {
            Arrays.sort(currentLogFiles);
            final String lastLogFileName = currentLogFiles[currentLogFiles.length - 1].getName();
            final String[] parts = lastLogFileName.split("-");
            if (parts.length > 0){
                currentLogId=Integer.parseInt(parts[parts.length-1].replace(logFileCompressedExtension,"").replace(logFileExtension,""))+1;
            }else {
                log(Level.WARN,"Invalid naming of a log file. Log ID was set to 1.");
            }
        }
        return currentLogId;
    }

    public void log(@NotNull final Level level, @NotNull final String message) {
        printLog(generateConsoleMessage(level, message));
        writeLog(generateLogMessage(level, message));
    }

    public void softLog(@NotNull final Level level, @NotNull final String message) {
        writeLog(generateLogMessage(level, message));
    }

    public void log(@NotNull final Level level, @NotNull final String message, @NotNull final Exception exception) {
        printLog(generateConsoleMessage(level, message, exception));
        writeLog(generateLogMessage(level, message, exception));
    }

    @NotNull
    private String generateConsoleMessage(@NotNull final Level level, @NotNull final String message) {
        final StringBuilder stringBuilder = new StringBuilder()
                .append(level.getConsoleColor().getColorCode())
                .append("[")
                .append(getTime())
                .append("]")
                .append(" ")
                .append(level.getPrefix())
                .append(" ")
                .append(message)
                .append(ConsoleColor.RESET.getColorCode());

        return stringBuilder.toString();
    }

    @NotNull
    private String generateConsoleMessage(@NotNull final Level level, @NotNull final String message, @NotNull final Exception exception) {
        final StringBuilder stringBuilder = new StringBuilder()
                .append(level.getConsoleColor().getColorCode())
                .append("[")
                .append(getTime())
                .append("]")
                .append(" ")
                .append(level.getPrefix())
                .append(" ")
                .append(message)
                .append(System.lineSeparator())
                .append(exception.toString())
                .append(System.lineSeparator());
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString()).append(System.lineSeparator());
        }
        stringBuilder.append(ConsoleColor.RESET.getColorCode());
        return stringBuilder.toString();
    }

    @NotNull
    private String generateLogMessage(@NotNull final Level level, @NotNull final String message) {
        final StringBuilder stringBuilder = new StringBuilder()
                .append("[")
                .append(getTime())
                .append("]")
                .append(" ")
                .append(level.getPrefix())
                .append(" ")
                .append(message);

        return stringBuilder.toString();
    }

    @NotNull
    private String generateLogMessage(@NotNull final Level level, @NotNull final String message, @NotNull final Exception exception) {
        final StringBuilder stringBuilder = new StringBuilder()
                .append("[")
                .append(getTime())
                .append("]")
                .append(" ")
                .append(level.getPrefix())
                .append(" ")
                .append(message)
                .append(System.lineSeparator())
                .append(exception.toString())
                .append(System.lineSeparator());
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString()).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private void compressLogFiles() {
        final File[] allLogFiles = new File(logFolder).listFiles((dir,name) -> (!name.equals(currentLogFileName)&&name.endsWith(logFileExtension)));
        final List<File> matchingFiles = new ArrayList<>();
        if (allLogFiles != null) {
            for (File file : allLogFiles) {
                if (file.isFile()) matchingFiles.add(file);
            }
        }
        final File[] uncompressedLogFiles = matchingFiles.toArray(new File[0]);
        for (File file : uncompressedLogFiles){
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileOutputStream fileOutputStream = new FileOutputStream(file.getPath().replace(logFileExtension,logFileCompressedExtension));
                 GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    gzipOutputStream.write(buffer, 0, bytesRead);
                }
                log(Level.FINE,"Log file "+file.getName()+" was compressed successfully.");
                if (!file.delete()) {
                    log(Level.WARN,"Uncompressed Log file "+file.getName()+" could not be deleted. Delete file on Exit...");
                    file.deleteOnExit();
                }
            } catch (IOException e) {
                log(Level.ERROR,"Log file "+file.getName()+" could not be compressed."+e);
            }
        }
    }

    public enum Level {
        INFO("[INFO]", ConsoleColor.WHITE),
        FINE("[FINE]", ConsoleColor.GREEN),
        ERROR("[ERROR]", ConsoleColor.RED),
        WARN("[WARN]", ConsoleColor.YELLOW);

        private final String prefix;
        private final ConsoleColor consoleColor;

        Level(@NotNull final String prefix, @NotNull final ConsoleColor consoleColor) {
            this.prefix = prefix;
            this.consoleColor = consoleColor;
        }

        @NotNull
        public String getPrefix() {
            return prefix;
        }

        @NotNull
        public ConsoleColor getConsoleColor() {
            return consoleColor;
        }
    }

}
