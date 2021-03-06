package top.didasoft.core.ssl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.didasoft.core.ssl.client.config.ClientConfigProperties;
import top.didasoft.core.ssl.client.handler.DiscardClientHandler;

import javax.net.ssl.SSLException;

public class DiscardClient implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DiscardClient.class);

    private String host;
    private int port;
    private long timeoutms;
    private boolean ssl;
    private ClientConfigProperties clientConfigProperties;
    private EventLoopGroup workerGroup;
    ChannelFuture f;

    public DiscardClient(String host, int port, long timeoutms, boolean ssl, ClientConfigProperties clientConfigProperties) {
        this.host = host;
        this.port = port;
        this.timeoutms = timeoutms;
        this.ssl = ssl;
        this.clientConfigProperties = clientConfigProperties;
    }

    @Override
    public void run() {
        SslContext sslCtx = null;
        if (ssl) {
            try {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                log.error("Ssl context exception", e);
                return;
            }
        }
        workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new DiscardClientInitializer(sslCtx, clientConfigProperties));
//            b.handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                public void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new DiscardClientHandler());
//                }
//            });

            // Start the client.
            f = b.connect(host, port);
            if (f.await(timeoutms)) {
                // Wait until the connection is closed.
                f.channel().closeFuture().sync();
            }
            else
            {
                shutdown();
            }
                   // .sync(); // (5)


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        log.info("Stopping client");
        try
        {

            if (workerGroup != null) {
                workerGroup.shutdownGracefully().sync();
            }
            if (f != null) {
                f.channel().closeFuture().sync();
            }
            log.info("client stopped");
        }
        catch (InterruptedException e)
        {
            log.error("Exception occured: {}", e);
        }
    }
}