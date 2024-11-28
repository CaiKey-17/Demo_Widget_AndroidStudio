package com.example.api;


import com.example.api.model.Music;
import com.example.api.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
@RestController
@RequestMapping
public class controller {

    @Autowired
    private Service service;

    public controller(Service service) {
        this.service = service;
    }


    @GetMapping()
    public List<Music> getAll(){
        return service.getAll();
    }

    @GetMapping("/get")
    public Music getMusic(@RequestParam("id") int id){
        return service.getMusicById(id);
    }

    @PostMapping()
    public String addMusc(@RequestBody Music music){
        service.addMusic(music);
        return "Succes";
    }

    @PostMapping("/add")
    public String addMusic(@RequestParam("name") String name, @RequestParam("singer") String singer, @RequestParam("file") MultipartFile file) {
        String uploadDir = "src/main/resources/static/images/";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = uploadDir + file.getOriginalFilename();
        try {
            file.transferTo(Paths.get(filePath));

            Music music = new Music();
            music.setName(name);
            music.setSinger(singer);
            music.setFileMp3(filePath);

            service.addMusic(music);

            return "Music added successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading file!";
        }
    }

}
