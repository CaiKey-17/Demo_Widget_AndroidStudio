package com.example.api.repository;

import com.example.api.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Repository extends JpaRepository<Music,Integer> {
}
