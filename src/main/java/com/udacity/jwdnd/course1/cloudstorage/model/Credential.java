package com.udacity.jwdnd.course1.cloudstorage.model;

public class Credential {
    private Integer informId;
    private String url;
    private String userName;
    private String idCode;
    private String password;
    private Integer userid;

    public Credential(Integer informId, String url, String userName, String idCode, String password, Integer userid) {
        this.informId = informId;
        this.url = url;
        this.userName = userName;
        this.idCode = idCode;
        this.password = password;
        this.userid = userid;
    }

    public Credential(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public Integer getInformId() {
        return informId;
    }

    public void setInformId(Integer informId) {
        this.informId = informId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String key) {
        this.idCode = idCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
}
