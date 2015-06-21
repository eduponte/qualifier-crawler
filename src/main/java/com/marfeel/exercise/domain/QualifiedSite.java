package com.marfeel.exercise.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by eduardo.ponte on 18/06/2015.
 */
@Entity
public class QualifiedSite {
    @Id
    private String url;
    private Boolean isMarfeelizable;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") // ISO-8601
    private Date lastScanned;

    protected QualifiedSite() {
    }

    public QualifiedSite(String url, Boolean isMarfeelizable, Date lastScanned) {
        this.url = url;
        this.isMarfeelizable = isMarfeelizable;
        this.lastScanned = lastScanned;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsMarfeelizable() {
        return isMarfeelizable;
    }

    public void setIsMarfeelizable(Boolean isMarfeelizable) {
        this.isMarfeelizable = isMarfeelizable;
    }

    public Date getLastScanned() {
        return lastScanned;
    }

    public void setLastScanned(Date lastScanned) {
        this.lastScanned = lastScanned;
    }

    @Override
    public String toString() {
        return "QualifiedSite{" +
                "url='" + url + '\'' +
                ", isMarfeelizable=" + isMarfeelizable +
                ", lastScanned=" + lastScanned.getTime() +
                '}';
    }
}
