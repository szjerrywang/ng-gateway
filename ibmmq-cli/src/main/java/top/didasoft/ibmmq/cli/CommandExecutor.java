package top.didasoft.ibmmq.cli;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.model.ParserMetadata;
import com.github.rvesse.airline.parser.errors.ParseException;

/**
 * Helper class that launches and runs the actual example commands and CLIs
 * 
 * @author rvesse
 *
 */
public class CommandExecutor {

    private static <T extends CommandRunnable> void execute(T cmd) {
        try {
            int exitCode = cmd.run();
            System.out.println();
            System.out.println("Exiting with Code " + exitCode);
            System.exit(exitCode);
        } catch (Throwable e) {
            System.err.println("Command threw error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static <T extends CommandRunnable> void executeSingleCommand(Class<T> cls, String[] args) {
        SingleCommand<T> parser = SingleCommand.singleCommand(cls);
        try {
            T cmd = parser.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("Parser error: " + e.getMessage());
        } catch (Throwable e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
    
    public static <T extends CommandRunnable> void executeSingleCommand(Class<T> cls, ParserMetadata<T> parserConfig, String[] args) {
        SingleCommand<T> parser = SingleCommand.singleCommand(cls, parserConfig);
        try {
            T cmd = parser.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("Parser error: " + e.getMessage());
        } catch (Throwable e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static <T extends CommandRunnable> void executeCli(Cli<T> cli, String[] args) {
        try {
            T cmd = cli.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("Parser error: " + e.getMessage());
        } catch (Throwable e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static void busyWaitMicros(long micros){
        long waitUntil = System.nanoTime() + (micros * 1_000);
        while(waitUntil > System.nanoTime()){
            ;
        }
    }
}
