package it.unisa.tiziano.pdtify.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Song {

    public Song(String authors, String name, URL url) {
        this.authors = authors;
        this.name = name;
        this.url = url;
        this.views = 0;
    }

    public void addSingleView() {
        this.views += 1;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ID;

    private String authors;

    private String name;

    private URL url;

    private int views;
}
