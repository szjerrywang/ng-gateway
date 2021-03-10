package top.didasoft.core.ssl.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import top.didasoft.core.ssl.client.config.ClientConfigProperties;
import top.didasoft.core.ssl.client.handler.DiscardClientHandler;

public class DiscardClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final ClientConfigProperties clientConfigProperties;

    public DiscardClientInitializer(SslContext sslCtx, ClientConfigProperties clientConfigProperties) {
        this.sslCtx = sslCtx;
        this.clientConfigProperties = clientConfigProperties;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            // Add SSL handler first to encrypt and decrypt everything.
            // In this example, we use a bogus certificate in the server side
            // and accept any invalid certificates in the client side.
            // You will need something more complicated to identify both
            // and server in the real world.
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), clientConfigProperties.getHostName(), clientConfigProperties.getPort()));
        }
        // On top of the SSL handler, add the text line codec.
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        // and then business logic.
        pipeline.addLast(new DiscardClientHandler());
    }
}