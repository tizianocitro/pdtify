# PDtify
Progetto in Spring Boot per la gestione di una playlist utilizzando come player di riproduzione
le API di Youtube Embedded nell'ambito del corso di Programmazione Distribuita 
dell'Università degli Studi di Salerno.

L'applicazione è ispirata dagli esercizi [07-lab-EJB](https://github.com/spagnuolocarmine/programmazione-distribuita/blob/22.23/07-lab-EJB/README.md).

## Recuperare facilmente il progetto
Per recuperare facilmente il progetto, lo si può aggiungere a una lista su GitHub che
sarà poi facilmente accessibile dal proprio profilo.

Nell'esempio, si aggiunge il progetto a una lista chiamata `PD`, ma si può chiamarla come si preferisce.

![Add project to list](https://github.com/tizianocitro/pdtify/blob/main/assets/StartProject.png)

## Screenshot dell'applicazione
### Pagina Home
![Home](https://github.com/tizianocitro/pdtify/blob/main/assets/Home.png)

L'applicazione è anche responsive, anche se non in maniera impeccabile.

![Home mobile](https://github.com/tizianocitro/pdtify/blob/main/assets/HomeMobile.png)

### Pagina per il `play` delle canzoni
![Play song](https://github.com/tizianocitro/pdtify/blob/main/assets/PlaySong.png)

### Pagina per l'aggiunta di una nuova canzone
![Add song](https://github.com/tizianocitro/pdtify/blob/main/assets/AddSong.png)

## Setup progetto
Scaricare il file [pdtify.zip](https://github.com/tizianocitro/pdtify/blob/main/pdtify.zip) oppure
creare un nuovo progetto tramite [Spring Initializer](https://start.spring.io/), come nell'immagine seguente:

![Project setup in Spring Initializer](https://github.com/tizianocitro/pdtify/blob/main/assets/SpringInitializer.png)

> Sostituire `tiziano` con il proprio nome.

## Configurare l'applicazione

### File pom.xml
Aggiungere nel file `pom.xml` le dipendenze per `Lombok` e
il connector per `MySQL`, usato come database.

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.26</version>
    <optional>true</optional>
</dependency>
```

### File application.properties
Aggiungere le proprietà necessarie all'applicazione
nel file `application.properties` nella directory `src/main/resources`.

```text
# La porta di ascolto per le chiamate in arrivo
server.port = 8080

# Livello di logging che indica il dettaglio delle informazioni visualizzate nella console durante l'esecuzione 
logging.level.root = INFO

# Configurazioni specifiche per il database utilizzato
spring.datasource.url = jdbc:mysql://localhost:3306/pdtify?createDatabaseIfNotExist=true
spring.datasource.username = root
spring.datasource.password =
spring.jpa.hibernate.ddl-auto = update
spring.datasource.driverClassName = com.mysql.cj.jdbc.Driver
spring.jpa.database-platform = org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql = true
```

Non è importante come il database sia in esecuzione, l'importante è che sia un database `MySQL`
e che si configurino opportunamente le proprietà `spring.datasource.url `,
`spring.datasource.username` e `spring.datasource.password`. 

Nel caso il database non fosse `MySQL`, cambiare anche la proprietà `spring.datasource.driverClassName`,
oltre alle tre precedenti.

### Visualizzare la URL per l'accesso all'applicazione durante lo startup
Creare un package `config` nel package `it.unisa.tiziano.pdtify`.
Nel package `config`, creare un file `StartupRunner.java` per la classe `StartupRunner` che
implementerà l'interfaccia `ApplicationRunner`.

Il metodo `run` definito dall'interfaccia `ApplicationRunner` e implementato in `StartupRunner`
verrà eseguito durante la fase di startup dell'applicazione e semplicemente aggiungerà un log 
nella console con la URL per l'accesso all'applicazione.

Aggiungere gli import necessari.

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
```

E implementare il metodo `run` definito dall'interfaccia `ApplicationRunner`.

L'annotazione `@Slf4j` è fornita da `Lombok` e permette di ottenere un'interfaccia di logging
senza configurarla esplicitamente, denominata `Simple Logging Facade for Java (SLF4J)`.

```java
// Simple Logging Facade for Java (SLF4J)
@Slf4j
@Component
public class StartupRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments arg0) {
        log.info("Application started at http://localhost:8080");
    }
}
```

L'annotazione `@Component` segnala all'application container di creare e gestire un bean creato come istanza
della classe `StartupRunner`.

### Avviare l'applicazione
Assicurarsi che il database sia raggiungibile e avviare l'applicazione da terminale con il comando:

```bash
mvn spring-boot:run
```
E accedere all'applicazione alla URL [http://localhost:8080](http://localhost:8080).

Non ci sono ancora pagine da visualizzare ma è possibile vedere nella console il log dello `StartupRunner`.

![Startup Runner Log](https://github.com/tizianocitro/pdtify/blob/main/assets/StartupRunner.png)

## Aggiungere lo strato di persistenza
Per gestire la persistenza dell'applicazione bisognerà aggiungere l'entità per le canzoni (`Song`)
e una repository per interagire con il database.

### Aggiungere l'entità
Creare un package `entity` nel package `it.unisa.tiziano.pdtify`.
Nel package `entity`, creare un file `Song.java` per la classe `Song` che modellerà una canzone nel database.

Aggiungere gli import necessari.

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.UUID;
```

Aggiungere i campi della classe che modellano la canzone. L'annotazione `@Entity` definisce una classe
come un'entità che verrà gestita dallo strato di persistenza di Spring Data JPA. L'annotazione
`@Id` definisce il campo `ID` di tipo UUID come chiave primaria. L'ID sarà generato
automaticamente al momento del salvataggio di una nuova entità, come specificato dall'annotazione `@GeneratedValue`.

Le annotazioni `@NoArgsConstructor`, `@Getter` e `@Setter` sono fornite da `Lombok` e permettono
l'autogenerazione di un costruttore senza argomenti e dei metodi getter e setter per i campi dell'entità.

```java
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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ID;

    private String authors;

    private String name;

    private URL url;

    private int views;
}
```

### Aggiungere la repository
Creare un package `repository` nel package `it.unisa.tiziano.pdtify`.
Nel package `repository`, creare un file `SongRepository.java` per l'interfaccia `SongRepository`
che fornirà un'astrazione per interagire con le entità istanze della classe `Song`.

Aggiungere gli import necessari.

```java
import it.unisa.tiziano.pdtify.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
```

Creare l'interfaccia e l'application container di Spring creerà automaticamente un bean
con una serie di metodi per interagire con le entità, ad esempio `save()`, `findById()` e così via.

Definire anche due metodi custom `findByName()` e `existsByName()` non definiti di base dall'astrazione
ma che verranno generati automaticamente nel bean creato,
per il solo fatto di essere definiti nell'interfaccia.

```Java
@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    Song findByName(String name);

    boolean existsByName(String name);
}
```

### Popolare il database allo startup dell'applicazione
Nel package `config`, creare un file `DatabasePopulator.java` per la classe `DatabasePopulator`
che allo startup dell'applicazione salverà due canzoni nel database.

Aggiungere gli import necessari.

```java
import it.unisa.tiziano.pdtify.entity.Song;
import it.unisa.tiziano.pdtify.repository.SongRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
```

Iniettare la repository `SongRepository` all'interno della classe `DatabasePopulator` per interagire con il database.
L'annotazione `@Autowired` definisce che i parametri del costruttore sono da fornire tramite
`dependency injection`. Quando il bean `DatabasePopulator` verrà creato, l'application container assicurerà
che il bean `SongRepository` sia già stato creato e che sia fornito al bean `DatabasePopulator`.

```java
@Component
public class DatabasePopulator {

    private final SongRepository songRepository;

    @Autowired
    public DatabasePopulator(SongRepository songRepository) {
        this.songRepository = songRepository;
    }
}
```

Aggiungiamo il metodo che si occuperà di salvare le canzoni nel database.
L'annotazione `@PostConstruct` segnala all'application container che il metodo `populate()`
dovrà essere eseguito subito dopo lo startup dell'applicazione.

```java
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
```

### Avviare l'applicazione
Assicurarsi che il database sia raggiungibile e avviare l'applicazione da terminale con il comando:

```bash
mvn spring-boot:run
```
E controllare che le due canzoni siano state salvate nel database.

## Aggiungere la logica di business
Creare un'interfaccia che fornirà tutti i metodi da implementare che sono necessari alla logica di business dell'applicazione.

Creare un package `service` nel package `it.unisa.tiziano.pdtify`.
Nel package `service`, creare un file `SongService.java` per l'interfaccia `SongService`
che fornisce un'astrazione per interagire con i servizi che poi la implementeranno
e da usare per la dependency injection.

```java
import it.unisa.tiziano.pdtify.entity.Song;

import java.util.List;

public interface SongService {

    List<Song> findAll();

    Song findByName(String name);

    void save(Song song);
}
```

A questo punto, creare un file `SongServiceImpl.java` per la classe che implementerà
l'interfaccia `SongService.java`.

Aggiungiamo gli import necessari.

```java
import it.unisa.tiziano.pdtify.entity.Song;
import it.unisa.tiziano.pdtify.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
```

La classe `SongServiceImp` dovrà implementare l'interfaccia `SongService`
e fornire un'implementazione per i metodi definiti. l'annotazione `@Service` si comporta allo stesso
modo dell'annotazione `@Component`.

Il servizio riceverà inoltre, tramite dependency injection, la repository per interagire con il database.

```java
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
}
```

## Aggiungere la visualizzazione di pagine
Creare un package `controller` nel package `it.unisa.tiziano.pdtify`.
Nel package `controller`, creare un file `SongController.java` per la classe `SongController`, che
sarà responsabile di ricevere le richieste, eseguire della logica e reindirizzare l'utente a una pagina dedicata.

Aggiungere gli import necessary.

```java
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
```

Usare l'annotazione `@Controller` per specificare che la classe si occuperà di ricevere le richieste
ricevute dall'applicazione e fornire in risposta le opportune pagine.

Il controller userà il servizio implementato per eseguire logica di business tra la ricezione della
richiesta e l'invio della visualizzazione in risposta.

```java
@Controller
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }
}
```

### Aggiungere la pagina Home
Aggiungere la prima pagina, che sarà la pagina `Home`. Creare la directory `templates`
(se non già presente) nella directory `src/main/resources` e creare un file `home.html` per la componente
seguente.

La pagina utilizza una variabile `songs` contenente le canzoni da visualizzare e che
sarà fornita dall'endpoint del `SongController` che la fornisce al client.

```html
<!DOCTYPE html>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>PDtify</title>
        <link
                href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                rel="stylesheet"
                integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
                crossorigin="anonymous">
    </head>
    <body>
        <nav class="navbar bg-primary">
            <div class="container-fluid">
                <a class="navbar-brand text-white" href="/">PDtify</a>
                <form class="justify-content-end">
                    <button class="btn btn-light" type="button">
                        <a class="text-decoration-none" href="/add-song">Add</a>
                    </button>
                </form>
            </div>
        </nav>
        <div class="container">
            <div class="row" style="margin-top: 24px">
                <div class="col-sm-4" th:each="song: ${songs}" style="margin-top: 12px">
                    <div class="card">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-9">
                                    <h5 class="card-title text-center text-capitalize fw-bold" th:text="${song.name}"/>
                                    <p class="card-text text-center text-capitalize text-body-secondary" th:text="'By ' + ${song.authors}"/>
                                    <p class="card-text text-center text-capitalize fw-semibold" th:text="${song.views} + ' views'"/>
                                </div>
                                <div class="col-3" style="margin-top: 24px">
                                    <a th:href="'/play?name=' + ${song.name}" class="btn btn-lg btn-primary">Play</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
```

Lo stile è aggiunto usando [Bootstrap](https://getbootstrap.com/) tramite CDN:

```html
<link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
    rel="stylesheet"
    integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
    crossorigin="anonymous">
```

Aggiungere ora l'endpoint nel controller `SongContoller` che fornirà la pagina all'utente.
L'annotazione `GetMapping` indica che l'endpoint risponderà alle richieste GET alla URL [http://localhost:8080](http://localhost:8080).

Il parametro `model` è iniettato dall'application container e permette
di fornire dati alla pagina, in questo caso le canzoni nella variabile `songs`.

```java
@GetMapping("")
public String home(Model model) {
    List<Song> songs = songService.findAll();
    model.addAttribute("songs", songs);
    return "home";
}
```

### Avviare l'applicazione
Assicurarsi che il database sia raggiungibile e avviare l'applicazione da terminale con il comando:

```bash
mvn spring-boot:run
```
E controllare che alla URL [http://localhost:8080](http://localhost:8080) venga visualizzata la pagina Home.

## Aggiungere la pagina per il `play` delle canzoni
La pagina Home permette di fare il play delle canzoni.
Per implementare questa funzionalità, aggiungere un nuovo endpoint nella classe `SongController`.

L'annotazione `GetMapping` indica che l'endpoint risponderà alle richieste GET alla URL [http://localhost:8080/play](http://localhost:8080/play).

L'annotazione `@RequestParam` permette di specificare un query param per l'endpoint,
in questo caso il nome della canzone da riprodurre.

```java
@GetMapping("/play")
public String play(@RequestParam(value = "name") String name, Model model) {
    Song song = songService.findByName(name);
    model.addAttribute("song", song);
    return "play";
}
```

Aggiungere adesso la pagina per il play delle canzoni,
creando un file `play.html` nella directory `src/main/resources/templates`.

```html
<!DOCTYPE html>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>PDtify</title>
        <link
                href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                rel="stylesheet"
                integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
                crossorigin="anonymous">
    </head>
    <body>
        <nav class="navbar bg-primary">
            <div class="container-fluid">
                <a class="navbar-brand text-white" href="/">PDtify</a>
                <form class="justify-content-end">
                    <button class="btn btn-light" type="button">
                        <a class="text-decoration-none" href="/">Home</a>
                    </button>
                </form>
            </div>
        </nav>
        <div class="container">
            <div class="row" style="margin-top: 24px">
                <h5 class="card-title text-center text-capitalize fw-bold" th:text="${song.authors} + ' - ' + ${song.name}"/>
            </div>
            <div class="row" style="margin-top: 24px">
                <iframe width="720" height="480" th:src="${song.url}" allowfullscreen></iframe>
            </div>
        </div>
        <p class="text-center fw-semibold" style="margin-top: 24px" th:text="${song.views} + ' views'"/>
    </body>
</html>
```

### Avviare l'applicazione
Assicurarsi che il database sia raggiungibile e avviare l'applicazione da terminale con il comando:
```bash
mvn spring-boot:run
```
E controllare che cliccando `play` su di una canzone, venga visualizzata la pagina di play
con il video di Youtube visualizzato in maniera embedded.

## Aggiungere la pagine per l'aggiunta di nuove canzoni
La pagina Home permette anche di aggiungere nuove canzoni. Per fare ciò c'è bisogno di altri
due endpoint dedicati da aggiungere nella classe `SongController`.

```java
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
```

In conclusione, la classe `SongController` apparirà come segue:

```java
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
```

Aggiungere adesso la pagina per l'aggiunta delle canzoni,
creando un file `addSong.html` nella directory `src/main/resources/templates`.

```html
<!DOCTYPE html>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>PDtify</title>
        <link
                href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                rel="stylesheet"
                integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
                crossorigin="anonymous">
    </head>
    <body>
        <nav class="navbar bg-primary">
            <div class="container-fluid">
                <a class="navbar-brand text-white" href="/">PDtify</a>
                <form class="justify-content-end">
                    <button class="btn btn-light" type="button">
                        <a class="text-decoration-none" href="/">Home</a>
                    </button>
                </form>
            </div>
        </nav>
        <div class="container">
            <div class="row" style="margin-top: 24px">
                <form action="/save-song" method="post" th:object="${song}">
                    <div class="mb-3">
                        <label for="song-name" class="col-form-label">Name</label>
                        <input type="text" class="form-control" id="song-name" th:field="*{name}"/>
                    </div>
                    <div class="mb-3">
                        <label for="song-authors" class="col-form-label">Authors</label>
                        <input type="text" class="form-control" id="song-authors" th:field="*{authors}"/>
                    </div>
                    <div class="mb-3">
                        <label for="song-url" class="col-form-label">URL</label>
                        <input type="text" class="form-control" id="song-url" th:field="*{url}"/>
                    </div>
                    <button type="submit" class="btn btn-primary">Add</button>
                </form>
            </div>
        </div>
    </body>
</html>
```

### Avviare l'applicazione
Assicurarsi che il database sia raggiungibile e avviare l'applicazione da terminale con il comando:

```bash
mvn spring-boot:run
```

E controllare che cliccando `Add` nella navbar della pagina Home,
venga visualizzata la pagina con il form per l'aggiunta di una nuova canzone.

Per inserire una nuova canzone sarà necessario specificare un nome, gli autori e il link dell'API di Youtube Embedded.

Provare ad aggiungere la canzone seguente:

```json
{
  "name": "Sweet Home Alabama",
  "authors": "Lynyrd Skynyrd",
  "url": "https://www.youtube.com/embed/RrmWFjnAP2E"
}
```

E controllare che nella pagina Home appaia la canzone appena aggiunta.

## Aggiungere il conteggio del numero di visualizzazioni delle canzoni
Implementare un filtro che intercetti le richieste all'endpoint con request path `/play` per incrementare
le visualizzazioni della canzone riprodotta.

Estendere la classe `Song.java` per aggiungere un metodo che permetta di incrementare il campo `views` di 1.

```java
public void addSingleView() {
    this.views += 1;
}
```

Aggiungere un nuovo metodo anche all'interfaccia `SongService.java` da implementare nella classe `SongServiceImpl.java`.

Nell'interfaccia `SongService`:

```java
void updateSongViewsByName(String name);
```

Nella classe `SongServiceImpl`:

```java
@Override
public void updateSongViewsByName(String name) {
    Song song = findByName(name);
    song.addSingleView();
    save(song);
}
```

Aggiungere quindi il filtro nel package `service` creando un file `SongViewsFilter.java` per la classe
`SongViewsFilter`, che estenderà la classe `OncePerRequestFilter`.

Aggiungiamo gli import necessari.

```java
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
```

E fare l'`override` dei metodi forniti da `OncePerRequestFilter`.

```java
@Slf4j
@Component
public class SongViewsFilter extends OncePerRequestFilter {

    private final SongService songService;

    @Autowired
    public SongViewsFilter(SongService songService) {
        this.songService = songService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String songName = request.getParameter("name");
        log.info(String.format("Updating views for %s song", songName));
        songService.updateSongViewsByName(songName);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/play");
    }
}
```

### Avviare l'applicazione
Assicurarsi che il database sia raggiungibile e avviare l'applicazione da terminale con il comando:

```bash
mvn spring-boot:run
```
E controllare che facendo il play di una canzone ne vengano incrementate le visualizzazioni.
