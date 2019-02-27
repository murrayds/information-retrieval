/*
 * Author: Dakota Murray
 * 
 * I am drawing heavily on code I found as part of the Lucene
 * Demo, which goes through loading documents from a file in Java 
 * using Lucene's functionality. This tutorial can be found at the 
 * following link:
 * 
 * https://www.avajava.com/tutorials/lessons/how-do-i-use-lucene-to-index-and-search-text-files.html
 * 
 * I am also drawing on a few other tutorials I found for XML reading and such, but I did a lot of googling and 
 * honestly lost most of those sources. I tried my best not to simply copy any code, and instead I modified and 
 * commented all the code here. 
 * 
 */
package search;


// Import necessary Java IO and data structure libraries
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

// Import Lucene libraries
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

// import XMl data
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;


public class generateIndex {
	
	// Define file paths
	public static final String FILES_TO_INDEX_DIRECTORY = "/Users/dakotamurray/Desktop/corpus/";
	public static final String FILE_EXTENSION = ".trectext";
	
	public static final String PATH_TO_INDEX_OUTPUT = "/Users/dakotamurray/Desktop/index/";
	
	// Define document tags from the corpus
	public static final String DOC_TAG = "DOC";
	public static final String DOCNO_TAG = "DOCNO";
	public static final String HEAD_TAG = "HEAD";
	public static final String BYLINE_TAG = "BYLINE";
	public static final String DATELINE_TAG = "DATELINE";
	public static final String TEXT_TAG = "TEXT";
	
	
	/*
	 * Simple `main` function; when executed, creates simple index using constants set within this file
	 */
	public static void main(String[] args) throws IOException, Exception {
		
		generateIndex.createIndex(FILES_TO_INDEX_DIRECTORY,  FILE_EXTENSION,  PATH_TO_INDEX_OUTPUT);
	}
	
	/*
	 * createIndex
	 * 
	 * Creates the index, taking relevant files from the input directory and constructing a lucene index, outputting
	 * it in the provided output directory.
	 * 
	 * This version of the function takes no analyzer as a parameter—instead, it defaults to the StandardAnalyzer
	 * 
	 * @param corpusPath: String containing file path to the Corpus
	 * @param corpusFileExt: String containing the file extension of relevant files in the corpus directory
	 * @param indexPath: String containing the path to output the Lucene index
	 */
	public static void createIndex(String corpusPath, String corpusFileExt, String indexPath) throws Exception {
		Analyzer analyzer = new StandardAnalyzer();
		generateIndex.createIndex(corpusPath, corpusFileExt, indexPath, analyzer);
	}
	
	/*
	 * createIndex
	 * 
	 * Creates the index, taking relevant files from the input directory and constructing a lucene index, outputting
	 * it in the provided output directory
	 * 
	 * @param corpusPath: String containing file path to the Corpus
	 * @param corpusFileExt: String containing the file extension of relevant files in the corpus directory
	 * @param indexPath: String containing the path to output the Lucene index
	 * @param analyzer: The Lucene analyzer to apply when creating the index
	 */
	public static void createIndex(String corpusPath, String corpusFileExt, String indexPath, Analyzer analyzer) throws Exception {
		List<String> files = generateIndex.getListOfFiles(corpusPath, corpusFileExt);
		System.out.println("Number of files :" + files.size());

		// Open the directory output
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		//Analyzer analyzer = new StandardAnalyzer();
		
		
		// Build the object that will write Lucene documents to the index
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);
		
		Integer numOfDocuments = 0;
		
		// Iterate through each file, construct to a Lucene document and then write to the index
		for (String file : files) {
			List<Document> documents = generateIndex.readOneFile(file, false);
			numOfDocuments = numOfDocuments + documents.size();
			for (Document doc : documents) {
				writer.addDocument(doc);
			}
		}
		writer.close();
		
