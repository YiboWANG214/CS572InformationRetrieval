Lucene BM25:
We use Lucene built-in BM25Similarity to set the similarity implementation used by IndexSearcher.
 
Lucene LM similarity:
We use Lucene built-in LMDirichletSimilarity to set the similarity implementation used by IndexSearcher.
 
Lucene RM1:
At first, we use LMDirichletSimilarity to search for documents. We try to add our stop words list and change the analyzer, but we find using standard analyzer can also get meaningful expanded terms.
Then we calculate P(t|q,R) taking use of LMsimilarity, normalize P(t|q,R) and choose top 5 terms from the top 5 documents to expand queries. 
We try to write the function to calculate P(q|D) by ourselves, from using different smoothing methods as followings:
1. P(qi|D) = 1 + (1+tf) / (mu * ((1+cf) / |C|)) + mu/(|Di| +mu); 
2. P(qi|D) = ((|Di|/(mu+|Di|))*tf/|Di|) + ((mu/(|Di|+mu)) * cf/|C|);
3. P(qi|D) = (Lambda) * tf / |Di| + ((1 - Lambda) * cf / |C|);
But these methods have not achieved good results. So we decide to use the score from LMsimilarity. Then we calculate the product of P(t|D) and P(q|D), and add them to be weight of each term. After selecting top 5 terms by sorting, we expand them into the original queries.
At last, we re-rank top 1000 documents on the expanded queries.
 
Lucene RM3:
At first, we use LMDirichletSimilarity to search for documents. Then we calculate P(t|q,R)_3 = (1 - lambda)*P(t|q,R)_MLE + lambda*P(t|q,R), normalize P(t|q,R)_3 and choose top 5 terms to expand queries. Then we re-rank top 1000 documents on the expanded queries.


HW1.jar:
java -jar HW1.jar BM25 indexpath querypath outputfile
java -jar HW1.jar LMLaplace indexpath querypath outputfile
java -jar HW1.jar RM3 indexpath querypath outputfile
