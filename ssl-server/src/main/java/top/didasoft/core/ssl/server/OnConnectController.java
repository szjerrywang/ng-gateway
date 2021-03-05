package top.didasoft.core.ssl.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.kgusarov.integration.spring.netty.annotations.NettyController;
import org.kgusarov.integration.spring.netty.annotations.NettyOnConnect;

import java.util.Random;

@NettyController
public class OnConnectController {   
    @NettyOnConnect(serverName = "server1", priority = 1)
    private void onConnect1() {
        System.out.println("Hello, world!");
    }

    @NettyOnConnect(serverName = "server1", priority = 2)
    ByteBuf onConnect2(final ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copyLong(new Random().nextLong()));
        return Unpooled.copyLong(new Random().nextLong());
    }
}