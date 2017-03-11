package com.example.message;

import java.io.Serializable;

/**
 * Created by zebul on 3/10/17.
 */

public class ClientAddress implements Serializable {

    private String address;
    public ClientAddress(String address){

        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientAddress that = (ClientAddress) o;

        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
