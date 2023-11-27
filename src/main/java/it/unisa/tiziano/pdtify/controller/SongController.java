package it.unisa.tiziano.pdtify.controller;

import it.unisa.tiziano.pdtify.entity.Song;
import it.unisa.tiziano.pdtify.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("")
    public String home(Model model) {
        List<Song> songs = songService.findAll();
        model.addAttribute("songs", songs);
        return "home";
    }

    @GetMapping("/play")
    public String play(@RequestParam(value = "name") String name, Model model) {
        Song song = songService.findByName(name);
        model.addAttribute("song", song);
        return "play";
    }

    @GetMapping("/add-song")
    public String addSong(Model model) {
        model.addAttribute("song", new Song());
        return "addSong";
    }

    @PostMapping("/save-song")
    public String saveSong(@ModelAttribute Song song, Model model) {
        songService.save(song);
        List<Song> songs = songService.findAll();
        model.addAttribute("songs", songs);
        return "home";
    }
}
