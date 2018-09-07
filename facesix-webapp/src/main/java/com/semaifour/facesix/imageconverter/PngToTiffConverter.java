package com.semaifour.facesix.imageconverter;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PngToTiffConverter extends AbstractTiffConverter {
    private static final Logger logger = Logger.getLogger(PngToTiffConverter.class.getName());

    @Override
    public void convert(InputStream inputFilePath, String outputFilePath) throws Exception {
        logger.info("Beginning conversion PNG to TIFF");
        logger.log(Level.INFO, "Input file is {0}", inputFilePath);
        logger.log(Level.INFO, "Output file is {0}", outputFilePath);
        super.convert(inputFilePath, outputFilePath);
        logger.info("Ending conversion PNG to TIFF");
    }
	
}
