package com.example.zebul.cameraservice.ice4j;

/**
 * Created by zebul on 3/14/17.
 */

public interface SDPProducer {

    String produceSDP() throws SDPProductionException;
}
