package com.adaptavant.cabapp.jdo;

/**
 * Created by user on 07/06/17.
 */

public class RouteJDO {

    String RoutName;
    String RoutID;

    public RouteJDO(String routName, String routID) {
        RoutName = routName;
        RoutID = routID;
    }

    public String getRoutName() {
        return RoutName;
    }

    public void setRoutName(String routName) {
        RoutName = routName;
    }

    public String getRoutID() {
        return RoutID;
    }

    public void setRoutID(String routID) {
        RoutID = routID;
    }
}
