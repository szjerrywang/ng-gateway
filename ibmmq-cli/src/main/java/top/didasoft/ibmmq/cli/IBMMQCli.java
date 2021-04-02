package top.didasoft.ibmmq.cli;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.help.CommandGroupUsageGenerator;
import com.github.rvesse.airline.help.cli.CliCommandGroupUsageGenerator;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import top.didasoft.ibmmq.cli.commands.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;


@Cli(name = "ibmmqcli",
        description = "Provides a IBM MQ CLI",
        defaultCommand = Help.class,
        commands = {QueueInfoCommand.class, QueuesCommand.class, BashCompletion.class, Manuals.class, Help.class })
public class IBMMQCli {
//    public static void main(String[] args) {
//        com.github.rvesse.airline.Cli<Runnable> cli = new com.github.rvesse.airline.Cli<>(IBMMQCli.class);
//        Runnable cmd = cli.parse(args);
//        cmd.run();
//    }

    static
    {
        System.setProperty("PID", getPid());
    }

    private static final Logger log = LoggerFactory.getLogger(IBMMQCli.class);
    private static String getPid() {
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        }
        catch (Throwable ex) {
            return null;
        }
    }

    public static void main(String[] args) {


        log.info("Starting logging...");

        com.github.rvesse.airline.Cli<CommandRunnable> cli = new com.github.rvesse.airline.Cli<CommandRunnable>(IBMMQCli.class);

        CommandExecutor.executeCli(cli, args);
    }

    public static void generateHelp() throws IOException {
        com.github.rvesse.airline.Cli<CommandRunnable> cli = new com.github.rvesse.airline.Cli<CommandRunnable>(IBMMQCli.class);

        CommandGroupUsageGenerator<CommandRunnable> helpGenerator = new CliCommandGroupUsageGenerator<>();
        try {
            helpGenerator.usage(cli.getMetadata(), new CommandGroupMetadata[] { cli.getMetadata().getCommandGroups().get(0) }, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
