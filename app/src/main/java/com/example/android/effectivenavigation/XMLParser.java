package com.example.android.effectivenavigation;

/**
 * Created by Mohit on 11/30/2014.
 */

import android.content.Context;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.IOException;

public class XMLParser {

    //Function to get the XMl from a given URL.
    public String getXmlFromUrl(String url) {
        String xml = null;

        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            Log.e("XMLParser", e.getMessage());
        } catch (ClientProtocolException e) {
            Log.e("XMLParser", e.getMessage());
        } catch (IOException e) {
            Log.e("XMLParser", e.getMessage());
        }
        // return XML
        return xml;
    }

    public void writeXmlToFile(String fileContents, Context context, String filename) {
        try {
            Log.d("XML Parser", "Writing to the file.");
            FileWriter out = new FileWriter(new File(context.getFilesDir(), filename));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            Log.e("XMLParser", e.getMessage());
        }
    }

    public InputStream readXmlFromFile(Context context, String filename) {
        InputStream storyPlotXml = null;
        try {
            Log.d("XML Parser", "Reading from file.");
            //File storyFile = context.getFilesDir() + "/" + filename;
            storyPlotXml = context.openFileInput(filename);
        } catch (IOException e) {
            Log.e("XMLParser", e.getMessage());
        }
        return storyPlotXml;
    }

    //Function to get the DOM Element of the XML.
    public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("XMLParser", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("XMLParser", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("XMLParser", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }

    //Function to get value of a single element.
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    //Function to get the required value that we need.
    public final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    //Function to convert a inputstream to a string. This would be used to read the input
    public String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            Log.e("XMLParser", "Converting Steam to string failed" + e.getMessage());
        }
        return sb.toString();
    }
}
