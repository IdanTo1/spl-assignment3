package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StompEncDec implements MessageEncoderDecoder<Frame> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private static final char delimiter = '\n';
    private static final int COMMAND_PART = 0;
    private static final int HEADERS_PART = 1;
    private static final int BODY_PART = 2;

    /**
     * Converts a String to the Frame object representing it
     *
     * @param msg The message as a string
     * @return The Frame representation
     */
    private Frame parseMessage(String msg) {
        int delim = 0, prevDelim = 0;
        int msgPart = COMMAND_PART;
        int bodyIndex = msg.indexOf("\n\n");
        Frame parsed = null;
        do {
            prevDelim = delim;
            delim = msg.indexOf(delimiter, prevDelim + 1); // Find the end of the next line
            if (msgPart == COMMAND_PART) { // The command
                parsed = new Frame(msg.substring(0, delim));
                msgPart++;
            } else if (msgPart == HEADERS_PART) { // headers
                int mid = msg.indexOf(":", prevDelim);
                if (mid == -1 || mid > bodyIndex) { // No more headers
                    msgPart++;
                } else parsed.addHeader(msg.substring(prevDelim + 1, mid), msg.substring(mid + 1, delim));
            }

        } while (delim < msg.length() && msgPart < BODY_PART);
        parsed.addBody(msg.substring(prevDelim + 2));
        return parsed;
    }

    @Override
    public Frame decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == '\0') {
            return parseMessage(popString());
        }

        pushByte(nextByte);
        return null; //not a frame yet
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
