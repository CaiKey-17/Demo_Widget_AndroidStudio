package com.example.demo.Model;

public class Music {
    private int id;
    private String name;
    private String fileMp3;
    private String singer;

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