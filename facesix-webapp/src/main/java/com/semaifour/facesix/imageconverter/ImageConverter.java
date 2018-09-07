package com.semaifour.facesix.imageconverter;

import java.io.InputStream;

public interface ImageConverter {
	void convert(InputStream inputFilePath, String outputFilePath) throws Exception;
}
