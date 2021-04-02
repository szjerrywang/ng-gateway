package top.didasoft.ibmmq.cli;

public interface CommandRunnable {
    /**
     * Runs the command and returns an exit code that the application should
     * return
     *
     * @return Exit code
     */
    public int run();
}
