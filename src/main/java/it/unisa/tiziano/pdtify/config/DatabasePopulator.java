package it.unisa.tiziano.pdtify.config;

import it.unisa.tiziano.pdtify.entity.Song;
import it.unisa.tiziano.pdtify.repository.SongRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class DatabasePopulator {

    private final SongRepository songRepository;

    @Autowired
    public DatabasePopulator(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @PostConstruct
    public void populate() {
        try {
            Song s1 = new Song(
                    "Queen",
                    "We Are The Champions",
                    new URL("https://www.youtube.com/embed/KXw8CRapg7k"));
            Song s2 = new Song(
                    "Home Free",
                    "Sea Shanty Medley",
                    new URL("https://www.youtube.com/embed/lLGLUSzzuWU"));

            if (!songRepository.existsByName(s1.getName())) {
                songRepository.save(s1);
            }
            if (!songRepository.existsByName(s2.getName())) {
                songRepository.save(s2);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
