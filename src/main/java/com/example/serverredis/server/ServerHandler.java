package com.example.serverredis.server;

import com.example.serverredis.model.Command;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

//1. 数据类型支持 String Int List
//2. 命令支持set setnx get incr decr exists del shutdown(退出服务器) stat(返回服务器信息）
//3. shutdown时将内存里面的数据保存到磁盘
//4. 应用启动时读取配置文件，将磁盘上的数据载入到内存中，磁盘没有数据就初始化
//5. 定时重建内存，移除已经删除的数据。定时配置保存到配置文件中

@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProcessMessage(ctx,(ByteBuf) msg);
    }

    private void ProcessMessage(ChannelHandlerContext ctx,ByteBuf msg) {
        CommandServer commandServer = MyBeanUtil.getBean(CommandServer.class);
        Object result;
        //获取客户端发送过来的消息
        String message = msg.toString(CharsetUtil.UTF_8);

        System.out.println("收到客户端的消息："+message);

        String[] strings = message.split(" ");
        String name = strings.length > 0 ? strings[0] : null;
        String key = strings.length > 1 ? strings[1] : null;
        String value = strings.length > 2 ? strings[2] : null;
        Object newValue = null;
        if(value != null){
            try{
                newValue = Integer.parseInt(value);
            }catch (Exception e){
                newValue = value;
            }
        }
        Command command = new Command(name,key,newValue);
        result = commandServer.executeCommand(command);

        ctx.writeAndFlush(Unpooled.copiedBuffer(result.toString(), CharsetUtil.UTF_8));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常，关闭通道
        ctx.close();
    }


}
