//new search files
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.TopScoreDocCollector;

import org.apache.lucene.search.similarities.PerFieldSimilarityWrapper;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import java.util.Collections;
import java.util.Comparator;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;

import java.math.BigDecimal;

import java.io.FileInputStream;
import java.io.FileWriter;

public class RM1 {

  //private RM1() {}
  
  // original SearchFiles
  public static void RM1(String index1, String queries1, String filename1) throws Exception {


    String index = index1;
    String field = "contents";
    String queries = queries1;
    int repeat = 0;
    boolean raw = false;
    String queryString = null;
    int hitsPerPage = 10;
    


    // change the similarity algorithem

    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    IndexSearcher searcher = new IndexSearcher(reader);
    IndexReader reader22 = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    IndexSearcher searcher22 = new IndexSearcher(reader);
    
    // BM25
    searcher22.setSimilarity(new BM25Similarity(1.4f,0.9f));

    // LM Dirichlet
    searcher.setSimilarity(new LMDirichletSimilarity());

    //LM JelinekMercer
    //searcher.setSimilarity(new LMJelinekMercerSimilarity(0.1f));
    Analyzer analyzer = new StandardAnalyzer();

    /*
    We try to add our stop words list and change the analyzer.
    */
    //Analyzer analyzer = new EnglishAnalyzer();
    //EnglishAnalyzer.getDefaultStopSet();
    //String stopFilePath = "/Users/yibowang/Desktop/smart-stopwords.dms";
    //EnglishAnalyzerWithSmartStopword engAnalyzer = new EnglishAnalyzerWithSmartStopword(stopFilePath);
    //Analyzer analyzer = engAnalyzer.setAndGetEnglishAnalyzerWithSmartStopword();


    // Handling query
    BufferedReader in = null;
    if (queries != null) {
      in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
    } else {
      in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }
    QueryParser parser = new QueryParser(field, analyzer);



    while (true) {
      if (queries == null && queryString == null) {
        //System.out.println(field);       // prompt the user
        System.out.println("Enter query: ");
      }

      String line = queryString != null ? queryString : in.readLine();

      if (line == null || line.length() == -1) {
        break;
      }

      line = line.trim();
      if (line.length() == 0) {
        break;
      }
      
      Query query = parser.parse(line);

      System.out.println("Searching for: " + query.toString(field) + "\n");
            
      if (repeat > 0) {                           // repeat & time as benchmark
        Date start = new Date();
        for (int i = 0; i < repeat; i++) {
          searcher.search(query, 100);
        }
        Date end = new Date();
        System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
      }

      String filename = filename1;
      doPagingSearch(in, searcher, filename, query, hitsPerPage, raw, queries == null && queryString == null, reader,searcher22);

      if (queryString != null) {
        break;
      }
    }
    reader.close();
  }


