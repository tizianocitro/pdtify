package it.unisa.tiziano.pdtify.service;

import it.unisa.tiziano.pdtify.entity.Song;

import java.util.List;

public interface SongService {

    List<Song> findAll();

    Song findByName(String name);

    void save(Song song);

    void updateSongViewsByName(String name);
}
