package com.qualifier.async.repository;

import com.qualifier.async.domain.QualifiedSite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by eduardo.ponte on 20/06/2015.
 */
public interface QualifiedSiteRepository extends CrudRepository<QualifiedSite,String> {
    List<QualifiedSite> findByIsMarfeelizable(Boolean isMarfeelizable);
}
