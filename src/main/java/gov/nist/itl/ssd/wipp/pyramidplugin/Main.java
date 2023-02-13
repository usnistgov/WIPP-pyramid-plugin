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

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PatternOptionBuilder;

/**
 * @author Mylene Simon <mylene.simon at nist.gov>
 *
 */
public class Main {
	
	private static final Logger LOG = Logger.getLogger(
            Main.class.getName());

	public static void main(String[] args) {
		
		Options options = new Options();
		   
		Option helpOption = new Option("h", "help", false,
           "Display this help message and exit.");
		options.addOption(helpOption);
		
		Option inputOption = new Option("i", "inputImages", true,
                "Input images folder - images to convert to dzi.");
        inputOption.setRequired(true);
        options.addOption(inputOption);
        
//        Option inputSvOption = new Option("v", "inputStitchingVectors", true,
//                "Input stitching vectors folder.");
//        inputSvOption.setRequired(true);
//        options.addOption(inputSvOption);

        Option outputOption = new Option("o", "output", true,
                "Output folder or file where the output will be generated.");
        outputOption.setRequired(true);
        options.addOption(outputOption);
        
        Option tileSizeOption = new Option("ts", "tileSize", true,
                "Tile size (default 1024).");
        tileSizeOption.setType(PatternOptionBuilder.NUMBER_VALUE);
        options.addOption(tileSizeOption);

//        Option blendingOption = new Option("b", "blending", true,
//                "Blending method when assembling tiles, options are max or overlay.");
//        blendingOption.setRequired(true);
//        options.addOption(blendingOption);
        
//        Option formatOption = new Option("f", "format", true,
//                "Format, options are deepzoom or tiff (default deepzoom).");
//        options.addOption(formatOption);

		Option minThresholdScalerOption = new Option("mins", "minThresholdScaler", true,
				"The minimum threshold used for scaling images, " +
						"if min/max are the same, then scaling is disabled [default: 0]");
		minThresholdScalerOption.setType(PatternOptionBuilder.NUMBER_VALUE);
		options.addOption(minThresholdScalerOption);

		Option maxThresholdScalerOption = new Option("maxs", "maxThresholdScaler", true,
				"The maximum threshold used for scaling images, " +
						"if min/max are the same, then scaling is disabled [default: 0]");
		maxThresholdScalerOption.setType(PatternOptionBuilder.NUMBER_VALUE);
		options.addOption(maxThresholdScalerOption);
		
		CommandLineParser parser = new DefaultParser();
	       try {
	           CommandLine commandLine = parser.parse(options, args);

	           if (commandLine.hasOption(helpOption.getOpt())) {
	               printHelp(options);
	               return;
	           }
	           
	           File inputImages = new File(
	                    commandLine.getOptionValue(inputOption.getOpt()));
	           
//	           File inputStitchingVector = new File(
//	                    commandLine.getOptionValue(inputSvOption.getOpt()));
	
	           File outputFolder = new File(
	        		   commandLine.getOptionValue(outputOption.getOpt()));

	            Number tileSizeNumber = (Number) commandLine.getParsedOptionValue(
	                    tileSizeOption.getOpt());
	            int tileSize = tileSizeNumber == null
	                    ? 1024 : tileSizeNumber.intValue();
	            
//	            String blendingValue = commandLine.getOptionValue(
//	                    blendingOption.getOpt());
//	            String blending = blendingValue == null
//	            		? "overlay" : blendingValue;
	            
//	            String formatValue = commandLine.getOptionValue(
//	            		formatOption.getOpt());
//	            String format = formatValue == null
//	            		? "deepzoom" : formatValue;

			   Number minThresholdScalerNumber = (Number) commandLine.getParsedOptionValue(
					   minThresholdScalerOption.getOpt());
			   int minThresholdScaler = minThresholdScalerNumber == null
					   ? 0 : minThresholdScalerNumber.intValue();

			   Number maxThresholdScalerNumber = (Number) commandLine.getParsedOptionValue(
					   maxThresholdScalerOption.getOpt());
			   int maxThresholdScaler = maxThresholdScalerNumber == null
					   ? 0 : maxThresholdScalerNumber.intValue();
	            
	            try {
	                long start = System.currentTimeMillis();

	                PyramidBuilding pb = new PyramidBuilding(
						inputImages, 
						null,
						outputFolder, 
						null,
						null,
						tileSize,
						minThresholdScaler,
						maxThresholdScaler);
	                pb.run();
	                
	                float duration = (System.currentTimeMillis() - start) / 1000F;
	                LOG.info("Pyramids built in " + duration + "s.");
	            } catch (Exception ex) {
	            	String errorMessage = "Error while building the pyramid.";
	                LOG.severe(errorMessage);
	                System.exit(-1);
	                return;
	            }
	            

	       } catch (ParseException ex) {
	    	   LOG.severe("Error while parsing arguments: " + ex.getMessage());
	           printHelp(options);
	           System.exit(1);
	           return;
	       }

	}
	
	/**
     * Print help
     * @param options
     */
    private static void printHelp(Options options) {
        new HelpFormatter().printHelp("wipp-pyramid-building",
				options);
    }

}
