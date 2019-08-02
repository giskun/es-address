package com.ruixin.esaddress.vo;

import java.io.Serializable;

/**
 * @author zfk
 * @date 2019/8/2
 * @description
 */
public class Geo implements Serializable {

    private static final long serialVersionUID = 3723495753709275885L;

    private String type;

    private double[] coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
