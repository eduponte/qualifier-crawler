package com.marfeel.exercise.repository;

import com.marfeel.exercise.domain.QualifiedSite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by eduardo.ponte on 20/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-jpa-context.xml")
@WebAppConfiguration
public class QualifiedSiteRepositoryTest {
    @Autowired
    private QualifiedSiteRepository repository;

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void saveOne() {
        repository.save(new QualifiedSite("abracadabra.com",false,new Date()));
        assertTrue(repository.exists("abracadabra.com"));
        assertEquals(1,repository.count());
    }

    @Test
    public void findAll() {
        QualifiedSite qs0 = new QualifiedSite("abracadabra.com", false, new Date());
        QualifiedSite qs1 = new QualifiedSite("friendship4ever.org", false, new Date());
        repository.save(qs0);
        repository.save(qs1);

        List all = (List) repository.findAll();
        assertEquals(2,all.size());
        assertEquals(qs0.toString(), all.get(0).toString());
        assertEquals(qs1.toString(), all.get(1).toString());
    }

    @Test
    public void findMarfeelizable() {
        QualifiedSite qs0 = new QualifiedSite("abracadabra.com", false, new Date());
        QualifiedSite qs1 = new QualifiedSite("reallygoodnews.org", true, new Date());
        repository.save(qs0);
        repository.save(qs1);

        List marfeelizables = (List) repository.findByIsMarfeelizable(true);
        assertEquals(1,marfeelizables.size());
        assertEquals(qs1.toString(), marfeelizables.get(0).toString());
    }
}
