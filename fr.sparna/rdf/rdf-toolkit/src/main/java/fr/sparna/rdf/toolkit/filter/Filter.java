package fr.sparna.rdf.toolkit.filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.FilteringRDFHandler;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Filter implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsFilter args = (ArgumentsFilter)o;

		// TODO configure logging

		// create filter
		FilteringRDFHandler filter = new FilteringRDFHandler(null, args.getIncludes(), args.getExcludes());
		
		// create output dir if needed
		if(!args.getOutput().exists()) {
			args.getOutput().mkdirs();
		}
		
		// process input		
		for (File anInput : args.getInput()) {
			processInput(anInput, filter, args.getOutput());
		}
	}
	
	protected void processInput(File inputFile, FilteringRDFHandler filter, File outputDir) 
	throws RepositoryException, RepositoryFactoryException, RDFHandlerException, IOException {
		log.warn("Filtering "+inputFile.getAbsolutePath()+"...");
		if(!inputFile.exists()) {
			log.warn("Input file "+inputFile.getAbsolutePath()+" does not exist.");
			return;
		}
		
		if(inputFile.isFile()) {
			// load RDF
			Repository inputRepository = RepositoryBuilder.fromString(inputFile.getAbsolutePath());
			
			try {
				
				// create output file
				File outputFile = new File(outputDir, inputFile.getName());
				if(!outputFile.exists()) {
					outputFile.createNewFile();
				}
				
				// picks up the correct RDF format based on target file extension (.rdf, .n3, .ttl, etc...)
				RDFHandler writer = RDFWriterRegistry.getInstance().get(RDFFormat.forFileName(inputFile.getName())).getWriter(new FileOutputStream(outputFile));
				filter.setHandler(writer);
				
				RepositoryConnection c = null;
				try {
					c = inputRepository.getConnection();
					c.export(filter);
				} finally {
					c.close();
				}
				
				// shutdown
				inputRepository.shutDown();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		} else {
			// input is a directory, iterate recursively on files
			for (File aFile : inputFile.listFiles()) {
				processInput(aFile, filter, outputDir);
			}
		}
	}

}