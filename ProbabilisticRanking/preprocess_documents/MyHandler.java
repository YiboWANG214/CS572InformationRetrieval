
package com.journaldev.xml.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.journaldev.xml.DDocument;

public class MyHandler extends DefaultHandler {

	// List to hold Employees object
	private List<DDocument> docList = null;
	private DDocument doc = null;
	private StringBuilder data = null;

	// getter method for employee list
	public List<DDocument> getDocList() {
		return docList;
	}

	boolean bdocno = false;
	boolean bht = false;
	boolean bheader = false;
	boolean btext = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("Doc")) {
			// create a new Employee and put it in Map
			//String id = attributes.getValue("id");
			// initialize Employee object and set id attribute
			doc = new DDocument();
			//doc.setId(Integer.parseInt(id));
			// initialize list
			if (docList == null)
				docList = new ArrayList<>();
		} else if (qName.equalsIgnoreCase("docno")) {
			// set boolean values for fields, will be used in setting Employee variables
			bdocno = true;
		} else if (qName.equalsIgnoreCase("ht")) {
			bht = true;
		} else if (qName.equalsIgnoreCase("header")) {
			bheader = true;
		} else if (qName.equalsIgnoreCase("text")) {
			btext = true;
		}
		// create the data container
		data = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (bdocno) {
			// age element, set Employee age
			doc.setDocno(data.toString());
			bdocno = false;
		} else if (bht) {
			doc.setHt(data.toString());
			bht = false;
		} else if (bheader) {
			doc.setHeader(data.toString());
			bheader = false;
		} else if (btext) {
			doc.setText(data.toString());
			btext = false;
		}
		
		if (qName.equalsIgnoreCase("Doc")) {
			// add Employee object to list
			docList.add(doc);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		data.append(new String(ch, start, length));
	}
}
