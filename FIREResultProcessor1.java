import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FIREResultProcessor1 {

	/*
	 * Please modify the following two constants before running the program. In
	 * case of any problems please contact Danish Contractor
	 * (danish.contractor.007@gmail.com)
	 * 
	 * (c) Source code copyright FIRE 2011. All rights reserved.
	 */

	private static String INPUT_DIR = "C:/Users/Abhinav/Documents/run";// Location
																									// of
																									// test (run results)
																									// files
																									// as
																									// well
																									// as
																									// the
																									// result
																									// xmls (matches files).
	private static String OUTPUT_DIR = "C:/Internet/";// Result Summary to be
														// generated in this
														// folder.

	private static void readResultDirectory(File dir) throws IOException,
			ParserConfigurationException, SAXException {

		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				// indexDirectory(writer, f); // recurse
				continue;
			} // else if (f.getName().endsWith(".txt")) {
			else {
				if (!f.getName().endsWith(".xml"))
					validateSMSResultFile(f.getAbsolutePath());
			}
		}
	}

	public static void validateSMSResultFile(String smsResultFile)
			throws ParserConfigurationException, SAXException, IOException {
		String file;
		String errorRun = "";
		file = smsResultFile.substring(smsResultFile.lastIndexOf("\\"));
		System.out.println(file);
		file = smsResultFile.substring(0, smsResultFile.lastIndexOf("\\"))
				+ "/"
				+ file.substring(file.indexOf("$") + 1, file.lastIndexOf("$"))
				+ ".xml";
		System.out.println("Using:" + file);
		DocumentBuilderFactory dbfac = null;
		Document doc = null;
		Element root = null;

		dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		}
		doc = docBuilder.newDocument();
		root = doc.createElement("QUERIES");
		doc.appendChild(root);

		Document doc2 = docBuilder.parse(new File(file));

		doc2.getDocumentElement().normalize();
		System.out.println("Root element of the doc is "
				+ doc2.getDocumentElement().getNodeName());

		NodeList listOfSMS = doc2.getElementsByTagName("SMS");
		int totalSMS = listOfSMS.getLength();
		System.out.println("Total no of SMSes : " + totalSMS);

		HashSet<String> ids = new HashSet<String>();
		HashMap<String, HashSet<String>> idToMatchesMap = new HashMap<String, HashSet<String>>();
		int inDomainTotal = 0;
		int outDomainTotal = 0;

		for (int s = 0; s < listOfSMS.getLength(); s++) {

			Node sms = listOfSMS.item(s);
			if (sms.getNodeType() == Node.ELEMENT_NODE) {
				Element SMSelement = (Element) sms;
				NodeList idList = SMSelement
						.getElementsByTagName("SMS_QUERY_ID");
				Element idElement = (Element) idList.item(0);

				NodeList textId = idElement.getChildNodes();

				String queryId = ((Node) textId.item(0)).getNodeValue().trim();
				// System.out.println(queryId);
				if (ids.contains(queryId)) {
					System.out.println(queryId + " is a duplicate");
				} else
					ids.add(queryId);

				// NodeList textList =
				// SMSelement.getElementsByTagName("SMS_TEXT");
				// Element textElement = (Element) textList.item(0);

				// NodeList text = textElement.getChildNodes();
				// String smsText = ((Node) text.item(0)).getNodeValue().trim();

				NodeList matches = SMSelement.getElementsByTagName("MATCHES");
				Element matchesElement = (Element) matches.item(0);

				NodeList engList = matchesElement
						.getElementsByTagName("ENGLISH");
				Element engElement = (Element) engList.item(0);

				NodeList hinList = matchesElement.getElementsByTagName("HINDI");
				Element hinElement = (Element) hinList.item(0);

				NodeList malList = matchesElement
						.getElementsByTagName("MALAYALAM");
				Element malElement = (Element) malList.item(0);

				String engMatchVal = engElement.getChildNodes().item(0)
						.getNodeValue().trim();
				String hinMatchVal = hinElement.getChildNodes().item(0)
						.getNodeValue().trim();
				String malMatchVal = malElement.getChildNodes().item(0)
						.getNodeValue().trim();

				HashSet<String> matchSet = new HashSet<String>();
				matchSet.add(engMatchVal);
				matchSet.add(hinMatchVal);
				matchSet.add(malMatchVal);

				if (matchSet.size() == 1)
					outDomainTotal++;
				else
					inDomainTotal++;

				idToMatchesMap.put(queryId, matchSet);
			}

		}
		BufferedReader b1 = new BufferedReader(new InputStreamReader(
				new FileInputStream(smsResultFile), "UTF8"));
		System.out.println("Processing:" + smsResultFile);

		String res = "";
		int inDomainCorrect = 0;
		int outOfDomainCorrect = 0;
		int inDomainIncorrect = 0;
		int outOfDomainIncorrect = 0;
		float MRR = 0;
		int smsCount = 0;

		while ((res = b1.readLine()) != null) {
			try {

				// System.out.println(res);

				String[] splitRes = res.split(",");
				if (splitRes[0].trim().length() <= 1)
					continue;
				// smsCount++;
				HashSet<String> matches = idToMatchesMap
						.get(splitRes[0].trim());
				if (matches != null) {

					if (!splitRes[1].trim().equalsIgnoreCase("NULL")) {
						if (matches.size() > 1)
							smsCount++;
						// smsCount++;
						if (matches.contains(splitRes[1].trim())
								&& !splitRes[1].trim().equalsIgnoreCase("none")) {
							// System.out.println(splitRes[1]);
							inDomainCorrect++;
							// smsCount++;
						} else {
							if (matches.size() > 1)
								inDomainIncorrect++;
							else
								outOfDomainIncorrect++;
						}
						for (int i = 1; i < splitRes.length; i += 2) {
							if (matches.contains(splitRes[i].trim())
									&& !splitRes[1].trim().equalsIgnoreCase(
											"none")) {
								MRR += ((float) 1 / i);
								// smsCount++;
								break;
							}
						}

					} else {
						if (matches.contains("NONE") && matches.size() == 1)
							// if (matches.size() == 1)
							outOfDomainCorrect++;
						else
							// outOfDomainIncorrect++;
							inDomainIncorrect++;
					}
				} else {
					if (splitRes[1].trim().equalsIgnoreCase("NULL"))
						outOfDomainCorrect++;
					else
						outOfDomainIncorrect++;
				}
			} catch (Exception e) {
				errorRun += "\n" + res;
				e.printStackTrace();
			}
		}

		MRR = MRR / smsCount;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(FIREResultProcessor1.OUTPUT_DIR
						+ (new File(smsResultFile)).getName() + "$result"),
				"UTF-8"));
		System.out.println("In Domain Total:" + inDomainTotal);
		System.out.println("Out of Domain Total:" + outDomainTotal);

		writer.write("***** FIRE 2011 SMS TASK EVALUATION REPORT *****\n");
		writer.write("\n");
		// writer.write("Task:"+)
		writer.write("No. of In-domain Queries :" + inDomainTotal + "\n");
		writer.write("No. of Out of Domain Queries:" + outDomainTotal + "\n");
		writer.write("\n");
		writer.write("In Domain correct:" + inDomainCorrect + "/"
				+ inDomainTotal + " ("
				+ ((float) inDomainCorrect / inDomainTotal) + ")\n");
		writer.write("Out of Domain correct:" + outOfDomainCorrect + "/"
				+ outDomainTotal + " ("
				+ ((float) outOfDomainCorrect / outDomainTotal) + ")\n");

		writer.write("\n");

		writer.write("Total Score: "
				+ ((float) (inDomainCorrect + outOfDomainCorrect) / (inDomainTotal + outDomainTotal)));
		writer.write("\n\n");

		writer.write("Mean Reciprocal Rank (MRR): " + MRR + " \n");
		writer.write("\n");
		writer.write("ERRORS:" + errorRun);
		writer.close();
		b1.close();

	}

	public static void main(String s[]) throws ParserConfigurationException,
			SAXException, IOException {
		try {
			readResultDirectory(new File(FIREResultProcessor1.INPUT_DIR));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
