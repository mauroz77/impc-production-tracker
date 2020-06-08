package org.gentar.biology.mutation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.gentar.biology.gene.GeneDTO;
import java.util.List;

@Data
@RequiredArgsConstructor
public class MutationDTO
{
    private Long id;
    private String mgiAlleleId;
    private String mgiAlleleSymbol;
    private Boolean mgiAlleleSymbolRequiresConstruction;
    private Boolean mgiAlleleSymbolWithoutImpcAbbreviation;
    private String geneticMutationTypeName;
    private String molecularMutationTypeName;
    private String genbankFileName;
    private String description;
    private String autoDescription;
    private Long imitsAllele;
    private Boolean alleleConfirmed;
    private String alleleSymbolSuperscriptTemplate;

    @JsonProperty("mutationQcResults")
    private List<MutationQCResultDTO> mutationQCResultDTOs;

    @JsonProperty("genes")
    private List<GeneDTO> geneDTOS;

    @JsonProperty("mutationSequences")
    private List<MutationSequenceDTO> mutationSequenceDTOS;

    @JsonProperty("mutationCategorizations")
    private List<MutationCategorizationDTO> mutationCategorizationDTOS;
}
