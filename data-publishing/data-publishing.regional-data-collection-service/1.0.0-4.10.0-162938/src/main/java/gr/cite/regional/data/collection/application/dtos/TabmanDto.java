package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TabmanDto implements Dto {
    @JsonProperty("agency")
    private String agency;

    @JsonProperty("validFrom")
    private Date validFrom;

    @JsonProperty("validUntiTo")
    private Date validUntiTo;

    @JsonProperty("description")
    private String description;

    @JsonProperty("rights")
    private String rights;

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getValidUntiTo() {

        return validUntiTo;
    }

    public void setValidUntiTo(Date validUntiTo) {
        this.validUntiTo = validUntiTo;
    }

    public Date getValidFrom() {

        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public String getAgency() {

        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }
}
