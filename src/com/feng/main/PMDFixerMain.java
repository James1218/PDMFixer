/* 
 * (C) Copyright Feng Yang 2017 to present. All rights reserved.
 */
package com.feng.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.feng.rules.RuleFixerInterface;

/**
 * Class Description - This is the main class to start the PMDFixer.
 *
 *
 * @author Feng Yang
 * @version 1.0 Sep 7, 2017
 */
public class PMDFixerMain {

	/**
	 * Description - This is the main method for PMDFixer. It will read the 
	 * ruleconfig.xml file for applicable rules and apply the fix on the 
	 * source code mentioned in the cofig.properties file.
	 *
	 *
	 *
	 * @author Feng Yang
	 * @param args
	 *            No arguments are required for this main method.
	 */
	public static void main(String[] args) {

		Properties prop = new Properties();
		InputStream input = null;
		String targetWSArr[];

		try {
			// Input stream to read the properties file
			input = PMDFixerMain.class.getClassLoader().getResourceAsStream("resources\\config.properties");
			prop.load(input);

			// Get the target workspace property String (Comma Separted)
			String targetWSProp = prop.getProperty("TARGET_WORKSPACE");

			// Split the string with comma to get the each workspace path in an
			// string array
			targetWSArr = targetWSProp.split(",");

			// Get the rule fix class names from the xml configuration
			List<String> ruleClassNames = readRuleConfig();

			// Get the list of Classes from the class names
			List<RuleFixerInterface> ruleFixerObjects = getRuleFixerClasses(ruleClassNames);

			// Iterate through the array to process each workspace/project
			// directory
			for (String eachTargetWSPath : targetWSArr) {
				
				//Iterate through the list of rules
				for (RuleFixerInterface ruleFixerObj : ruleFixerObjects) {
					
					ruleFixerObj.fixRule(eachTargetWSPath);
					
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Description - This method will read the rule config xml and get the list of 
	 * rule class names of rule fixer classes.
	 *
	 *
	 *
	 * @author User
	 * @return	The list of string fully qualified class name of the rule classes
	 */
	private static List<String> readRuleConfig() {
		
		List<String> ruleClassNames = new ArrayList<String>();
		try {
			// Read the rule configuration file using Input Stream
			InputStream is = new FileInputStream("src\\resources\\ruleconfig.xml");

			// Create a new file to hold the data read by InputFileStream and
			// convert to the File object.
			// This is because Document builder's parse method needs a file
			// object only.
			File ruleConfigXMLFile = new File("tempxml");
			FileUtils.copyInputStreamToFile(is, ruleConfigXMLFile);

			// Create the document builder object using document builder factory
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			// Parse the xml file into XML document object
			Document doc = dBuilder.parse(ruleConfigXMLFile);
			doc.getDocumentElement().normalize();

			// Find all the "rule" from the document object and create node list
			NodeList nList = doc.getElementsByTagName("rule");

			// Traverse through the Node list to get the each rule and its
			// implementing class name and create a list of the class names
			for (int counter = 0; counter < nList.getLength(); counter++) {

				Node nNode = nList.item(counter);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println("Rule id : " + eElement.getAttribute("id"));
					String ruleClassName = eElement.getElementsByTagName("impl-class").item(0).getTextContent();
					System.out.println("Class Name : " + ruleClassName);
					ruleClassNames.add(ruleClassName);
				}
			}
			
			//Clean up. Can be moved to finally block.
			is.close();
			ruleConfigXMLFile.delete();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return ruleClassNames;
	}

	/**
	 * 
	 * Description -This method will create the object of the rule class from
	 * the string class name using reflection and return the lis of rule objects
	 *
	 *
	 *
	 * @author User
	 * @param ruleClassNames	The list of string class names of the rule fixer classes
	 * @return		The list of objects of the rule fixer classes.
	 */
	private static List<RuleFixerInterface> getRuleFixerClasses(List<String> ruleClassNames) {
		List<RuleFixerInterface> ruleFixerClasses = new ArrayList<RuleFixerInterface>();

		for (String ruleClassName : ruleClassNames) {
			try {
				Class ruleClass = Class.forName(ruleClassName);
				RuleFixerInterface ruleClassObject = (RuleFixerInterface) ruleClass.newInstance();
				ruleFixerClasses.add(ruleClassObject);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		return ruleFixerClasses;

	}
}
