package org.gentar.biology.plan.attempt.phenotyping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PhenotypingAttemptCommonDTO
{
    @JsonIgnore
    private Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("imitsPhenotypeAttemptId")
    private Long imitsPhenotypeAttempt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("imitsPhenotypingProductionId")
    private Long imitsPhenotypingProduction;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("imitsParentColonyId")
    private Long imitsParentColony;

    private Boolean doNotCountTowardsCompleteness;

    private String phenotypingExternalRef;

    @JsonProperty("phenotypingBackgroundStrainName")
    private String strainName;
}