  // get P(qi|D) for each Doc
  public static float getP_Q_D(String query, String document, float vocSize, IndexReader reader, IndexSearcher searcher, ScoreDoc[] hits)throws IOException{

    String[] splitQuery = query.replaceAll("\\pP","").trim().split("\\s+");
    String[] splitDocument = document.trim().split("\\s+");
    float mixingLambda = 0.2f;
    int mu = 2000;
    float docSize = splitDocument.length;//D


    float P_Q_D = 1.0f;

    for (int i = 0; i<splitQuery.length; i++){

        int cf = 0;
        for (int k = 0; k < 5; k++){
          int docID = hits[k].doc;
          Document doccc = searcher.doc(docID); 


          String docc = doccc.toString();

          String[] splitDocumentt = docc.trim().split("\\s+");

          for (int m = 0; m<splitDocumentt.length; m++){
                if(splitQuery[i].equalsIgnoreCase(splitDocumentt[m])){
                cf += 1;
              }
          } 
        }

        int nums = 0;

        for (int j = 0; j<splitDocument.length; j++){
              if(splitQuery[i].equalsIgnoreCase(splitDocument[j])){
              nums += 1;
            } 
        }
        float tf = nums;//tf

        //System.out.println("term:" + splitQuery[i]+"tf: "+tf+"  docSize:"+docSize+"  cf:"+cf+"  vocSize"+vocSize);

        //float tmp = ((docSize/(mu+docSize))*tf/docSize) + ((mu/(docSize+mu)) * cf/vocSize);
        //float tmp = (float) (Math.log(1 + tf / (mu * (cf / vocSize))) + Math.log(mu/(docSize +mu)));
        //float tmp = (float)Math.abs(Math.log((mixingLambda) * tf / docSize) + Math.log((1.0f - mixingLambda) * cf / vocSize));
        //float tmp = (float)Math.log((mixingLambda) * tf / docSize + (1.0f - mixingLambda) * cf / vocSize);
        //float tmp = (mixingLambda) * tf / docSize + (1.0f - mixingLambda) * cf / vocSize;
        float tmp = ((docSize/(mu+docSize))*(tf+1)/(docSize+1) + ((mu/(docSize+mu)) * (cf+1)/(vocSize+1)));

        P_Q_D *= (100.0f *tmp);
        
    }

    return P_Q_D;
    }

