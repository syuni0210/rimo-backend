package com.ansim.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "JRNY")
public class Jrny {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JRNY_ID")
    private Long jrnyId;

    @Column(name = "MMBR_ID")
    private Long mmbrId;

    @Column(name = "STRT_ADDRSS")
    private String strtAddrss;

    @Column(name = "END_ADDRSS")
    private String endAddrss;

    @Column(name = "STRT_LAT")
    private Double strtLat;

    @Column(name = "STRT_LNG")
    private Double strtLng;

    @Column(name = "END_LAT")
    private Double endLat;

    @Column(name = "END_LNG")
    private Double endLng;

    @Column(name = "PTH_TYP_CD")
    private String pthTypCd;

    @Column(name = "STTS_CD")
    private String sttsCd;

    @Column(name = "STRT_DT")
    private LocalDateTime strtDt;

    @Column(name = "END_DT")
    private LocalDateTime endDt;

    public Long getJrnyId() {
        return jrnyId;
    }

    public void setJrnyId(Long jrnyId) {
        this.jrnyId = jrnyId;
    }

    public Long getMmbrId() {
        return mmbrId;
    }

    public void setMmbrId(Long mmbrId) {
        this.mmbrId = mmbrId;
    }

    public String getStrtAddrss() {
        return strtAddrss;
    }

    public void setStrtAddrss(String strtAddrss) {
        this.strtAddrss = strtAddrss;
    }

    public String getEndAddrss() {
        return endAddrss;
    }

    public void setEndAddrss(String endAddrss) {
        this.endAddrss = endAddrss;
    }

    public Double getStrtLat() {
        return strtLat;
    }

    public void setStrtLat(Double strtLat) {
        this.strtLat = strtLat;
    }

    public Double getStrtLng() {
        return strtLng;
    }

    public void setStrtLng(Double strtLng) {
        this.strtLng = strtLng;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLng() {
        return endLng;
    }

    public void setEndLng(Double endLng) {
        this.endLng = endLng;
    }

    public String getPthTypCd() {
        return pthTypCd;
    }

    public void setPthTypCd(String pthTypCd) {
        this.pthTypCd = pthTypCd;
    }

    public String getSttsCd() {
        return sttsCd;
    }

    public void setSttsCd(String sttsCd) {
        this.sttsCd = sttsCd;
    }

    public LocalDateTime getStrtDt() {
        return strtDt;
    }

    public void setStrtDt(LocalDateTime strtDt) {
        this.strtDt = strtDt;
    }

    public LocalDateTime getEndDt() {
        return endDt;
    }

    public void setEndDt(LocalDateTime endDt) {
        this.endDt = endDt;
    }
}
