package com.semaifour.facesix.jni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.jni.bean.Coordinate;
import com.semaifour.facesix.jni.bean.GeoPoint;
import com.semaifour.facesix.jni.bean.Pixel;

/**
 * Handles call to jni lib to process image to geotiff.
 */
public class GeoServiceJniHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GeoServiceJniHandler.class);
 
    public native String doGeoReference(String inputfile, String outputFile, GeoPoint geoPoints[], int count, double rotaionangle);
    
    public native String pixelToCoordinate(String inputfile, Pixel Pixels[], int count, double rotaionangle);
    
    public native String coordinateToPixel(String inputfile, Coordinate Coordinates[], int count, double rotaionangle );
    
}
