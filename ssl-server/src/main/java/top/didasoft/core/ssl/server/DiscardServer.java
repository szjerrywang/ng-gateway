package top.didasoft.core.ssl.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.didasoft.core.ssl.server.handler.DiscardServerHandler;

/**
 * Discards any incoming data.
 */
public class DiscardServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DiscardServer.class);

    private int port;

    private ChannelFuture f;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public DiscardServer(int port) {
        this.port = port;
    }
    
    public void run()  {
        bossGroup = new NioEventLoopGroup(); // (1)
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new DiscardServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
    
            // Bind and start to accept incoming connections.
            log.info("Starting server");
            f = b.bind(port).sync(); // (7)
    
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            log.error("Exception occured: {}", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        log.info("Stopping server");
        try
        {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully().sync();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully().sync();
            }
            if (f != null) {
                f.channel().closeFuture().sync();
            }
            log.info("Server stopped");
        }
        catch (InterruptedException e)
        {
            log.error("Exception occured: {}", e);
        }
    }

}