package com.marfeel.exercise.controllers;

import com.marfeel.exercise.domain.Site;
import com.marfeel.exercise.domain.QualifiedSite;
import com.marfeel.exercise.repository.QualifiedSiteRepository;
import com.marfeel.exercise.services.SiteCheckerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by eduardo.ponte on 19/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-servlet-context.xml")
@WebAppConfiguration
public class SiteControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private SiteCheckerService siteChecker;

    @Autowired
    private QualifiedSiteRepository repository;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        reset(siteChecker);
    }

    @Test
    public void asyncStatusOk() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/status"))
                .andExpect(request().asyncStarted())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());
    }

    @Test
    public void postSitesNoWaitChecksNSites() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/sites/nowait").content("[{\"url\": \"forocoches.com\"},{\"url\": \"huesca.es\"}]").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(request().asyncStarted())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());

        Mockito.verify(siteChecker, Mockito.times(2)).check(Mockito.any(Site.class));
    }

    @Test
    public void postSitesNoWaitReturnsEnqueued() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/sites/nowait").content("[{\"url\": \"forocoches.com\"},{\"url\": \"huesca.es\"}]").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(request().asyncStarted())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("totalEnqueuedForProcessing").value(2));

    }

    @Test
    public void postSitesNoWaitBadRequest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/sites").content("{\"dumbField\": \"dumbdumb.com\"}").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postSitesReturnsRepoData() throws Exception {
        Date knownDate = new Date();
        when(siteChecker.check(any(Site.class))).thenReturn(CompletableFuture.completedFuture(new QualifiedSite("any", false, knownDate)));
        when(repository.findAll()).thenReturn(Arrays.<QualifiedSite>asList(
                        new QualifiedSite("forocoches.com", false, knownDate),
                        new QualifiedSite("huesca.es", true, knownDate))
        );

        MvcResult mvcResult = this.mockMvc.perform(post("/sites").content("[{\"url\": \"forocoches.com\"},{\"url\": \"huesca.es\"}]").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvcResult.getAsyncResult();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].url").value("forocoches.com"))
                .andExpect(jsonPath("$[0].isMarfeelizable").value(false))
                .andExpect(jsonPath("$[1].url").value("huesca.es"))
                .andExpect(jsonPath("$[1].isMarfeelizable").value(true));

    }

}
