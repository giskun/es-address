package com.ruixin.esaddress.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;

/**
 * @author zfk
 * @date 2019/7/29
 * @description
 */
@Document(indexName = "address-index", type = "address",indexStoreType = "fs",shards = 5,replicas = 0,refreshInterval = "-1")
@Setting(settingPath = "/json/address-setting.json")
@Mapping(mappingPath = "/json/address-mapping.json")
public class Address implements Serializable {


    private static final long serialVersionUID = -8712769733956791064L;
    @Id
    private Long id;

    private String name;

    private String type;

    private double lon;

    private double lat;

    private Geo geoShape = new Geo();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void initGeoShape(){
        this.geoShape.setType("POINT");
        this.geoShape.setCoordinates(new double[]{this.lon,this.lat});
    }

    public Geo getGeoShape() {
        return geoShape;
    }

    public void setGeoShape(Geo geoShape) {
        this.geoShape = geoShape;
    }
}
