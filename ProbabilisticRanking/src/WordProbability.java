class WordProbability {
    String w;
    float p_w_given_R;      // proba. of w given R

    public WordProbability() {
    }

    public WordProbability(String w, float p_w_given_R) {
        this.w = w;
        this.p_w_given_R = p_w_given_R;
    }

}