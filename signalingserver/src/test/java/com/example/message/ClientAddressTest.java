package com.example.message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 3/10/17.
 */

public class ClientAddressTest {

    @Test
    public void test_when_two_instances_of_ClientAddress_have_same_ctor_argument_then_instances_are_equal(){

        //given
        String ctorArgument = "foo foo";

        //when
        ClientAddress clientAddress1 = new ClientAddress(ctorArgument);
        ClientAddress clientAddress2 = new ClientAddress(ctorArgument);

        //then
        assertEquals(clientAddress1, clientAddress2);
    }
}
