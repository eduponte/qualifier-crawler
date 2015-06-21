package com.marfeel.exercise.services;

import com.marfeel.exercise.domain.QualifiedSite;
import com.marfeel.exercise.domain.Site;
import com.marfeel.exercise.repository.QualifiedSiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestOperations;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Created by eduardo.ponte on 18/06/2015.
 */
@Service
public class AsyncTitleMatchSiteCheckerService implements SiteCheckerService {
    private Qualifier qualifier;
    private AsyncRestOperations asyncRestTemplate;
    private QualifiedSiteRepository repository;
    private static final Logger log = LoggerFactory.getLogger(AsyncTitleMatchSiteCheckerService.class);

    @Autowired
    AsyncTitleMatchSiteCheckerService(Qualifier qualifier, AsyncRestOperations restTemplate, QualifiedSiteRepository repository) {
        this.qualifier = qualifier;
        this.asyncRestTemplate = restTemplate;
        this.repository = repository;
    }

    @Override
    public CompletableFuture<QualifiedSite> check(final Site site) {
        CompletableFuture<QualifiedSite> future = new CompletableFuture<QualifiedSite>();
        ListenableFuture<ResponseEntity<String>> siteHome = asyncRestTemplate.getForEntity("http://www." + site.getUrl(), String.class);
        log.info("Fetching {}", site.getUrl());
        siteHome.addCallback(
                new ListenableFutureCallback<ResponseEntity<String>>() {
                    @Override
                    public void onSuccess(ResponseEntity<String> responseEntity) {
                        QualifiedSite qualifiedSite = new QualifiedSite(site.getUrl(), qualifier.isMarfeelizable(responseEntity), new Date());
                        repository.save(qualifiedSite);
                        log.info("Got content for {} / isMarfeelizable: {}", site.getUrl(), qualifiedSite.getIsMarfeelizable());
                        future.complete(qualifiedSite);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        log.info("Failure for {} / isMarfeelizable: {}", site.getUrl(), false);
                        QualifiedSite qualifiedSite = new QualifiedSite(site.getUrl(), false, new Date());
                        repository.save(qualifiedSite);
                        future.complete(qualifiedSite);
                    }
                }
        );

        return future;
    }

//    @Deprecated
//    public ListenableFuture<QualifiedSite> check(final Site site) {
//        SettableListenableFuture<QualifiedSite> future = new SettableListenableFuture<>();
//        ListenableFuture<ResponseEntity<String>> siteHome = asyncRestTemplate.getForEntity("http://www." + site.getUrl(), String.class);
//        log.info("Fetching {}", site.getUrl());
//        siteHome.addCallback(
//                new ListenableFutureCallback<ResponseEntity<String>>() {
//                    @Override
//                    public void onSuccess(ResponseEntity<String> responseEntity) {
//                        QualifiedSite qualifiedSite = new QualifiedSite(site.getUrl(), qualifier.isMarfeelizable(responseEntity), new Date());
//                        repository.save(qualifiedSite);
//                        log.info("Got content for {} / isMarfeelizable: {}", site.getUrl(), qualifiedSite.getIsMarfeelizable());
//                        future.set(qualifiedSite);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        log.info("Failure for {} / isMarfeelizable: {}", site.getUrl(), false);
//                        QualifiedSite qualifiedSite = new QualifiedSite(site.getUrl(), false, new Date());
//                        repository.save(qualifiedSite);
//                        future.set(qualifiedSite);
//                    }
//                }
//        );
//
//        return future;
//    }
}
