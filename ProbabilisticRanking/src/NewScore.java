public class NewScore {
    public float score;
    public String docid;
    public int luceneDocid;

    public NewScore() {
    }

    public NewScore(int luceneDocid, float score) {
        this.luceneDocid = luceneDocid;
        this.score = score;
    }
    
}