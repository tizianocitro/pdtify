# PDtify
Progetto in Spring Boot per la gestione di una playlist utilizzando come player di riproduzione
le API di Youtube Embedded nell'ambito del corso di Programmazione Distribuita 
all'Università degli Studi di Salerno.

## Setup progetto
Scaricare il file [pdtify.zip](https://github.com/tizianocitro/pdtify/blob/main/pdtify.zip) oppure
creare un nuovo progetto tramite [Spring Initializer](https://start.spring.io/) come nell'immagine seguente:

![Project setup in Spring Initializer](https://github.com/tizianocitro/pdtify/blob/main/assets/SpringInitializer.png)

> Sostituire `tiziano` con il proprio nome.

## Configurare l'applicazione

### File pom.xml
Aggiungere nel file `pom.xml` le dipendenze per `Lombok` e il connector per `mysql`, usato come database.

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

# Livello di logging che specifica la tipologia di informazioni
# visualizzate nella console durante l'esecuzione 
logging.level.root = INFO

# Configurazioni specifiche per il database utilizzato
spring.datasource.url = jdbc:mysql://localhost:3306/pdtify?createDatabaseIfNotExist=true
spring.datasource.username = root
spring.datasource.password =
spring.jpa.hibernate.ddl-auto = update
spring.datasource.driverClassName = com.mysql.cj.jdbc.Driver
spring.jpa.show-sql = true
```

Non è importante coem il database è in esecuzione, l'importante è che sia un database `mysql`
e configurare opportunamente le proprietà `spring.datasource.url `,
`spring.datasource.username` e `spring.datasource.password`. 

Nel caso il database non fosse `mysql`, cambiare anche la proprietà `spring.datasource.driverClassName`,
oltre alle tre precedenti.

### Aggiungere un runner per visualizzare l'endpoint dell'applicazione
Creare un package `config` nel package `it.unisa.tiziano.pdtify`.
Nel package `config`, creare un file `StartupRunner.java` per la classe `StartupRunner` che
implementa l'interfaccia `ApplicationRunner`. Il metodo `run` di questa classe verrà eseguito
durante la fase di startup dell'applicazione e semplicemente aggiungerà un log 
nella console con l'endpoint di ascolto.

Aggiungere gli import necessari.
```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
```

E implementare il metodo `run` definito dall'interfaccia `ApplicationRunner`.
```java
@Slf4j
@Component
public class StartupRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments arg0) {
        log.info("Application started at http://localhost:8080");
    }
}
```

L'annotazione `@Component` rende lo `StartupRunner` un bean che verrà gestito dall'application container
di Spring e utilizzabile per la dependency injection.

### Avviare l'applicazione
Assicurarsi che il database sia raggiungibile e avviare l'applicazione da terminale con il comando:
```bash
mvn spring-boot:run
```
E collegarsi all'endpoint locale [http://localhost:8080](http://localhost:8080).
Ad ora non abbiamo ancora da visualizzare ma possiamo vedere nella console il log dello `StartupRunner`.

![Startup Runner Log](https://github.com/tizianocitro/pdtify/blob/main/assets/StartupRunner.png)

## Aggiungere lo strato di persistenza
Per gestire la persistenza dell'applicazione dovremo aggiungere l'entità per le canzoni (`Song`)
e una repository per interagire con il database.

### Aggiungere l'entità
Creare un package `entity` nel package `it.unisa.tiziano.pdtify`.
Nel package `entity`, creare un file `Song.java` per la classe `Song` che modella una canzone nel database.

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
`@Id` definisce che la chiave primaria è il campo `ID` di tipo UUID, che sarà generato
automaticamente al momento di salvare una nuova entità, come specificato dall'annotazione `@GeneratedValue`.

Le annotazioni `@NoArgsConstructor`, `@Getter` e `@Setter` sono fornite da Lombok e permettono
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
che fornisce un'astrazione per interagire con le entità istanze della classe `Song`.

Aggiungere gli import necesari.
```java
import it.unisa.tiziano.pdtify.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
```

Sviluppare l'interfaccia e l'application container di Spring creerà automaticamente un bean
con una serie di metodi per interagire con le entità, ad esempio `save()`, `findById()` e così via.
Definire anche due metodi custom non definiti di base dal'astrazione
ma che verranno generati automaticamente nel bean creato,
per il solo fatto di essere definiti nell'interfaccia.
```Java
@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    Song findByName(String name);

    boolean existsByName(String name);
}
```

### Popolare il databse allo startup dell'applicazione
Nel package `config`, creare un file `DatabasePopulator.java` per la classe `DatabasePopulator`
che allo startup dell'applicazione salva due canzoni nel database.

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

Iniettare la repository all'interno della classe `DatabasePopulator` per interagire con il database.
L'annotazione `@Autowired` definisce che i parametri del costruttore sono da fornire tramite
`dependency injection`. Quando il bean `DatabasePopulator` viene creato, l'application container assicura
che il bean della `SongRepository` sia già stato creato e che venga fornito al `DatabasePopulator`.
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
E controlalre che le due canzoni siano state salvate nel database.

## Aggiungere la logica di business
