package com.marfeel.exercise.services;

import com.marfeel.exercise.domain.QualifiedSite;
import com.marfeel.exercise.domain.Site;

import java.util.concurrent.CompletableFuture;

/**
 * Created by eduardo.ponte on 18/06/2015.
 */
public interface SiteCheckerService {
    public CompletableFuture<QualifiedSite> check(Site site);
}