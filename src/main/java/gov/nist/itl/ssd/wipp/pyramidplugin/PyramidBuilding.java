/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package gov.nist.itl.ssd.wipp.pyramidplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class for running C++ pyramid building over series of time frames
 * 
 * @author Mylene Simon <mylene.simon at nist.gov>
 *
 */
public class PyramidBuilding {

	// Stitching vector naming conventions
	private static final String STITCHING_VECTOR_FILENAME_PREFIX = "img-global-positions-";
	private static final String STITCHING_VECTOR_FILENAME_SUFFIX = ".txt";
	
	// Pyramid building options
	private static final String PYRAMID_BUILDING_COMMAND = "/opt/executables/commandLineCli";
	private static final String PYRAMID_BUILDING_INPUT_IMAGES_OPTION = "--images";
	private static final String PYRAMID_BUILDING_INPUT_SV_OPTION = "--vector";
	private static final String PYRAMID_BUILDING_OUTPUT_DIR = "--output";
	private static final String PYRAMID_BUILDING_TILE_SIZE_OPTION = "--tilesize";
	private static final String PYRAMID_BUILDING_NAME_OPTION = "--name";
	private static final String PYRAMID_BUILDING_DEPTH_OPTION = "--depth";
	private static final String PYRAMID_BUILDING_BLENDING_OPTION = "--blending";
	private static final String PYRAMID_BUILDING_FORMAT_OPTION = "--format";
	private static final String PYRAMID_BUILDING_EXPERT_FLAGS = "--expertmode";

		
	private final File tilesFolder;
    private final File stitchingVectorFolder;
    private final File outputFolder;
    private final String blendingOption;
    private final String depthOption;
    private final String formatOption;
    private final int tileSize;
    private final String expertFlags;

    private static final Logger LOG = Logger.getLogger(
            PyramidBuilding.class.getName());

    public PyramidBuilding(
    		File tilesFolder, 
    		File stitchingVector,
            File outputFolder, 
            String blendingOption,
            String depthOption,
            String formatOption,
            int tileSize,
            String expertFlags) {
        this.tilesFolder = tilesFolder;
        this.stitchingVectorFolder = stitchingVector;
        this.outputFolder = outputFolder;
        this.blendingOption = blendingOption;
        this.depthOption = depthOption;
        this.formatOption = formatOption;
        this.tileSize = tileSize;
        this.expertFlags = expertFlags;
    }

    public Integer run() throws Exception {
        File[] timeSlices = stitchingVectorFolder.listFiles((dir, fn)
                -> fn.startsWith(STITCHING_VECTOR_FILENAME_PREFIX)
                && fn.endsWith(STITCHING_VECTOR_FILENAME_SUFFIX));
        Arrays.sort(timeSlices);

        outputFolder.mkdirs();
	    
	    List<Integer> timeSliceInt = new ArrayList<Integer>();
	    for (File timeSlice : timeSlices) {
            int tsi = Integer.parseInt(timeSlice.getName()
                    .replace(STITCHING_VECTOR_FILENAME_PREFIX, "")
                    .replace(STITCHING_VECTOR_FILENAME_SUFFIX, ""));
            timeSliceInt.add(tsi);
	    }
	    
	    int maxTimeSlice = Collections.max(timeSliceInt);
	    int nbDigits = String.valueOf(maxTimeSlice).length();
	    
        int timeSlicesBuilt = 0;
        
        // Check for expert flags
        String pyramidBuildingCommand = PYRAMID_BUILDING_COMMAND;
        
		// Process time slices one by one
        for (File timeSlice : timeSlices) {
            int timeSliceNb = Integer.parseInt(timeSlice.getName()
                    .replace(STITCHING_VECTOR_FILENAME_PREFIX, "")
                    .replace(STITCHING_VECTOR_FILENAME_SUFFIX, ""));
            String timeSliceStr = String.format("%0" + nbDigits + "d", timeSliceNb);

            LOG.info("Building pyramid " + timeSliceStr + "...");
            
            // calling C++ pyramid building executable
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pyramidBuildingCommand,
                    PYRAMID_BUILDING_INPUT_IMAGES_OPTION, tilesFolder.getAbsolutePath(),
                    PYRAMID_BUILDING_INPUT_SV_OPTION, timeSlice.getAbsolutePath(),
                    PYRAMID_BUILDING_OUTPUT_DIR, outputFolder.getAbsolutePath(),
                    PYRAMID_BUILDING_TILE_SIZE_OPTION, String.valueOf(this.tileSize),
                    PYRAMID_BUILDING_NAME_OPTION, timeSliceStr,
                    PYRAMID_BUILDING_DEPTH_OPTION, this.depthOption,
                    PYRAMID_BUILDING_BLENDING_OPTION, this.blendingOption,
                    PYRAMID_BUILDING_FORMAT_OPTION, this.formatOption,
                    PYRAMID_BUILDING_EXPERT_FLAGS, this.expertFlags);

            processBuilder.redirectErrorStream(true);
            Process p = processBuilder.start();
    		// get output and error logs
    		BufferedReader stdOut = new BufferedReader(new InputStreamReader(
    				p.getInputStream()));
    		String s;
    		while ((s = stdOut.readLine()) != null) {
    			LOG.info(s);
    		}
    		
    		try {
    			if (p.waitFor()!= 0) {
    				LOG.severe("Pyramid building error.");
    				throw new RuntimeException("Pyramid building command failed");
    			}
    		} catch (InterruptedException | RuntimeException e) {
    			String errorMessage = "Error during pyramid building execution";
    			LOG.severe(e.getMessage());
    			LOG.severe(errorMessage);
    			throw new RuntimeException(errorMessage);
    		}

            timeSlicesBuilt++;
        }

        if (timeSlicesBuilt == 0) {
            throw new RuntimeException("No time slice found.");
        }
        
        return timeSlicesBuilt;
    }

}
