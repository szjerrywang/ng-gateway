package top.didasoft.ibmmq.cli.commands;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.help.cli.bash.BashCompletionGenerator;
import com.github.rvesse.airline.model.GlobalMetadata;
import top.didasoft.ibmmq.cli.CommandRunnable;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;

@Command(name = "generate-completions", description = "Generates a Bash completion script, the file can then be sourced to provide completion for this CLI")
public class BashCompletion implements CommandRunnable{

    @Inject
    private GlobalMetadata<CommandRunnable> global;
    
    @Option(name = "--include-hidden", description = "When set hidden commands and options are shown in help", hidden = true)
    private boolean includeHidden = false;

    @Override
    public int run() {
        try (FileOutputStream out = new FileOutputStream(this.global.getName() + "-completions.bash")) {
            new BashCompletionGenerator<CommandRunnable>(this.includeHidden, false).usage(global, out);
            System.out.println("Generated completion script " + this.global.getName() + "-completions.bash");
        } catch (IOException e) {
            System.err.println("Error generating completion script: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return 0;
    }
}