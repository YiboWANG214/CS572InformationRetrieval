public class entry {
	public static void main(String[] args) throws Exception{

		String index = args[1];
    	String queries = args[2];
    	String filename = args[3];

		if("BM25".equals(args[0])){
			BM25 bm25 = new BM25();
			bm25.BM25(index, queries, filename);
		} else if (("LMLaplace").equals(args[0])){
			LMLaplace lm = new LMLaplace();
			lm.LMLaplace(index, queries, filename);
		} else if (("RM1").equals(args[0])){
			RM1 rm1 = new RM1();
			rm1.RM1(index, queries, filename);
		} else if (("RM3").equals(args[0])){
			RM3 rm3 = new RM3();
			rm3.RM3(index, queries, filename);
		}
	}
}
