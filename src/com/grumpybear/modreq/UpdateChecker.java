package com.grumpybear.modreq;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateChecker {

	private main plugin;
	private URL latest;
	private String version,link;
	
	public UpdateChecker(main plugin, String url) {
		this.plugin = plugin;
		try {
			this.latest = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean newUpdate() {
		try {
			InputStream input = this.latest.openConnection().getInputStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			Node latest = doc.getElementsByTagName("item").item(0);
			NodeList children = latest.getChildNodes();
			
			// this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z^.0-9]", "");
			this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z -]", "");
			this.link = children.item(3).getTextContent();
			
			if (!plugin.getDescription().getVersion().equals(this.version)) {
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getLink() {
		return this.link;
	}
	
}
