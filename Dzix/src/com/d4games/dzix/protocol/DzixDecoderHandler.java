package com.d4games.dzix.protocol;

import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.exception.BadRequestException;
import com.strategicgains.restexpress.pipeline.MessageContext;
import com.strategicgains.restexpress.util.HttpSpecification;
import com.strategicgains.restexpress.util.StringUtils;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import static com.strategicgains.restexpress.ContentType.TEXT_PLAIN;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Dzix 패킷 암호화를 해제하기 위한 핸들러
 */
public class DzixDecoderHandler extends SimpleChannelUpstreamHandler {

    private static final Logger log = LoggerFactory.getLogger(DzixDecoderHandler.class);

    protected String userAgent = "none";
    protected String decodeType = "none";
    protected List<String> dzixNoneCryptoIPList;

    public DzixDecoderHandler(String userAgent, String decodeType, List<String> dzixNoneCryptoIPList) {
        this.userAgent = userAgent;
        this.decodeType = decodeType;
        this.dzixNoneCryptoIPList = dzixNoneCryptoIPList;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        DefaultHttpRequest request = (DefaultHttpRequest) e.getMessage();

        if(dzixNoneCryptoIPList != null && dzixNoneCryptoIPList.isEmpty() == false) {
            InetSocketAddress remoteSocketAddress = (InetSocketAddress)e.getRemoteAddress();
            InetAddress remoteAddress = remoteSocketAddress.getAddress();
            String remoteAddressString = remoteAddress.getHostAddress();
            for(String dzixNoneCryptoIP : dzixNoneCryptoIPList) {
                if(dzixNoneCryptoIP.compareTo(remoteAddressString) == 0) {
                    ctx.sendUpstream(e);
                    return;
                }
            }
        }

        switch(decodeType) {
            case "XOR" : {

                byte[] content = request.getContent().array();

                for (int i = 0; i < content.length; i++) {
                    content[i] = (byte) (content[i] ^ 0xD4);
                }

                //request.setContent(new BigEndianHeapChannelBuffer(content));
                request.getContent().setBytes(0, content);

            } break;
            case "RSA" : {

            } break;
            case "XORTest" : {
                byte[] content = request.getContent().array();

                byte testByte;
                for (int i = 0; i < content.length; i++) {
                    testByte = (byte) (content[i] ^ 0xD4);
                }
            } break;
        }

        ctx.sendUpstream(e);
    }

    /*@Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
        e.getCause().printStackTrace();
    }*/

}
