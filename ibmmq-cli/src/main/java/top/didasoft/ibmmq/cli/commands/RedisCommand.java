package top.didasoft.ibmmq.cli.commands;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.didasoft.ibmmq.cli.CommandRunnable;

@Command(name = "redis", description = "A command that connect redis to put some key")
public class RedisCommand implements CommandRunnable {

    private static final Logger log = LoggerFactory.getLogger(RedisCommand.class);

    @Option(name = { "-v", "--verbose" }, description = "Enables verbose mode")
    protected boolean verbose = false;

    @Option(name = { "--uri" }, title = "Uri", arity = 1, description = "Redis connection uri")
    @Required
    protected String uri;

    @Override
    public int run() {

        RedisClusterClient redisClient = null;
        StatefulRedisClusterConnection<String, String> connection = null;

        try {
            redisClient = RedisClusterClient.create(uri);

            connection = redisClient.connect();
            RedisAdvancedClusterCommands<String, String> syncCommands = connection.sync();

            syncCommands.set("key", "Hello, Redis!");
        }
        finally {
            if (connection != null) {
                connection.close();
            }
            if (redisClient != null) {
                redisClient.shutdown();
            }
        }



        return 0;
    }
}
