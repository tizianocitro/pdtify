package it.unisa.tiziano.pdtify.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments arg0) {
        log.info("Application started at http://localhost:8080");
    }
}
