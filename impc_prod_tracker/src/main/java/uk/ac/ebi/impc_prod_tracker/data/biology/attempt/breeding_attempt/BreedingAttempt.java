package uk.ac.ebi.impc_prod_tracker.data.biology.attempt.breeding_attempt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.ac.ebi.impc_prod_tracker.data.BaseEntity;
import uk.ac.ebi.impc_prod_tracker.data.biology.attempt.breeding_attempt.breeding_type.BreedingType;
import uk.ac.ebi.impc_prod_tracker.data.biology.attempt.breeding_attempt.deleter_strain.DeleterStrain;
import uk.ac.ebi.impc_prod_tracker.data.experiment.colony.Colony;
import uk.ac.ebi.impc_prod_tracker.data.experiment.plan.Plan;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
@Getter
@Setter
@Entity
public class BreedingAttempt extends BaseEntity
{
    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    private Plan plan;

    // TODO
    //@ManyToOne
    //@JoinColumn(name = "previous_breeding_outcome_id")
   // private BreedingOutcome previousBreedingOutcome;

    @ManyToOne
    @JoinColumn(name = "previous_breeding_colony_id")
    private Colony previousBreedingColony;

    @Column(name = "number_of_cre_matings_started")
    private Integer numberOfCreMatingsStarted;

    @Column(name = "number_of_cre_matings_sucessfull")
    private Integer numberOfCreMatingsSucessful;

    @ManyToOne
    private DeleterStrain deleterStain;

    @ManyToOne
    private BreedingType type;
}