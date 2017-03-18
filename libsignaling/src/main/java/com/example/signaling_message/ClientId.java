package com.example.signaling_message;

import java.io.Serializable;

/**
 * Created by zebul on 3/10/17.
 */

public class ClientId implements Serializable {

    private String id;
    public ClientId(String id){

        this.id = id;
    }

    @Override
    public String toString() {
        return "ClientId{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientId that = (ClientId) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
