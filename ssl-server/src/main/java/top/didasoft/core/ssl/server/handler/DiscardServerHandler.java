package top.didasoft.core.ssl.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<String> { // (1)

    private static final Logger log = LoggerFactory.getLogger(DiscardServerHandler.class);

    private AtomicLong numberOfMsg = new AtomicLong(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;

            log.info("remote connected: {}:{}", inetSocketAddress.getAddress().toString(), inetSocketAddress.getPort());
        }
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