		System.out.println("Number of documents = " + numOfDocuments);
		
	}
	
	
	/*
	 * createOneLuceneDocument
	 * 
	 * Converts the key-value hashmap into a document
	 * 
	 * @param docValues: A HashMap of values as that which was returned by `readOneFile`
	 * 
	 * @returns: A document containing the key-value combinations from the corpus
	 */
	private static Document createOneLuceneDocument(HashMap<String, String> docValues) {
		Document luceneDoc = new Document();

		luceneDoc.add(new StringField(DOCNO_TAG, docValues.get(DOCNO_TAG), Field.Store.YES));
		luceneDoc.add(new StringField(HEAD_TAG, docValues.get(TEXT_TAG), Field.Store.YES));
		luceneDoc.add(new StringField(BYLINE_TAG, docValues.get(TEXT_TAG), Field.Store.YES));
		luceneDoc.add(new StringField(DATELINE_TAG, docValues.get(TEXT_TAG), Field.Store.YES));
		luceneDoc.add(new TextField(TEXT_TAG, docValues.get(TEXT_TAG), Field.Store.YES));
		
		return luceneDoc;
	}
	
	
	/*
	 * @function getListOfFiles
	 *  
	 * @param corpusPath: String path to the folder containing the corpus
	 * @param extToInclude: the file extension that files must have to be returned
	 * 
	 * @returns: list of files matching path names in the provided folder 
	 */
	public static List<String> getListOfFiles(String corpusPath, String extToInclude) {
		System.out.println("function call: getFiles");
		
		// Create arraylist to hold the files that will be returned
        List<String> listOfFiles = new ArrayList<>();
        
        // Get list of files contained in the given folder
        File folder = new File(corpusPath);
        File[] folderListings = folder.listFiles();
        
        // For each file...
        for (File file : folderListings) {
        	// Check that the file extension is the one that we are interested in
            if (file.getName().toLowerCase().endsWith(extToInclude)) {
            	// Get file path and return
            	String filePath = file.getPath().replace('\\', '/');
                listOfFiles.add(filePath);
            }
        }
        return listOfFiles;
	} // end getListOfFiles 
	
	
	/*
	 * function getContentFromFile
	 * 
	 * Given a filename, returns the content of that file as a String, with 
	 * processing completed making it ready for XML parsing.
	 */
	private static String getContentFromFile(String filePath) throws IOException {
		// Load the file
		File file = new File(filePath);
		
		// Extract file contents to string
        String content = FileUtils.readFileToString(file, "utf-8");
		
        // Add <root></> tag surrounding whole document
		content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <root>\n" + content + "\n</root>";
		// Replace '&' character with safe equivelant
		content = content.replaceAll("&", "and");
		
		return content;
	}
	
	/*
	 * getXMLRootElement
	 * 
	 * @param content: String containing xml content to parse
	 * 
	 * @returns the root element of the XML document
	 */
	private static Element getXMLRootElement(String content) throws Exception, IOException {
		
		// Create the document reader
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream input = new ByteArrayInputStream(
		   content.getBytes("utf-8"));
		
		// Parse the document
		org.w3c.dom.Document doc = builder.parse(input);
		
		// Construct and return the root element
		return(doc.getDocumentElement());
	}
	
	/*
	 * parseOneElement
	 * 
	 * Parses a single element from the XML node structure
	 * 
	 * @param element: The root of the node element with which to search for
	 * the children elements
	 * @param elementTagName: A string containing the tag of the children elements
	 * to search for
	 * 
	 * @return: A single string containing the content of each of the nodes with the
	 * tag provided as a parameter
	 */
	private static String parseOneElement(Element element, String elementTagName) {
		
		// Begin with an Arraylist to contain all of the String values from the nodes
		ArrayList<String> nodeValueList = new ArrayList<>();
		
		// Get all children elements with the specified tag name
		NodeList nodes = element.getElementsByTagName(elementTagName);
		
		// If nodes were retrieved, extract the relevant information
		if (nodes != null & nodes.getLength() > 0) {
			// Iterate through 1 or more children nodes, add them to the Array List
			for (int i = 0; i < nodes.getLength(); i++) {
				Element eItem = (Element) nodes.item(i);
				nodeValueList.add(eItem.getFirstChild().getNodeValue());
			}
		} 
		
		// Now, collapse the arraylist to a single string and return
		if (nodeValueList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String s : nodeValueList) {
			    sb.append(s);
			    sb.append(" ");
			}
			return(sb.toString());
		} else {
			return "";
		}
	}
	
	/*
	 * readOneFile
	 * 
	 * Given the path to a single file in the specified corpus, iterates through each document
	 * within the file and extracts relevant information from the XML nodes
	 * 
	 * @param filePath: A string contianing the path to the file to load
	 * 
	 * @returns A HashMap containing key-value pairs mapping each bit of relevant information to a tag
	 */
    public static List<Document> readOneFile(String filePath, Boolean toLog) throws Exception, IOException {
    	
    	List<Document> documents = new ArrayList<Document>();
    	
    	// Load file and get the content as an XML string
		String content = getContentFromFile(filePath);
		
		// Parse the XML and convert to a node format
		Element element = getXMLRootElement(content);
		
		// Get the list of document nodes in the file
		NodeList nodeList = element.getElementsByTagName(DOC_TAG);
        
        // Iterate through each document in the parsed XML
        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            Node nNode = nodeList.item(temp);
            
            // check to make sure that it is an element
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            	Element node = (Element) nNode;
            	
            	// Parse each of the elements
            	String docno = parseOneElement(node, DOCNO_TAG);
            	String head = parseOneElement(node, "HEAD");
            	String byline = parseOneElement(node, "BYLINE");
            	String dateline = parseOneElement(node, "DATELINE");
            	String text = parseOneElement(node, "TEXT");
            	
            	// If the logging parameter is set to true, print out the document elements
            	if (toLog) {
            		System.out.println(DOCNO_TAG + " = " + docno);
            		System.out.println(HEAD_TAG + " = " + head);
            		System.out.println(BYLINE_TAG + " = " + byline);
            		System.out.println(DATELINE_TAG + " = " + dateline);
            		System.out.println(TEXT_TAG + " = " + text);
            	}
            	
            	// Place into a hashmap
            	HashMap<String, String> parsedValues = new HashMap<String, String>();
            	
            	parsedValues.put(DOCNO_TAG, docno);
            	parsedValues.put(HEAD_TAG, head);
            	parsedValues.put(BYLINE_TAG, byline);
            	parsedValues.put(DATELINE_TAG, dateline);
            	parsedValues.put(TEXT_TAG, text);
            	
            	// Now add the document to the function-level document
            	documents.add(createOneLuceneDocument(parsedValues));
            }
        }
        
        return(documents);
    }

}
