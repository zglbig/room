package org.bql.net.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.bql.net.message.ServerRequest;
import org.bql.net.server.session.ISession;
import org.bql.net.server.session.SessionImpl;
import org.bql.utils.logger.LoggerUtils;

public class ServerSimpleHandler extends SimpleChannelInboundHandler<ServerRequest> {
	private ISession session = null;
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		session = new SessionImpl(ctx.channel());
		TcpHandler.channelActive(ctx.channel());
		//客户端在

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		TcpHandler.channelInactive(ctx.channel());

//		outLine(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		TcpHandler.exceptionCaught(ctx.channel(),cause);
//		outLine(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ServerRequest request) throws Exception {
		//将消息发送到消息分发
		TcpHandler.massegeRansiter(session,request);
//		handlerMessage(ctx.channel(),request);
	}
//	/**消息分发器*/
//	private void handlerMessage(Channel channel, ServerRequest request){
//
//	}

//	private void outLine(Channel channel){
//		ISession session = new SessionImpl(channel);
//		Msg m = new Msg("10");
//		TcpHandler.messageRecieve(session,new Request((short)5,m));
//	}

	/**
	 * 心跳机制
	 * @param ctx
	 * @param evt
	 * @throws Exception
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent e = (IdleStateEvent) evt;
//			if(e.state().equals(IdleState.READER_IDLE)){
//				System.out.println("---读空闲---");
//				ctx.channel().close();
//			}else if(e.state().equals(IdleState.WRITER_IDLE)){
//				System.out.println("---写空闲----");
//			}else
			 if(e.state().equals(IdleState.ALL_IDLE)){
				System.out.println("---读写空闲---");
//			 	outLine(ctx.channel());
//				ctx.channel().writeAndFlush("ping\r\n");
			}
		}
	}
}