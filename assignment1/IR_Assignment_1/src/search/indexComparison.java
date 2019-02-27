/*
 * Author: Dakota Murray
 * 
 */

package search;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class indexComparison {

	
	public static final String PATH_TO_INDEX = "/Users/dakotamurray/Desktop/index/";
	
	public static final String STANDARD_ANALYZER_INDEX_OUTPUT = "/Users/dakotamurray/Desktop/standard_index/";
	public static final String SIMPLE_ANALYZER_INDEX_OUTPUT = "/Users/dakotamurray/Desktop/simple_index/";
	public static final String KEYWORD_ANALYZER_INDEX_OUTPUT = "/Users/dakotamurray/Desktop/keyword_index/";
	public static final String STOP_ANALYZER_INDEX_OUTPUT = "/Users/dakotamurray/Desktop/stop_index/";
	
	
	public static void main(String[] args) throws IOException, Exception {
		indexComparison.createEachIndex();
		
		System.out.println("--------------------------------");
		System.out.println("STANDARD ANALYZER");
		System.out.println("--------------------------------");
		indexComparison.printStatsForIndex(STANDARD_ANALYZER_INDEX_OUTPUT);

		System.out.println("--------------------------------");
		System.out.println("SIMPLE ANALYZER");
		System.out.println("--------------------------------");
		indexComparison.printStatsForIndex(SIMPLE_ANALYZER_INDEX_OUTPUT);
		
		System.out.println("--------------------------------");
		System.out.println("KEYWORD ANALYZER");
		System.out.println("--------------------------------");
		indexComparison.printStatsForIndex(KEYWORD_ANALYZER_INDEX_OUTPUT);
		
		System.out.println("--------------------------------");
		System.out.println("STOP ANALYZER");
		System.out.println("--------------------------------");
		indexComparison.printStatsForIndex(STOP_ANALYZER_INDEX_OUTPUT);
	}

	
	/*
	 * printStatsForIndex
	 * 
	 * @param pathToIndex: String containing path to the index to print stats for
	 */
	public static void printStatsForIndex(String pathToIndex) throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get(pathToIndex)));

		System.out.println("Total number of documents in the corpus: "
				+ reader.maxDoc());

		int count = 0;
		Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		TermsEnum iterator = vocabulary.iterator();
		BytesRef iterPointer = null;
		while((iterPointer = iterator.next()) != null) {
			count = count+1;
		}
		
		System.out.println("Size of vocabulary = " + count);

		System.out
				.println("Number of documents that have at least one term for this field: "
						+ vocabulary.getDocCount());

		System.out.println("Number of tokens for this field: "
				+ vocabulary.getSumTotalTermFreq());

		System.out.println("Number of postings for this field: "
				+ vocabulary.getSumDocFreq());


		reader.close();
	}
	
	/*
	 * createEachIndex
	 * 
	 * Simple method to encapsulate the creation of each of the indexes specified in 
	 * the assignment. I know that I shouldn't be hard-coding things here, but I think
	 * that this is a small enough problem that perfect code isn't necessary
	 */
	public static void createEachIndex() throws Exception {
		
		Analyzer standardAnalyzer = new StandardAnalyzer();
		Analyzer keywordAnalyzer = new KeywordAnalyzer();
		Analyzer stopAnalyzer = new StopAnalyzer();
		Analyzer simpleAnalyzer = new SimpleAnalyzer();
		
		generateIndex.createIndex(
				generateIndex.FILES_TO_INDEX_DIRECTORY,
				generateIndex.FILE_EXTENSION,
				STANDARD_ANALYZER_INDEX_OUTPUT,
				standardAnalyzer);
		
		generateIndex.createIndex(
				generateIndex.FILES_TO_INDEX_DIRECTORY,
				generateIndex.FILE_EXTENSION,
				SIMPLE_ANALYZER_INDEX_OUTPUT,
				simpleAnalyzer);
		
		generateIndex.createIndex(
				generateIndex.FILES_TO_INDEX_DIRECTORY,
				generateIndex.FILE_EXTENSION,
				STOP_ANALYZER_INDEX_OUTPUT,
				stopAnalyzer);
		
		generateIndex.createIndex(
				generateIndex.FILES_TO_INDEX_DIRECTORY,
				generateIndex.FILE_EXTENSION,
				KEYWORD_ANALYZER_INDEX_OUTPUT,
				keywordAnalyzer);	
	}
	
}
