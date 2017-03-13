package com.example.message_pipe.outgoing;

import com.example.message.Message;
import com.example.message.MessagePipe;
import com.example.message.TransmissionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by zebul on 3/11/17.
 */

public class OutgoingMessageCompressingPipe implements MessagePipe {

    @Override
    public Message transmit(Message message_) throws TransmissionException {

        try {
            byte [] data = (byte[]) message_.getData();
            return new Message(message_.getAddress(), compress(data));
        } catch (IOException exc) {
            throw new TransmissionException(exc);
        }
    }

    private byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return data;
        }

        ByteArrayInputStream sourceStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream targetStream = new ByteArrayOutputStream(data.length);
        DeflaterOutputStream compressor = new DeflaterOutputStream(targetStream);
        compressor.write(data);
        compressor.close();
        targetStream.close();
        return targetStream.toByteArray();
    }
}
