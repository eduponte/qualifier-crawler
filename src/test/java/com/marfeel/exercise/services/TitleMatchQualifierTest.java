package com.marfeel.exercise.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by eduardo.ponte on 19/06/2015.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TitleMatchQualifierTest {
    @Test
    public void matchesNews() throws Exception {
        assertTrue(new TitleMatchQualifier().isMarfeelizable(new ResponseEntity<String>(
                "<html>\n" +
                        " <head>\n" +
                        "  <title>my news site</title>" +
                        " </head>" +
                        "<html>", HttpStatus.OK)));
    }

    @Test
    public void matchesNoticias() throws Exception {
        assertTrue(new TitleMatchQualifier().isMarfeelizable(new ResponseEntity<String>(
                "<html>\n" +
                        " <head>\n" +
                        "  <title>mis noticias</title>" +
                        " </head>" +
                        "<html>", HttpStatus.OK)));
    }

    @Test
    public void matchesCaseInsensitive() throws Exception {
        assertTrue(new TitleMatchQualifier().isMarfeelizable(new ResponseEntity<String>(
                "<html>\n" +
                        " <head>\n" +
                        "  <title>mis NoTiCias</title>" +
                        " </head>" +
                        "<html>", HttpStatus.OK)));
    }

    @Test
    public void titleNotMatches() throws Exception {
        assertFalse(new TitleMatchQualifier().isMarfeelizable(new ResponseEntity<String>(
                "<html>\n" +
                        " <head>\n" +
                        "  <title>Ecommerce Shop</title>" +
                        " </head>" +
                        "<html>", HttpStatus.OK)));
    }

    @Test
    public void noTitle() throws Exception {
        assertFalse(new TitleMatchQualifier().isMarfeelizable(new ResponseEntity<String>(
                "<html>\n" +
                        " <head>\n" +
                        " </head>" +
                        "<html>", HttpStatus.OK)));
    }

    @Test
    public void notFound() throws Exception {
        assertFalse(new TitleMatchQualifier().isMarfeelizable(new ResponseEntity<String>(
                "whatever content", HttpStatus.NOT_FOUND)));
    }

    @Test
    public void forbidden() throws Exception {
        assertFalse(new TitleMatchQualifier().isMarfeelizable(new ResponseEntity<String>(
                "whatever content", HttpStatus.FORBIDDEN)));
    }

}
