package com.example.demo.Model;

import java.io.Serializable;

public class Music implements Serializable {
    private int id;
    private String name;
    private String fileMp3;
    private String singer;
    private  String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Music() {
    }

    public Music(int id, String name, String fileMp3, String singer) {
        this.id = id;
        this.name = name;
        this.fileMp3 = fileMp3;
        this.singer = singer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileMp3() {
        return fileMp3;
    }

    public void setFileMp3(String fileMp3) {
        this.fileMp3 = fileMp3;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }
}