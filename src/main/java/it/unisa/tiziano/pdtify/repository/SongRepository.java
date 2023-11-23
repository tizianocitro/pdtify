package it.unisa.tiziano.pdtify.repository;

import it.unisa.tiziano.pdtify.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    Song findByName(String name);

    boolean existsByName(String name);
}
