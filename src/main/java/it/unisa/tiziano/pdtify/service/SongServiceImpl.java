package it.unisa.tiziano.pdtify.service;

import it.unisa.tiziano.pdtify.entity.Song;
import it.unisa.tiziano.pdtify.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Autowired
    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> findAll() {
        return songRepository.findAll();
    }

    @Override
    public Song findByName(String name) {
        return songRepository.findByName(name);
    }

    @Override
    public void save(Song song) {
        songRepository.save(song);
    }

    @Override
    public void updateSongViewsByName(String name) {
        Song song = findByName(name);
        song.addSingleView();
        save(song);
    }
}
