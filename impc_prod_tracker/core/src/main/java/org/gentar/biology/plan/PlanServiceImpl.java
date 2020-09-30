/******************************************************************************
 Copyright 2019 EMBL - European Bioinformatics Institute

 Licensed under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License. You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied. See the License for the specific
 language governing permissions and limitations under the
 License.
 */
package org.gentar.biology.plan;

import org.gentar.biology.plan.engine.PlanCreator;
import org.gentar.biology.plan.engine.PlanStateMachineResolver;
import org.gentar.biology.plan.filter.PlanFilter;
import org.gentar.exceptions.ForbiddenAccessException;
import org.gentar.exceptions.NotFoundException;
import org.gentar.audit.history.HistoryService;
import org.gentar.security.abac.ResourceAccessChecker;
import org.gentar.security.abac.spring.ContextAwarePolicyEnforcement;
import org.gentar.security.permissions.Actions;
import org.gentar.statemachine.ProcessEvent;
import org.gentar.statemachine.TransitionAvailabilityEvaluator;
import org.gentar.statemachine.TransitionEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.gentar.audit.history.History;
import org.gentar.biology.plan.engine.PlanUpdater;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PlanServiceImpl implements PlanService
{
    private final PlanRepository planRepository;
    private final ResourceAccessChecker<Plan> resourceAccessChecker;
    private final PlanUpdater planUpdater;
    private final HistoryService<Plan> historyService;
    private final PlanCreator planCreator;
    private final PlanStateMachineResolver planStateMachineResolver;
    private final TransitionAvailabilityEvaluator transitionAvailabilityEvaluator;
    private final ContextAwarePolicyEnforcement policyEnforcement;

    private static final String READ_PLAN_ACTION = "READ_PLAN";
    private static final String PLAN_NOT_EXISTS_ERROR =
        "The plan[%s] does not exist.";

    PlanServiceImpl(
        PlanRepository planRepository,
        ResourceAccessChecker<Plan> resourceAccessChecker,
        PlanUpdater planUpdater,
        HistoryService<Plan> historyService,
        PlanCreator planCreator,
        PlanStateMachineResolver planStateMachineResolver,
        TransitionAvailabilityEvaluator transitionAvailabilityEvaluator,
        ContextAwarePolicyEnforcement policyEnforcement)
    {
        this.planRepository = planRepository;
        this.resourceAccessChecker = resourceAccessChecker;
        this.planUpdater = planUpdater;
        this.historyService = historyService;
        this.planCreator = planCreator;
        this.planStateMachineResolver = planStateMachineResolver;
        this.transitionAvailabilityEvaluator = transitionAvailabilityEvaluator;
        this.policyEnforcement = policyEnforcement;
    }

    @Override
    public Plan getNotNullPlanByPin(String pin)
    throws NotFoundException
    {
        Plan plan = planRepository.findPlanByPin(pin);
        if (plan == null)
        {
            throw new NotFoundException(String.format(PLAN_NOT_EXISTS_ERROR, pin));
        }
        validateReadPermissions(plan);
        return getAccessChecked(plan);
    }

    @Override
    public List<Plan> getPlans(PlanFilter planFilter)
    {
        Specification<Plan> specifications = buildSpecificationsWithCriteria(planFilter);
        List<Plan> plans = planRepository.findAll(specifications);
        return getCheckedCollection(plans);
    }

    @Override
    public Page<Plan> getPageablePlans(Pageable page, PlanFilter planFilter)
    {
        Specification<Plan> specifications = buildSpecificationsWithCriteria(planFilter);
        Page<Plan> plans = planRepository.findAll(specifications, page);
        return plans;
    }

    private Specification<Plan> buildSpecificationsWithCriteria(PlanFilter planFilter)
    {
        Specification<Plan> specifications =
            Specification.where(PlanSpecs.withProjectTpns(planFilter.getTpns()))
                .and(Specification.where(PlanSpecs.withWorkUnitNames(planFilter.getWorkUnitNames())))
                .and(Specification.where(PlanSpecs.withWorkGroupNames(planFilter.getWorGroupNames())))
                .and(Specification.where(PlanSpecs.withStatusNames(planFilter.getStatusNames())))
                .and(Specification.where(
                    PlanSpecs.withSummaryStatusNames(planFilter.getSummaryStatusNames())))
                .and(Specification.where(PlanSpecs.withPins(planFilter.getPins())))
                .and(Specification.where(PlanSpecs.withTypeNames(planFilter.getPlanTypeNames())))
                .and(Specification.where(
                    PlanSpecs.withAttemptTypeNames(planFilter.getAttemptTypeNames())))
                .and(Specification.where(
                    PlanSpecs.withImitsMiAttempts(planFilter.getImitsMiAttemptIds())))
                .and(Specification.where(
                    PlanSpecs.withImitsPhenotypeAttempts(planFilter.getImitsPhenotypeAttemptIds())))
                .and(Specification.where(
                        PlanSpecs.withPhenotypingExternalRefs(planFilter.getPhenotypingExternalRefs())));
        return specifications;
    }

    @Override
    public Plan getPlanByPinWithoutCheckPermissions(String pin)
    {
        return planRepository.findPlanByPin(pin);
    }

    private List<Plan> getCheckedCollection(Collection<Plan> plans)
    {
        return plans.stream().map(this::getAccessChecked)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Plan getAccessChecked(Plan plan)
    {
        return (Plan) resourceAccessChecker.checkAccess(plan, READ_PLAN_ACTION);
    }

    @Override
    public History updatePlan(String pin, Plan plan)
    {
        Plan existingPlan = getNotNullPlanByPin(pin);
        Plan originalPlan = new Plan(existingPlan);
        return planUpdater.updatePlan(originalPlan, plan);
    }

    @Override
    public List<History> getPlanHistory(Plan plan)
    {
        return historyService.getHistoryByEntityNameAndEntityId(
            Plan.class.getSimpleName(), plan.getId());
    }

    @Override
    public Plan createPlan(Plan plan)
    {
        return planCreator.createPlan(plan);
    }

    @Override
    public List<TransitionEvaluation> evaluateNextTransitions(Plan plan)
    {
        List<ProcessEvent> transitions =
            planStateMachineResolver.getAvailableTransitionsByEntityStatus(plan);
        return transitionAvailabilityEvaluator.evaluate(transitions, plan);
    }

    @Override
    public ProcessEvent getProcessEventByName(Plan plan, String name)
    {
        return planStateMachineResolver.getProcessEventByActionName(plan, name);
    }

    private void validateReadPermissions(Plan plan)
    {
        try
        {
            policyEnforcement.checkPermission(plan, READ_PLAN_ACTION);
        }
        catch (AccessDeniedException ade)
        {
            throw new ForbiddenAccessException(Actions.READ, Plan.class.getSimpleName(), plan.getPin());
        }
    }
}
