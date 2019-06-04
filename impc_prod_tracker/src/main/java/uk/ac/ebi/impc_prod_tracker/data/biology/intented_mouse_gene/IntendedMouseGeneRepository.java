package uk.ac.ebi.impc_prod_tracker.data.biology.intented_mouse_gene;

import org.springframework.data.repository.CrudRepository;
import java.util.Collection;
import java.util.List;

public interface IntendedMouseGeneRepository extends CrudRepository<IntendedMouseGene, Long>
{
    List<IntendedMouseGene> findAllBySymbolIn(Collection<String> symbols);
}
