package com.example.signaling_message;

import com.example.utils.GenericSerializer;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by zebul on 3/10/17.
 */

public class KeepAliveRequestTest {

    @Test
    public void test_KeepAliveRequest_serializability() throws IOException, ClassNotFoundException {

        ClientId clientAddress = new ClientId("foo");
        KeepAliveRequest inputKeepAliveRequest = new KeepAliveRequest(clientAddress);
        final byte[] data = GenericSerializer.serialize(inputKeepAliveRequest);
        KeepAliveRequest outputKeepAliveRequest = GenericSerializer.deserialize(data, KeepAliveRequest.class);

    }
}
