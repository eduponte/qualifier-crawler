package com.marfeel.exercise.services;

import com.marfeel.exercise.domain.QualifiedSite;
import com.marfeel.exercise.domain.Site;
import com.marfeel.exercise.repository.QualifiedSiteRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by eduardo.ponte on 19/06/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncTitleMatchSiteCheckerServiceTest {
    @Mock
    private Qualifier qualifier;
    @Mock
    private AsyncRestTemplate restTemplate;
    @Mock
    private QualifiedSiteRepository repository;

    private AsyncTitleMatchSiteCheckerService siteChecker;

    private Site site;

    @Before
    public void setUp() {
        siteChecker = new AsyncTitleMatchSiteCheckerService(qualifier, restTemplate, repository);
        site = new Site();
        site.setUrl("brave.com");
    }

    @Test
    public void fetchesSite() throws Exception {
        SettableListenableFuture myPage = new SettableListenableFuture();
        myPage.set(new ResponseEntity<String>(HttpStatus.OK));

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(myPage);
        Mockito.when(qualifier.isMarfeelizable(Mockito.any())).thenReturn(true);

        // "block" just for testing
        QualifiedSite qualifiedSite = siteChecker.check(site).get();

        Mockito.verify(restTemplate).getForEntity("http://www.brave.com", String.class);
    }

    @Test
    public void callsQualifier() throws Exception {
        SettableListenableFuture myPage = new SettableListenableFuture();
        myPage.set(new ResponseEntity<String>(HttpStatus.OK));

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(myPage);
        Mockito.when(qualifier.isMarfeelizable(Mockito.any())).thenReturn(true);

        // "block" just for testing
        QualifiedSite qualifiedSite = siteChecker.check(site).get();

        Mockito.verify(qualifier).isMarfeelizable(Mockito.any(ResponseEntity.class));
    }

    @Test
    public void returnsQualificationWrappedInFuture() throws Exception {
        SettableListenableFuture myPage = new SettableListenableFuture();
        myPage.set(new ResponseEntity<String>(HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(myPage);
        Mockito.when(qualifier.isMarfeelizable(Mockito.any())).thenReturn(true);

        Future<QualifiedSite> qualifiedSiteFuture = siteChecker.check(site);

        assertTrue(qualifiedSiteFuture instanceof Future);

        // "block" just for testing
        QualifiedSite qualifiedSite = qualifiedSiteFuture.get();
        assertEquals(site.getUrl(), qualifiedSite.getUrl());
        assertTrue(qualifiedSite.getIsMarfeelizable());
    }

    @Test
    public void persistQualification() throws Exception {
        SettableListenableFuture myPage = new SettableListenableFuture();
        myPage.set(new ResponseEntity<String>(HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(myPage);
        Mockito.when(qualifier.isMarfeelizable(Mockito.any())).thenReturn(true);

        Future<QualifiedSite> qualifiedSiteFuture = siteChecker.check(site);

        assertTrue(qualifiedSiteFuture instanceof Future);

        // "block" just for testing
        QualifiedSite qualifiedSite = qualifiedSiteFuture.get();
        assertEquals(site.getUrl(), qualifiedSite.getUrl());
        assertTrue(qualifiedSite.getIsMarfeelizable());
    }

    @Test
    public void failureQualifiesAsFalse() throws Exception {
        SettableListenableFuture myPage = new SettableListenableFuture();
        myPage.setException(new RuntimeException());
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(myPage);
        Mockito.when(qualifier.isMarfeelizable(Mockito.any())).thenReturn(true);

        // "block" just for testing
        QualifiedSite qualifiedSite = siteChecker.check(site).get();
        assertEquals(site.getUrl(), qualifiedSite.getUrl());
        assertFalse(qualifiedSite.getIsMarfeelizable());
    }
}
