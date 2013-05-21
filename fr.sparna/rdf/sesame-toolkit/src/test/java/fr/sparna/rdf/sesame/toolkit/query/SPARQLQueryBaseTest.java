package fr.sparna.rdf.sesame.toolkit.query;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
public class SPARQLQueryBaseTest {

	@Test
	public void testToString() throws Exception {
		
		// TEST AVEC UNE URI
		SPARQLQuery q1 = new SPARQLQuery(
				"SELECT ?uri WHERE { ?uri a ?type }",
				new HashMap<String, Object>() {{
					put("type", URI.create("http://www.test.com"));
				}}	
		);
		System.out.println(q1.toString());
		Assert.assertTrue(q1.toString().equals("SELECT ?uri WHERE { ?uri a <http://www.test.com> }"));
		
		// TEST AVEC UN LITERAL
		SPARQLQuery q2 = new SPARQLQuery(
				"SELECT ?uri WHERE { ?uri a $type }",
				new HashMap<String, Object>() {{
					put("type", "toto");
				}}	
		);
		System.out.println(q2.toString());
		Assert.assertTrue(q2.toString().equals("SELECT ?uri WHERE { ?uri a \"toto\" }"));
		
		final Repository r = new LocalMemoryRepositoryFactory().createNewRepository();
		
		// TEST AVEC UNE URI OpenRDF
		SPARQLQuery q3 = new SPARQLQuery(
				"SELECT ?uri WHERE { ?uri a $type }",
				new HashMap<String, Object>() {{
					put("type", r.getValueFactory().createURI("http://www.test.com"));
				}}	
		);
		System.out.println(q3.toString());
		Assert.assertTrue(q3.toString().equals("SELECT ?uri WHERE { ?uri a <http://www.test.com> }"));
		
		// TEST AVEC UNE LANGUE
		SPARQLQuery q4 = new SPARQLQuery(
				"SELECT ?uri WHERE { ?uri a $type }",
				new HashMap<String, Object>() {{
					put("type", r.getValueFactory().createLiteral("toto", "fr"));
				}}	
		);
		System.out.println(q4.toString());
		Assert.assertTrue(q4.toString().equals("SELECT ?uri WHERE { ?uri a \"toto\"@fr }"));
		
		// TEST AVEC UN DATATYPE
		SPARQLQuery q5 = new SPARQLQuery(
				"SELECT ?uri WHERE { ?uri a $type }",
				new HashMap<String, Object>() {{
					put("type", r.getValueFactory().createLiteral("toto", r.getValueFactory().createURI("http://www.mydatatype.com")));
				}}	
		);
		System.out.println(q5.toString());
		Assert.assertTrue(q5.toString().equals("SELECT ?uri WHERE { ?uri a \"toto\"^^<http://www.mydatatype.com> }"));
	}

}