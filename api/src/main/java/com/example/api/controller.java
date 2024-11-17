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
        // Define the upload directory
        String uploadDir = "src/main/resources/static/images/";

        // Create the directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();  // Ensure the directory exists before saving the file
        }

        // Save the MP3 file on the server
        String filePath = uploadDir + file.getOriginalFilename();  // Full path with filename
        try {
            file.transferTo(Paths.get(filePath));  // Transfer the file to the desired path

            // Create a new Music object with the file path
            Music music = new Music();
            music.setName(name);
            music.setSinger(singer);
            music.setFileMp3(filePath);  // Save the file path in the Music object

            // Save the music object to the database
            service.addMusic(music);

            return "Music added successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading file!";
        }
    }

}
