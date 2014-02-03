import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class KgramCorrection {

	/**
	 * @param args
	 */
	static String question="";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File fXmlFile = new File("C:/Users/anjaney/Desktop/eng-mono.xml");
			System.out.print(fXmlFile.isFile());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList FAQList = doc.getElementsByTagName("SMS");

			System.out.println("----------------------------");

			for (int temp = 0; temp < FAQList.getLength(); temp++) {

				Node nNode = FAQList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					question="";
					Element eElement = (Element) nNode;
					String ques=eElement.getElementsByTagName("SMS_TEXT").item(0).getTextContent();
					System.out.println(ques);
					String[] sarr=ques.split(" ");
					for(int i=0;i<sarr.length;i++)
					{
						question=question+query(sarr[i])+" ";
					}

					System.out.println(question);
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String query(String str)
	{
		System.out.println(str+" ");
		try {
			BufferedReader buffread=new BufferedReader(new FileReader("C:\\Users\\anjaney\\Desktop\\project\\dictfromgivendata.txt"));
			String sCurrentLine;
			while ((sCurrentLine = buffread.readLine()) != null) {
				if(sCurrentLine.toLowerCase().equals(str))
				{
					return str;
				}
			}
			buffread.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		char[] cquery=str.toLowerCase().toCharArray();
		String paa="";
		if (cquery.length>0 && (cquery[0]>=97  && cquery[0]<=122))
		{
			paa+=cquery[0];
		}
		for(int i=1;i<cquery.length;i++)
		{
			if(cquery[i]>=97  && cquery[i]<=122)
			{
				if(cquery[i]=='a' || cquery[i]=='e' || cquery[i]=='i' || cquery[i]=='o' || cquery[i]=='u')
				{}
				else
				{
					paa+=cquery[i];
				}
			}
			else
			{
				//code for numbrs and spcial smbols
			}

		}

		cquery=paa.toCharArray();
		if(cquery.length>2)
		{
			ArrayList<myClass> alist=new ArrayList<myClass>();
		//	ArrayList<String> finalwords=new ArrayList<String>();
			String[] str1=new String[cquery.length-2];
			int max=0;
			String hippo="";
			for(int i=0;i<=cquery.length-3;i++)
			{
				str1[i]=""+cquery[i]+cquery[i+1]+cquery[i+2];
				try {
					String curr;

					BufferedReader br=new BufferedReader(new FileReader("C:\\Users\\anjaney\\Desktop\\project\\3gram\\"+str1[i]+"to.txt"));
					while((curr=br.readLine())!=null)
					{
						int flash=0;
						for(int in=0;in<alist.size();in++)
						{
							myClass o=alist.get(in);
							if(o.word.equals(curr))
							{
								o.freq++;
								if(o.freq>max)
								{
									max=o.freq;
									hippo=o.word;
								}
								flash=1;
								break;
							}

						}
						if(flash==0)
						{
							myClass obj=new myClass();
							obj.word=curr;
							obj.freq=1;
							alist.add(obj);
						}
					}
					//System.out.println(str1[i]+": "+alist);
					br.close();
					//System.out.print("\n");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println(ngram.get(str1[i]));5
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}


			String temp="";
			for(int i=0;i<alist.size();i++)
			{
				myClass ob=alist.get(i);
				if(ob.freq==max)
				{
					temp=ob.word;
				}
				if(temp=="")
				{

				}
				else if(temp.length()<hippo.length())
				{
					hippo=temp;
				}
			}
			return hippo;

		}

		return null;

	}

}
