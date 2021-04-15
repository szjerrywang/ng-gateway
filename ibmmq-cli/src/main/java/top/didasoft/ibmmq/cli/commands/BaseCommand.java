package top.didasoft.ibmmq.cli.commands;

import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import top.didasoft.ibmmq.cli.CommandRunnable;

public abstract class BaseCommand implements CommandRunnable {

    @Option(name = { "-v", "--verbose" }, description = "Enables verbose mode")
    protected boolean verbose = false;


    @Option(name = { "--host" }, title = "Host", arity = 1, description = "MQ queue manager host name")
    @Required
    protected String host;

    @Option(name = { "--channel" }, title = "Channel", arity = 1, description = "MQ queue manager channel name")
    @Required
    protected String channel;

    @Option(name = { "--userId" }, title = "UserId", arity = 1, description = "MQ queue manager user id")
    protected String userId;

    @Option(name = { "--password" }, title = "Password", arity = 1, description = "MQ queue manager user password")
    protected String password;

    @Option(name = { "--queueManager" }, title = "QueueManager", arity = 1, description = "MQ queue manager name")
    @Required
    protected String qMgrName;

    @Option(name = { "--appName" }, title = "AppName", arity = 1, description = "Application Name")
    protected String appName = "IBMMQCli";

    @Option(name = { "--port" }, title = "Port", arity = 1, description = "MQ queue manager listener port")
    protected int port = 1414;

    @SuppressWarnings("unchecked")
    protected MQQueueManager createMQQueueManager() throws MQException {
        MQEnvironment.hostname = host;
        MQEnvironment.port = port;
        MQEnvironment.channel = channel;
        MQEnvironment.userID = userId;
        MQEnvironment.password = password;
        MQEnvironment.properties.put(MQConstants.APPNAME_PROPERTY, appName);

        return new MQQueueManager(qMgrName);

    }

}
