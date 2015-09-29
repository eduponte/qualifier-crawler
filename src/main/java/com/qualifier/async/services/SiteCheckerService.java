package com.qualifier.async.services;

import com.qualifier.async.domain.QualifiedSite;
import com.qualifier.async.domain.Site;

import java.util.concurrent.CompletableFuture;

/**
 * Created by eduardo.ponte on 18/06/2015.
 */
public interface SiteCheckerService {
    public CompletableFuture<QualifiedSite> check(Site site);
}