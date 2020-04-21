package com.journaldev.xml;

public class DDocument {
    private String docno;
    private String ht;
    private String header;
    private String text;
    
    public String getDocno() {
        return docno;
    }
    public void setDocno(String docno) {
        this.docno = docno;
    }
    public String getHt() {
        return ht;
    }
    public void setHt(String ht) {
        this.ht = ht;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    
    @Override
    public String toString() {
        return "" + this.header  + this.text;
    }
    
}
