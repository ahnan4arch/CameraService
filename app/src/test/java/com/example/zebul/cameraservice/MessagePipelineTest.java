package com.example.zebul.cameraservice;

import com.example.message.Message;
import com.example.message.MessagePipe;
import com.example.message.MessagePipeline;
import com.example.message.MessagePipelineEndpoint;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 9/18/16.
 */
public class MessagePipelineTest {

    class DecoratingMessagePipe implements MessagePipe {

        private String decoratingText;

        public DecoratingMessagePipe(String decoratingText_){

            decoratingText = decoratingText_;
        }

        @Override
        public Message transmit(Message message_) {
            String data = (String)message_.getData();
            data += decoratingText;
            message_.setData(data);
            return message_;
        }
    }

    class CachingMessagePipeEndpoint implements MessagePipelineEndpoint {

        private Message message;
        @Override
        public void onTransmittedMessage(Message message_) {

            message = message_;
        }

        public Message getMessage(){

            return message;
        }
    }

    @Test
    public void when_message_is_sent_via_decorating_pipes_then_at_the_end_message_is_decorated() throws Exception {

        MessagePipeline messagePipeline = new MessagePipeline();
        messagePipeline.addMessagePipe(new DecoratingMessagePipe("a"));
        messagePipeline.addMessagePipe(new DecoratingMessagePipe("b"));
        messagePipeline.addMessagePipe(new DecoratingMessagePipe("c"));

        CachingMessagePipeEndpoint cachingMessagePipeEndpoint = new CachingMessagePipeEndpoint();
        messagePipeline.setMessageEndpoint(cachingMessagePipeEndpoint);

        Message textMessage = new Message("", "123");
        messagePipeline.transmit(textMessage);
        Message message = cachingMessagePipeEndpoint.getMessage();
        Assert.assertNotNull(message);
        String data = (String)message.getData();
        Assert.assertEquals("123abc", data);
    }
}
