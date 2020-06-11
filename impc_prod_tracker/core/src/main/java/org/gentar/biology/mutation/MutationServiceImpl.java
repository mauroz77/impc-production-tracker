package org.gentar.biology.mutation;

import org.gentar.exceptions.UserOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class MutationServiceImpl implements MutationService
{
    private MutationRepository mutationRepository;

    private static final String MUTATION_NOT_EXIST_ERROR = "Mutation %s does not exist.";

    public MutationServiceImpl(MutationRepository mutationRepository)
    {
        this.mutationRepository = mutationRepository;
    }

    @Override
    public Mutation getMutationByMinFailsIfNull(String min)
    {
        Mutation mutation = mutationRepository.findByMin(min);
        if (mutation == null)
        {
            throw new UserOperationFailedException(String.format(MUTATION_NOT_EXIST_ERROR, min));
        }
        return mutation;
    }

    @Override
    public Mutation createMutation(Mutation mutation)
    {
        Mutation createdMutation = mutationRepository.save(mutation);
        createdMutation.setMin(buildMin(createdMutation.getId()));
        return createdMutation;
    }

    private String buildMin(Long id)
    {
        String identifier = String.format("%0" + 12 + "d", id);
        identifier = "MIN:" + identifier;
        return identifier;
    }
}
