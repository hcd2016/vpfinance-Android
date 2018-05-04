package cn.vpfinance.vpjr.model;

public class QualificationMaterial {
    private String addTime;
    private String fileName;
    private String filePath;
    private String fileRemark;
    private int fileType;
    private long id;

    public void setAddTime(String addTime){
        this.addTime = addTime;
    }

    public String getAddTime(){
        return addTime;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){
        return filePath;
    }

    public void setFileRemark(String fileRemark){
        this.fileRemark = fileRemark;
    }

    public String getFileRemark(){
        return fileRemark;
    }

    public void setFileType(int fileType){
        this.fileType = fileType;
    }

    public int getFileType(){
        return fileType;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

}