    // for searching, and calculate our P(t|q,R) , then re-rank
    public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, String filename, Query query, 
                                     int hitsPerPage, boolean raw, boolean interactive, IndexReader reader, IndexSearcher searcher22) throws IOException {
    	  
        long totalterms = reader.getSumTotalTermFreq("contents");
        TopDocs results = searcher.search(query, 1000);
        ScoreDoc[] hits = results.scoreDocs;
        int hits_length = hits.length;

        HashMap<String,HashMap<Integer,Long>> termm = new HashMap<>();
        HashMap<String,Float> pterm = new HashMap<>();
        HashMap<String,Float> cterm = new HashMap<>();
        HashMap<String,Float> finalterm = new HashMap<>();
        HashMap<String, WordProbability> hashmap_PtGivenR = new LinkedHashMap<>();

        // for getting total vocabulary size of all docs (collections)
        long totalhitssize = 0;

        for (int i = 0; i < 5; i++) { 
              int docId = hits[i].doc;
              Document d = searcher.doc(docId); 

              Terms terms = reader.getTermVector(docId,"contents");
              long vocSize = terms.getSumTotalTermFreq();

              totalhitssize += vocSize;
            }


        // choose terms from top-5 docs
        for (int i = 0; i < 5; i++) {
        	int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            Terms terms = reader.getTermVector(docId,"contents");
            long vocSize = terms.getSumTotalTermFreq();   // the total number of terms in each doc
            //int doclength = d.toString().length();  // = vocSize

            // return P(qi|D) for each Doc
            String document = d.toString();
            String stringquery = query.toString("contents");

            //float P_Q_D = getP_Q_D(stringquery, document, totalhitssize, reader, searcher, hits);

            TermsEnum iterator = terms.iterator();
            BytesRef byteRef = null;

            while((byteRef = iterator.next()) != null) {
            String term = new String(byteRef.bytes, byteRef.offset, byteRef.length);
            long termFreq = iterator.totalTermFreq();    // tf of 't' 

            float term_w = (1000.0f * termFreq) / vocSize * hits[i].score;  // 
            //System.out.println("term:" + term + " freq:" + termFreq + " size:" + vocSize + "hits score" + hits[i].score + " " + term_w);
            
            cterm.put(term,term_w);
            }   // end of while loop

            for (Map.Entry<String,Float> entrySet : cterm.entrySet()) {
              String key = entrySet.getKey();
              Float value = entrySet.getValue();

              if(null == finalterm.get(key)){
                finalterm.put(key,value);
              }
              else{
                Float v = value + finalterm.get(key);
                finalterm.put(key,v);
              }
            }
            }

            for (Map.Entry<String,Float> entrySet : finalterm.entrySet()) {
              String key = entrySet.getKey();
              Float value = entrySet.getValue();
            }

            for (Map.Entry<String,Float> entrySet : pterm.entrySet()) {
              String key = entrySet.getKey();
              Float value = entrySet.getValue();

              if(null == finalterm.get(key)){
                finalterm.put(key,value);
              }
              else{
                Float v = value + finalterm.get(key);
                finalterm.put(key,v);
              }
            }
          }   // end of for loop

          // normalize
          List<WordProbability> list_PtGivenR = new ArrayList<>();
          // calculate total values of all terms
          Float totalvalue = 0.0f;
          for (Map.Entry<String,Float> entrySet : finalterm.entrySet()) {
              String key = entrySet.getKey();
              Float value = entrySet.getValue();
              totalvalue += value;}

          // P(t|R) storing class: WordProbability(term,P(t|R))
          for (Map.Entry<String,Float> entrySet : finalterm.entrySet()) {
              String key = entrySet.getKey();
              Float value = entrySet.getValue();
              Float v = value / totalvalue;
              finalterm.put(key,v);
              list_PtGivenR.add(new WordProbability(key, v));
            }

            // sort P(t|R) for every term
            Collections.sort(list_PtGivenR, new Comparator<WordProbability>(){
              @Override
            public int compare(WordProbability t, WordProbability t1) {
                return t.p_w_given_R<t1.p_w_given_R?1:t.p_w_given_R==t1.p_w_given_R?0:-1;
            }});

            // still P(t|R) change class to: hashmap_PtGivenR <term, WordProbability>
            for (WordProbability singleTerm : list_PtGivenR) {
            if (null == hashmap_PtGivenR.get(singleTerm.w)) {
                hashmap_PtGivenR.put(singleTerm.w, new WordProbability(singleTerm.w, singleTerm.p_w_given_R));
              }
            }

            // expand, just for trying re-search, actually not needed
            int cc = 0;
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            
            for (Map.Entry<String, WordProbability> entrySet : hashmap_PtGivenR.entrySet()) {
              String key = entrySet.getKey();
              //System.out.println(key);
              WordProbability termprob = entrySet.getValue();
              float value = termprob.p_w_given_R;
              Term t = new Term("contents", key);
              Query tq = new TermQuery(t);
              
              booleanQuery.add(tq,BooleanClause.Occur.SHOULD);

              cc++;
              if(cc>=5)
                break;
            }

            //booleanQuery.add(query,BooleanClause.Occur.SHOULD); */
              //System.out.println(booleanQuery.build().toString("contents"));

              /*
               try to use LM again to re-rank (for comparing)
               */
              //TopDocs results22 = searcher22.search(query, 5 * hitsPerPage);
              /*TopDocs results22 = searcher22.search(booleanQuery.build(), 5 * hitsPerPage);
              ScoreDoc[] hits22 = results22.scoreDocs;

              if(hits22 == null)
                    System.out.println("Nothing found");

              int hits22_length = hits22.length;

              StringBuffer resBuffer22 = new StringBuffer();
              for (int i = 0; i < hits22_length; ++i) {
                   int docId22 = hits22[i].doc;
                   Document d22 = searcher.doc(docId22);
                   resBuffer22.append("query.qid").append("\tQ0\t").
                        append(hits22[i].doc).append(" \t").
                        append((i)).append(" \t").
                        append(hits22[i].score).append(" \t").
                        append("yyyy").append("\n");
                    }
               System.out.println(resBuffer22);*/

               // re-rank:

            List<NewScore> finalList = new ArrayList<>();
            //System.out.println(hits.length);
            for (int i = 0; i < Math.min(1000,hits.length); i++) {    
                int docId = hits[i].doc;
                Document d = searcher.doc(docId); 

                float score = 0.0f;

                String document = d.toString();
                String stringquery = booleanQuery.build().toString("contents");
                float P_Q_D = getP_Q_D(stringquery, document, totalhitssize, reader, searcher, hits);
                score = hits[i].score * P_Q_D;

                  finalList.add(new NewScore(docId,score));
                }

            // sort
            Collections.sort(finalList, new Comparator<NewScore>(){
            @Override
            public int compare(NewScore t, NewScore t1) {
                return t.score<t1.score?1:t.score==t1.score?0:-1;
            }
            });

            String str = "";
          for(int j = 0; j < query.toString("contents").length()-1; j++)
          {
            str = str + query.toString("contents").charAt(j);
            if(query.toString("contents").charAt(j+1) == ' '){
              break;
            }
          }

            // print the output of rerank
            StringBuffer resBuffer = new StringBuffer();
                    for (int i = 0; i < Math.min(1000,hits.length); ++i) {
                        int docId = finalList.get(i).luceneDocid;
                        Document d = searcher.doc(docId);
                        String path = d.get("path");  
                        resBuffer.append(str).append("\tQ0\t").
                            append(path).append("\t").
                            append((i+1)).append("\t").
                            append(finalList.get(i).score).append(" \t").
                            append("Yao.Ge & Yibo.Wang2").append("\n");                
                    }

            FileWriter writer=new FileWriter(filename,true);
            writer.write(resBuffer.toString()+"\n");
            writer.close(); 
            
            // original code of searchFiles
    int numTotalHits = Math.toIntExact(results.totalHits.value);
    System.out.println(numTotalHits + " total matching documents");

    int start = 0;
    int end = Math.min(numTotalHits, hitsPerPage);
        
    while (true) {
      if (end > hits.length) {
        System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
        System.out.println("Collect more (y/n) ?");
        String line = in.readLine();
        if (line.length() == 0 || line.charAt(0) == 'n') {
          break;
        }

        hits = searcher.search(query, numTotalHits).scoreDocs;
      }
      
      end = Math.min(hits.length, start + hitsPerPage);
      
      for (int i = start; i < end; i++) {
        if (raw) {                              // output raw format
          System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
          continue;
        }

        Document doc = searcher.doc(hits[i].doc);

        String path = doc.get("path");
        if (path != null) {
          
          //System.out.println(str + "\t" + "Q0" + "\t" + path + "\t" + (i+1) + "\t" + hits[i].score + " \t" + "Yao & Yibo");
          //System.out.println(str + "\t" + "Q0" + "\t" + hits[i].doc + "\t" + (i+1) + "\t" + hits[i].score + " \t" + "Yao & Yibo");
          String title = doc.get("title");
          if (title != null) {
            System.out.println("   Title: " + doc.get("title"));
          }
        } else {
          System.out.println((i+1) + ". " + "No path for this document");
        }
                  
      }

      if (!interactive || end == 0) {
        break;
      }

      if (numTotalHits >= end) {
        boolean quit = false;
        while (true) {
          System.out.print("Press ");
          if (start - hitsPerPage >= 0) {
            System.out.print("(p)revious page, ");  
          }
          if (start + hitsPerPage < numTotalHits) {
            System.out.print("(n)ext page, ");
          }
          System.out.println("(q)uit or enter number to jump to a page.");
          
          String line = in.readLine();
          if (line.length() == 0 || line.charAt(0)=='q') {
            quit = true;
            break;
          }
          if (line.charAt(0) == 'p') {
            start = Math.max(0, start - hitsPerPage);
            break;
          } else if (line.charAt(0) == 'n') {
            if (start + hitsPerPage < numTotalHits) {
              start+=hitsPerPage;
            }
            break;
          } else {
            int page = Integer.parseInt(line);
            if ((page - 1) * hitsPerPage < numTotalHits) {
              start = (page - 1) * hitsPerPage;
              break;
            } else {
              System.out.println("No such page");
            }
          }
        }
        if (quit) break;
        end = Math.min(numTotalHits, start + hitsPerPage);
      }
    }
  }
}