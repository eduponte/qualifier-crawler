package com.marfeel.exercise.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marfeel.exercise.domain.Site;
import com.marfeel.exercise.domain.QualifiedSite;
import com.marfeel.exercise.repository.QualifiedSiteRepository;
import com.marfeel.exercise.services.SiteCheckerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by eduardo.ponte on 18/06/2015.
 */
@Controller
class SiteController {
    private static final Logger log = LoggerFactory.getLogger(SiteController.class);

    @Autowired
    private SiteCheckerService siteChecker;

    @Autowired
    private QualifiedSiteRepository qualifiedSiteRepository;

    @RequestMapping(value = "/sites/nowait", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    DeferredResult<ResponseEntity<?>> sites(@RequestBody List<Site> sites) {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        int enqueued = 0;
        if (sites != null) {
            for (Site site : sites) {
                if (site != null && site.getUrl() != null) {
                    siteChecker.check(site);
                    enqueued ++;
                }
            }
        }
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("totalEnqueuedForProcessing",enqueued);
        deferredResult.setResult(ResponseEntity.ok(node));
        return deferredResult;
    }

    @RequestMapping(value = "/sites", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    DeferredResult<ResponseEntity<?>> sitesWait(@RequestBody List<Site> sites) {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        List<CompletableFuture<QualifiedSite>> futures = new ArrayList<>();
        if (sites != null) {
            for (Site site : sites) {
                if (site != null && site.getUrl() != null) {
                    futures.add(siteChecker.check(site));
                }
            }
        }
        CompletableFuture<QualifiedSite>[] s = new CompletableFuture[futures.size()];
        CompletableFuture.allOf(futures.toArray(s)).thenApplyAsync((v) -> {
            JsonNode node = JsonNodeFactory.instance.pojoNode(qualifiedSiteRepository.findAll());
            deferredResult.setResult(ResponseEntity.ok(node));
            return null;
        });
        return deferredResult;
    }

    @RequestMapping(value = "/qsites", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    DeferredResult<ResponseEntity<?>> qualifiedSites() {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        JsonNode node = JsonNodeFactory.instance.pojoNode(qualifiedSiteRepository.findAll());
        deferredResult.setResult(ResponseEntity.ok(node));
        return deferredResult;
    }

    @RequestMapping(value = "/marfeelizable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    DeferredResult<ResponseEntity<?>> sites() {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        JsonNode node = JsonNodeFactory.instance.pojoNode(qualifiedSiteRepository.findByIsMarfeelizable(true));
        deferredResult.setResult(ResponseEntity.ok(node));
        return deferredResult;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    DeferredResult<ResponseEntity<?>> status() {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        deferredResult.setResult(ResponseEntity.ok().build());
        return deferredResult;
    }
}