package it.unisa.tiziano.pdtify.service;

import it.unisa.tiziano.pdtify.entity.Song;

import java.util.List;
import java.util.UUID;

public interface SongService {

    List<Song> findAll();

    Song findByName(String name);

    void save(Song song);
}
