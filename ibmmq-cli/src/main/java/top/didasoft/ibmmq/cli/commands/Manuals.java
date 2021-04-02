package top.didasoft.ibmmq.cli.commands;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.help.man.ManGlobalUsageGenerator;
import com.github.rvesse.airline.help.man.ManSections;
import com.github.rvesse.airline.model.GlobalMetadata;
import top.didasoft.ibmmq.cli.CommandRunnable;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;

@Command(name = "generate-manuals", description = "Generates manual pages for this CLI that can be rendered with the man tool")
public class Manuals implements CommandRunnable {

    @Inject
    private GlobalMetadata<CommandRunnable> global;

    @Option(name = "--include-hidden", description = "When set hidden commands and options are shown in help", hidden = true)
    private boolean includeHidden = false;

    @Override
    public int run() {
        try (FileOutputStream output = new FileOutputStream(this.global.getName() + ".1")) {
            new ManGlobalUsageGenerator<CommandRunnable>(ManSections.GENERAL_COMMANDS).usage(this.global, output);
            System.out.println("Generated manuals to " + this.global.getName() + ".1");
        } catch (IOException e) {
            System.err.println("Error generating completion script: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return 0;
    }
}