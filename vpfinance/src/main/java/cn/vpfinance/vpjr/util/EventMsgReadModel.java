package cn.vpfinance.vpjr.util;

public class EventMsgReadModel {
    public String type;
    public String isRead;

    public EventMsgReadModel(String type, String isRead) {
        this.type = type;
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }
}
