package com.example.api.service;


import com.example.api.model.Music;
import com.example.api.repository.Repository;
import java.util.*;
@org.springframework.stereotype.Service
public class Service {

    private final Repository repository;

    public Service(Repository repository) {
        this.repository = repository;
    }

    public List<Music> getAll(){
        return repository.findAll();
    }

    public Music getMusicById(int id){
        return repository.findById(id).get();
    }


    public Music addMusic(Music music){
        return repository.save(music);
    }




}
