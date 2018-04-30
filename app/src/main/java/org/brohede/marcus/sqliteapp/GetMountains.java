package org.brohede.marcus.sqliteapp;

/**
 * Created by jacobsvensson on 2018-04-26.
 */

public class GetMountains {
    private String name;
    private int height;
    private String location;

    public GetMountains(String name, int height, String location,
                     String url, String auxdata) {
        this.name=name;
        this.height=height;
        this.location=location;
    }

    public String utmatare() {
        return name + " is part of the " + location +  " mountains range and is " +  Integer.toString(height) + "m high.";
    }

    @Override
    public String toString() {
        return name;
    }


}


