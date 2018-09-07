package com.semaifour.facesix.imageconverter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

public abstract class AbstractTiffConverter implements ImageConverter {

    @Override
    public void convert(InputStream inputFilePath, String outputFilePath) throws Exception {
/*        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("File does not exist - " + inputFilePath);
        }*/
        //BufferedImage bufferedImage = ImageIO.read(inputFile);
        BufferedImage bufferedImage = ImageIO.read(inputFilePath);

        File outputFile = new File(outputFilePath);
        
        if(!outputFile.exists()) {
            if (!outputFile.createNewFile()) {
                throw new Exception("Cannot create output file - " + outputFilePath);
            }
        }

        ImageIO.write(bufferedImage, "TIF", outputFile);
    }
}
