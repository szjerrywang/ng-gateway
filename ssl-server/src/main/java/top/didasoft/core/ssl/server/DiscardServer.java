package top.didasoft.core.ssl.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * Discards any incoming data.
 */
public class DiscardServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DiscardServer.class);

    private int port;
    private boolean ssl;

    private ChannelFuture f;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public DiscardServer(int port, boolean ssl) {
        this.port = port;
        this.ssl = ssl;
    }
    
    public void run()  {
        SelfSignedCertificate ssc = null;
        SslContext sslCtx = null;
        if (ssl) {
            try {
                ssc = new SelfSignedCertificate();
            } catch (CertificateException e) {
                log.error("Certificate exception", e);
                return;
            }

            try {
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                        .build();
            } catch (SSLException e) {
                log.error("Certificate exception", e);
                return;
            }
        }
        bossGroup = new NioEventLoopGroup(); // (1)
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new DiscardServerInitializer(sslCtx))
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