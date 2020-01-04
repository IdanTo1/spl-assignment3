package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StompEncDec implements MessageEncoderDecoder<Frame> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    private Frame parseMessage(String msg) {
        int delim = 0, prevDelim = 0;
        int msgPart = 1;
        Frame parsed = null;
        do {
            prevDelim = delim;
            delim = msg.indexOf("\n", prevDelim);
            if (msgPart == 1) {
                parsed = new Frame(msg.substring(0, delim));
            }
            if (msgPart == 2) {
                int mid = msg.indexOf(":", prevDelim);
                parsed.addHeader(msg.substring(prevDelim + 1, mid), msg.substring(mid + 1, delim));
            }
            if (msgPart == 3) {
                // -2 so we don't include the \n or the \0
                parsed.addBody(msg.substring(prevDelim + 1, msg.length() - 2));
                return parsed;
            }
            msgPart++;

        } while (delim < msg.length());
        return null; // Impossible
    }

    @Override
    public Frame decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == '\0') {
            return parseMessage(popString());
        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Frame message) {
        return (message.toString() + "\0").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
