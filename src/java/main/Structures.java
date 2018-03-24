/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author PC
 */
@Entity
@Table(name = "STRUCTURES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Structures.findAll", query = "SELECT s FROM Structures s")
    , @NamedQuery(name = "Structures.findByIp", query = "SELECT s FROM Structures s WHERE s.ip = :ip")
    , @NamedQuery(name = "Structures.findByMac", query = "SELECT s FROM Structures s WHERE s.mac = :mac")
    , @NamedQuery(name = "Structures.findByBssid", query = "SELECT s FROM Structures s WHERE s.bssid = :bssid")
    , @NamedQuery(name = "Structures.findBySsid", query = "SELECT s FROM Structures s WHERE s.ssid = :ssid")
    , @NamedQuery(name = "Structures.findByWtp", query = "SELECT s FROM Structures s WHERE s.wtp = :wtp")})
public class Structures implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "IP")
    private String ip;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "MAC")
    private String mac;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "BSSID")
    private String bssid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "SSID")
    private String ssid;
    @Column(name = "WTP")
    private Integer wtp;

    public Structures() {
    }

    public Structures(String ip) {
        this.ip = ip;
    }

    public Structures(String ip, String mac, String bssid, String ssid) {
        this.ip = ip;
        this.mac = mac;
        this.bssid = bssid;
        this.ssid = ssid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Integer getWtp() {
        return wtp;
    }

    public void setWtp(Integer wtp) {
        this.wtp = wtp;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ip != null ? ip.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Structures)) {
            return false;
        }
        Structures other = (Structures) object;
        if ((this.ip == null && other.ip != null) || (this.ip != null && !this.ip.equals(other.ip))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "main.Structures[ ip=" + ip + " ]";
    }
    
}
