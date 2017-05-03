package com.example.analshah.chatbot;

import android.net.Uri;

/**
 * Created by analll on 14-04-2017.
 */

public class ImageDetail {
    String imagename;

    String imageid;

    String userid;
    String fileuri;

    public String getFileuri() {
        return fileuri;
    }

    public void setFileuri(String fileuri) {
        this.fileuri = fileuri;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public ImageDetail()
    {

    }



    public String getImagename() {
        return imagename;
    }

    public String getImageid() {
        return imageid;
    }

    public ImageDetail(String imagename, String imageid, String userid,String fileuri) {
        this.imagename = imagename;
        this.imageid = imageid;
        this.userid = userid;
        this.fileuri=fileuri;

    }
}
