package cn.vpfinance.vpjr.model;

public class ProjectIntroduction {

    private int borrowbaseId;
    private String content;
    private long id;
    private long loansignId;
    private String name;
    private int sort;
    private int ctype;

    public void setBorrowbaseId(int borrowbaseId){
        this.borrowbaseId = borrowbaseId;
    }

    public int getBorrowbaseId(){
        return borrowbaseId;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public void setLoansignId(long loansignId){
        this.loansignId = loansignId;
    }

    public long getLoansignId(){
        return loansignId;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setSort(int sort){
        this.sort = sort;
    }

    public int getSort(){
        return sort;
    }

    public void setCType(int ctype){
        this.ctype = ctype;
    }

    public int getCType(){
        return ctype;
    }
}
