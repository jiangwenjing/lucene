//example 6
package ie.tcd.dalyc24;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.regex.*;
import java.util.*;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import java.io.IOException;
import org.apache.lucene.search.similarities.Similarity;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;


public class QueryIndex
{
    
    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "../index";
    private static int MAX_RESULTS = 10;
    private static Similarity similarity;
    private Analyzer analyzer;
    private Directory directory;

    public QueryIndex(boolean flag) throws IOException
    {
        // Need to use the same analyzer and index directory throughout, so
        // initialize them here
        //this.analyzer = new StandardAnalyzer();
        //https://lucene.apache.org/core/8_6_3/analyzers-common/org/apache/lucene/analysis/en/EnglishAnalyzer.html
        this.analyzer = new EnglishAnalyzer();
        this.directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        if (flag)
            this.similarity = new ClassicSimilarity();
        else
            this.similarity = new BM25Similarity();


    }

    public void buildIndex(Map<String, Map<String, String>> map) throws IOException
    {

        // Create a new field type which will store term vector information
        FieldType ft = new FieldType(TextField.TYPE_STORED);
        ft.setTokenized(true); //done as default
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setStoreTermVectorOffsets(true);
        ft.setStoreTermVectorPayloads(true);

        // create and configure an index writer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        config.setSimilarity(similarity);
        IndexWriter iwriter = new IndexWriter(directory, config);

        // Add all input documents to the index
        for (String id : map.keySet())
        {
            ////System.out.printf("Indexing \"%s\"\n", map.get(id).get("title"));
            String content = map.get(id).get("word");
            Document doc = new Document();
            doc.add(new StringField("id", id, Field.Store.YES));
            doc.add(new Field("content", content, ft));
            iwriter.addDocument(doc);
        }
        
        // close the writer
        iwriter.close();
    }
    


    public void shutdown() throws IOException
    {
        directory.close();
    }

    public ArrayList<String> getQueryList(String content) throws IOException{
        TokenStream tokenStream = analyzer.tokenStream("", new StringReader(content));
        tokenStream.reset();
        tokenStream.addAttribute(CharTermAttribute.class);
        ArrayList<String> list = new ArrayList<String>();
        while (tokenStream.incrementToken()) {
            CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
            list.add(termAttribute.toString());
            //System.out.println(termAttribute.toString());
        }
        tokenStream.end();
        tokenStream.close();
        return list;
    }

    public static void main(String[] args) throws IOException
    {


    boolean flag = true;
    if (args.length > 0)
    {
        flag=false;

    }



    String content = new String(Files.readAllBytes(Paths.get("../cran/cran.all.1400")));
    //save cran.all.1400 into dict
    Map<String, Map<String, String>> map = new HashMap<>();
    for(String split_doc:content.split(".I"))
    {
        Map<String, String> map1= new HashMap<>();
        if (split_doc.length()>0)
        {    //System.out.println(split_doc);

            String id = "";
            String title = "";
            String author = "";
            String b = "";
            String word = "";
            
            //get id
            Pattern pattern_id = Pattern.compile("^\\s(.*)");
            Matcher m_id = pattern_id.matcher(split_doc);
            if (m_id.find( ))
            {
                id = m_id.group(1);
                //System.out.println(id);
            } 
            else
            {
                System.out.println(split_doc);
                System.out.println("NO MATCH id");                                           
            }
            
            
            //get title
            Pattern pattern_title = Pattern.compile(".*\\.T(.*).*\\.A",Pattern.DOTALL);
            Matcher m_title = pattern_title.matcher(split_doc);
            if (m_title.find( )) 
            {
                title = m_title.group(1);
                //System.out.println(title);
            } 
            else
            {
                System.out.println(split_doc);
                System.out.println("NO MATCH title");                                           
            }
            
            //get author
            Pattern pattern_author = Pattern.compile(".*\\.A(.*).*\\.B",Pattern.DOTALL);
            Matcher m_author = pattern_author.matcher(split_doc);
            if (m_author.find( )) 
            {
                author = m_author.group(1);
                //System.out.println(author);
            }
            else
            {
                System.out.println(split_doc);
                System.out.println("NO MATCH author");                                           
            }
            
            //get b
            Pattern pattern_b = Pattern.compile(".*\\.B(.*).*\\.W",Pattern.DOTALL);
            Matcher m_b = pattern_b.matcher(split_doc);
            if (m_b.find( )) 
            {
                b = m_b.group(1);
                //System.out.println(b);
            } 
            else
            {
                System.out.println(split_doc);
                System.out.println("NO MATCH b");                                           
            }
            
            //get word
            Pattern pattern_word = Pattern.compile(".*\\.W(.*)",Pattern.DOTALL);
            Matcher m_word = pattern_word.matcher(split_doc);
            if (m_word.find( )) 
            {
                word = m_word.group(1);
                //System.out.println(word);
            } 
            else
            {
                System.out.println(split_doc);
                System.out.println("NO MATCH word");                                           
            }
            //System.out.println(id);
            //System.out.println(id.length());
            
            map1.put("title",title);
            map1.put("author",author);
            map1.put("b",b);
            map1.put("word",word);
            map.put(id,map1);
        }
    }

    //save cran.qry to map2
    String cran_qry = new String(Files.readAllBytes(Paths.get("../cran/cran.qry")));
    Map<String, String> map2= new LinkedHashMap<>();
    int cnt = 1;
    String string_cnt = "1";
    for(String split_qry:cran_qry.split(".I"))
    {
        if (split_qry.length()>0)
        {
            Pattern pattern_word = Pattern.compile(".*\\.W(.*)",Pattern.DOTALL);
            Matcher m_word = pattern_word.matcher(split_qry);
            if (m_word.find( ))
            {
                String word = m_word.group(1);
                System.out.print(string_cnt);
                System.out.println(word);
                map2.put(string_cnt,word);
                cnt = cnt + 1;
                string_cnt = String.valueOf(cnt);
            }
            else
            {
                System.out.println(split_qry);
                System.out.println("NO MATCH word");
            }
        }

    }

    

        QueryIndex qi = new QueryIndex(flag);
        qi.buildIndex(map);
        String path = "../cran/VSM_result.txt";
        if (flag==false)
            path = "../cran/BM25_result.txt";
        FileWriter fw = new FileWriter(path, false);

        BufferedWriter bw = new BufferedWriter(fw);
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // create objects to read and search across the index
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        isearcher.setSimilarity(similarity);
        // builder class for creating our query

        for (String id : map2.keySet())
        {
            ArrayList<String> list = qi.getQueryList(map2.get(id));
            BooleanQuery.Builder query = new BooleanQuery.Builder();
            for (int i = 0; i < list.size(); i++)
            {
                Query term1 = new TermQuery(new Term("content", list.get(i)));
                query.add(new BooleanClause(term1, BooleanClause.Occur.SHOULD));
            }


            // Get the set of results from the searcher
            ScoreDoc[] hits = isearcher.search(query.build(), MAX_RESULTS).scoreDocs;

            // Print the results
            //System.out.println("Documents: " + hits.length);
            for (int i = 0; i < hits.length; i++)
            {
                Document hitDoc = isearcher.doc(hits[i].doc);
                String ret = id + " Q0 "+hitDoc.get("id") + " " + String.valueOf(i+1) + " " +hits[i].score+" STANDARD";
                System.out.println(ret);
                bw.write(ret);
                bw.newLine();
                bw.flush();
            }
        }
        // close everything we used
        bw.close();
        fw.close();
        ireader.close();
        directory.close();
    }

  
}
