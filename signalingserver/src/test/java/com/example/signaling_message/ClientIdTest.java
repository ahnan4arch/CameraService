package com.example.signaling_message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 3/10/17.
 */

public class ClientIdTest {

    @Test
    public void test_when_two_instances_of_ClientId_have_same_ctor_argument_then_instances_are_equal(){

        //given
        String ctorArgument = "foo foo";

        //when
        ClientId clientId1 = new ClientId(ctorArgument);
        ClientId clientId2 = new ClientId(ctorArgument);

        //then
        assertEquals(clientId1, clientId2);
    }
}
