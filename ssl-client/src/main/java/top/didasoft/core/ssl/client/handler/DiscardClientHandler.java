package top.didasoft.core.ssl.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Handles a client-side channel.
 */
public class DiscardClientHandler extends SimpleChannelInboundHandler<String> { // (1)

    private static final Logger log = LoggerFactory.getLogger(DiscardClientHandler.class);

    private AtomicLong numberOfMsg = new AtomicLong(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;

            log.info("remote connected: {}:{}", inetSocketAddress.getAddress().toString(), inetSocketAddress.getPort());
        }

        SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
        if (sslHandler != null)
        {
            log.info("SSL handler is not null");
            sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
                @Override
                public void operationComplete(Future<? super Channel> future) throws Exception {
                    log.info("Write through SSL channel");
                    ctx.writeAndFlush(
                            "Your session is protected by " +
                                    ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                                    " cipher suite.\n");
                }
            });
        }
//        ByteBuf byteBuf = ctx.alloc().buffer();
//        ByteBufUtil.writeUtf8(byteBuf, "Hello server\n");
//        ctx.channel().writeAndFlush(byteBuf);
        //ctx.channel().flush();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;

            log.info("remote disconnected: {}:{}", inetSocketAddress.getAddress().toString(), inetSocketAddress.getPort());
        }
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        try {
//            String hexDump = ByteBufUtil.prettyHexDump((ByteBuf) msg);
//            log.info("Received message {} ", numberOfMsg.incrementAndGet());
//            log.info("\n{}", hexDump);
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("Received message {} ", msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}