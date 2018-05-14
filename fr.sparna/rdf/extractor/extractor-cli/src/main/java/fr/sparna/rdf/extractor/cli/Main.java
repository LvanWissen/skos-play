package fr.sparna.rdf.extractor.cli;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;

import fr.sparna.rdf.extractor.cli.crawl.ArgumentsCrawl;
import fr.sparna.rdf.extractor.cli.crawl.Crawl;


public class Main {

	enum COMMAND {		
		
		CRAWL(new ArgumentsCrawl(), new Crawl())
		;
		
		private ExtractorCliCommandIfc command;
		private Object arguments;

		private COMMAND(Object arguments, ExtractorCliCommandIfc command) {
			this.command = command;
			this.arguments = arguments;
		}

		public ExtractorCliCommandIfc getCommand() {
			return command;
		}

		public Object getArguments() {
			return arguments;
		}		
	}
	
	
	private void run(String[] args) throws Exception {
		ArgumentsMain main = new ArgumentsMain();
		JCommander jc = new JCommander(main);
		
		for (COMMAND aCOMMAND : COMMAND.values()) {
			jc.addCommand(aCOMMAND.name().toLowerCase(), aCOMMAND.getArguments());
		}
		
		try {
			jc.parse(args);
		// a mettre avant ParameterException car c'est une sous-exception
		} catch (MissingCommandException e) {
			// if no command was found, exit with usage message and error code
			System.err.println("Unkwown command.");
			jc.usage();
			System.exit(-1);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			jc.usage(jc.getParsedCommand());
			System.exit(-1);
		} 
		
		// if help was requested, print it and exit with a normal code
		if(main.isHelp()) {
			jc.usage();
			System.exit(0);
		}
		
		// if no command was found (0 parameters passed in command line)
		// exit with usage message and error code
		if(jc.getParsedCommand() == null) {
			System.err.println("No command found.");
			jc.usage();
			System.exit(-1);
		}
		
		// configure logging using log4j
		if(main.getLog() != null) {
			if(main.getLog().getName().endsWith(".xml")) {
				DOMConfigurator.configure(main.getLog().getAbsolutePath());
			} else {
				PropertyConfigurator.configure(main.getLog().getAbsolutePath());
			}
		} else {
			DOMConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.xml"));
		}
		
		// executes the command with the associated arguments
		COMMAND.valueOf(jc.getParsedCommand().toUpperCase()).getCommand().execute(
				COMMAND.valueOf(jc.getParsedCommand().toUpperCase()).getArguments()
		);
	}
	
	public static void main(String[] args) throws Exception {
		Main me = new Main();
		me.run(args);
	}

}
