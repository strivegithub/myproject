package com.d4games.dzix.protocol;

import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.handler.codec.http.HttpConstants.CR;
import static org.jboss.netty.handler.codec.http.HttpConstants.LF;

/**
 * Dzix 패킷 암호화를 수행하기 위한 핸들러
 */
public class DzixEncoderHandler extends SimpleChannelDownstreamHandler {

    protected String encodeType = "none";
    protected List<String> dzixNoneCryptoIPList;

    public DzixEncoderHandler(String encodeType, List<String> dzixNoneCryptoIPList) {
        this.encodeType = encodeType;
        this.dzixNoneCryptoIPList = dzixNoneCryptoIPList;
    }

    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        try {
            DefaultHttpResponse response = (DefaultHttpResponse) e.getMessage();

            if (dzixNoneCryptoIPList != null && dzixNoneCryptoIPList.isEmpty() == false) {
                InetSocketAddress remoteSocketAddress = (InetSocketAddress) e.getRemoteAddress();
                InetAddress remoteAddress = remoteSocketAddress.getAddress();
                String remoteAddressString = remoteAddress.getHostAddress();
                for (String dzixNoneCryptoIP : dzixNoneCryptoIPList) {
                    if (dzixNoneCryptoIP.compareTo(remoteAddressString) == 0) {
                        ctx.sendDownstream(e);
                        return;
                    }
                }
            }

            switch (encodeType) {
                case "XOR": {
                    byte[] content = response.getContent().array();

                    for (int i = 0; i < content.length; i++) {
                        content[i] = (byte) (content[i] ^ 0xD4);
                    }

                    response.getContent().setBytes(0, content);
                }
                break;
                case "RSA": {

                }
                break;
                case "XORTest": {
                    byte[] content = response.getContent().array();
                    byte testByte;

                    for (int i = 0; i < content.length; i++) {
                        testByte = (byte) (content[i] ^ 0xD4);
                    }
                }
                break;
            }
            ctx.sendDownstream(e);
        } catch (Exception excp) {
            ctx.sendDownstream(e);
        }
    }

}
