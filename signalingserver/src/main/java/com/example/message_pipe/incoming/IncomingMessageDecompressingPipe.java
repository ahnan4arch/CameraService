package com.example.message_pipe.incoming;

import com.example.signaling_message.Message;
import com.example.signaling_message.MessagePipe;
import com.example.signaling_message.TransmissionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by zebul on 3/11/17.
 */

public class IncomingMessageDecompressingPipe implements MessagePipe {

    @Override
    public Message transmit(Message message_) throws TransmissionException {

        try {
            byte [] data = (byte[]) message_.getData();
            return new Message(message_.getAddress(), decompress(data));
        } catch (IOException exc) {
            throw new TransmissionException(exc);
        }
    }

    private byte[] decompress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return data;
        }

        ByteArrayInputStream sourceStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream targetStream =new ByteArrayOutputStream(data.length);
        InflaterInputStream decompressor = new InflaterInputStream(sourceStream);

        int b = 0;
        while ((b = decompressor.read()) != -1) {
            targetStream.write(b);
        }
        decompressor.close();
        targetStream.close();
        return targetStream.toByteArray();
    }
}
