package com.rescue.hc.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class PadLocation implements Serializable {
    private static final long serialVersionUID = -6743567631108323097L;
    private Double padLatitude;
    private Double padLongtitude;
}