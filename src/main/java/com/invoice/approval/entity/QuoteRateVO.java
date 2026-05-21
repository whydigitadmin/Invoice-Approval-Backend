package com.invoice.approval.entity;

import javax.persistence.*;

@Entity
@Table(name = "GST_QUOTERATE")
public class QuoteRateVO {
    
    @Id
    @Column(name = "GST_QUOTERATEID")
    private Long id;
    
    @Column(name = "AIRRATE")
    private Double airRate;
    
    @Column(name = "POD")
    private String pod;
    
    @Column(name = "POL")
    private String pol;
    
    @Column(name = "SEARATE")
    private Double seaRate;
    
    // Constructors
    public QuoteRateVO() {
    }
    
    public QuoteRateVO(Long id, Double airRate, String pod, String pol, Double seaRate) {
        this.id = id;
        this.airRate = airRate;
        this.pod = pod;
        this.pol = pol;
        this.seaRate = seaRate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Double getAirRate() { return airRate; }
    public void setAirRate(Double airRate) { this.airRate = airRate; }
    
    public String getPod() { return pod; }
    public void setPod(String pod) { this.pod = pod; }
    
    public String getPol() { return pol; }
    public void setPol(String pol) { this.pol = pol; }
    
    public Double getSeaRate() { return seaRate; }
    public void setSeaRate(Double seaRate) { this.seaRate = seaRate; }
    
    @Override
    public String toString() {
        return "QuoteRateVO{" +
                "id=" + id +
                ", airRate=" + airRate +
                ", pod='" + pod + '\'' +
                ", pol='" + pol + '\'' +
                ", seaRate=" + seaRate +
                '}';
    }
}