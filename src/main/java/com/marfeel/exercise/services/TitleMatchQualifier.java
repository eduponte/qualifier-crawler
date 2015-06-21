package com.marfeel.exercise.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Created by eduardo.ponte on 19/06/2015.
 */
@Component
public class TitleMatchQualifier implements Qualifier {
    private static final Logger log = LoggerFactory.getLogger(TitleMatchQualifier.class);
    @Override
    public boolean isMarfeelizable(ResponseEntity<String> responseEntity) {
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return false;
        }
        Document doc = Jsoup.parse(responseEntity.getBody());
        final String title = doc.head().select("TITLE").text();
        return (title != null && title.matches("(?i:.*(NEWS|NOTICIAS).*)"));
    }
}
