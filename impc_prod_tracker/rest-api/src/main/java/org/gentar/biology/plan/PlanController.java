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

import org.gentar.audit.history.History;
import org.gentar.audit.history.HistoryMapper;
import org.gentar.biology.ChangeResponse;
import org.gentar.biology.plan.filter.PlanFilter;
import org.gentar.biology.plan.filter.PlanFilterBuilder;
import org.gentar.biology.plan.mappers.PlanCreationMapper;
import org.gentar.biology.plan.mappers.PlanMapper;
import org.gentar.biology.plan.mappers.PlanResponseMapper;
import org.gentar.biology.project.ProjectService;
import org.gentar.common.history.HistoryDTO;
import org.gentar.helpers.LinkUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins="*")
public class PlanController
{
    private HistoryMapper historyMapper;
    private PlanService planService;
    private PlanMapper planMapper;
    private PlanCreationMapper planCreationMapper;
    private PlanResponseMapper planResponseMapper;
    private UpdatePlanRequestProcessor updatePlanRequestProcessor;
    private ProjectService projectService;

    public PlanController(
        HistoryMapper historyMapper,
        PlanService planService,
        PlanMapper planMapper,
        PlanCreationMapper planCreationMapper,
        PlanResponseMapper planResponseMapper,
        UpdatePlanRequestProcessor updatePlanRequestProcessor, ProjectService projectService)
    {
        this.historyMapper = historyMapper;
        this.planService = planService;
        this.planMapper = planMapper;
        this.planCreationMapper = planCreationMapper;
        this.planResponseMapper = planResponseMapper;
        this.updatePlanRequestProcessor = updatePlanRequestProcessor;
        this.projectService = projectService;
    }

    /**
     * Creates a new project in the system.
     * @param planCreationDTO Request with data of the plan to be created.
     * @return {@link PlanDTO} representing the plan created in the system.
     */
    @PostMapping
    public ChangeResponse createPlan(@RequestBody PlanCreationDTO planCreationDTO)
    {
        Plan planToBeCreated = planCreationMapper.toEntity(planCreationDTO);
        projectService.associatePlanToProject(
            planToBeCreated, projectService.getNotNullProjectByTpn(planCreationDTO.getTpn()));
        Plan planCreated = planService.createPlan(planToBeCreated);
        return buildChangeResponse(planCreated);
    }

    /**
     * Get all the plans in the system.
     * @return A collection of {@link PlanDTO} objects.
     */
    @GetMapping
    public ResponseEntity findAll(
        Pageable pageable,
        PagedResourcesAssembler assembler,
        @RequestParam(value = "projectTpns", required = false) List<String> projectTpns,
        @RequestParam(value = "workUnitNames", required = false) List<String> workUnitNames,
        @RequestParam(value = "workGroupNames", required = false) List<String> workGroupNames,
        @RequestParam(value = "summaryStatusNames", required = false) List<String> summaryStatusNames,
        @RequestParam(value = "pins", required = false) List<String> pins,
        @RequestParam(value = "typeNames", required = false) List<String> typeNames,
        @RequestParam(value = "attemptTypeNames", required = false) List<String> attemptTypeNames,
        @RequestParam(value = "imitsMiAttemptIds", required = false) List<String> imitsMiAttempts,
        @RequestParam(value = "imitsPhenotypeAttemptIds", required = false) List<String> imitsPhenotypeAttempts)
    {
        PlanFilter planFilter = PlanFilterBuilder.getInstance()
            .withTpns(projectTpns)
            .withWorkUnitNames(workUnitNames)
            .withWorkGroupNames(workGroupNames)
            .withSummaryStatusNames(summaryStatusNames)
            .withPins(pins)
            .withPlanTypeNames(typeNames)
            .withAttemptTypeNames(attemptTypeNames)
            .withImitsMiAttemptIds(imitsMiAttempts)
            .withImitsPhenotypeAttemptIds(imitsPhenotypeAttempts)
            .build();
        Page<Plan> plans = planService.getPageablePlans(pageable, planFilter);
        Page<PlanResponseDTO> planDTOSPage = plans.map(this::getDTO);

        PagedModel pr =
            assembler.toModel(
                planDTOSPage,
                linkTo(PlanController.class).withSelfRel());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Link", LinkUtil.createLinkHeader(pr));

        return new ResponseEntity<>(pr, responseHeaders, HttpStatus.OK);
    }

    private PlanResponseDTO getDTO(Plan plan)
    {
        PlanResponseDTO planResponseDTO = new PlanResponseDTO();
        if (plan != null)
        {
            planResponseDTO = planResponseMapper.toDto(plan);
        }
        return planResponseDTO;
    }

    /**
     * Get a specific plan.
     * @param pin Plan identifier.
     * @return Entity with the plan information.
     */
    @GetMapping(value = {"/{pin}"})
    public EntityModel<?> findOne(@PathVariable String pin)
    {
        EntityModel<PlanResponseDTO> entityModel;
        Plan plan = planService.getNotNullPlanByPin(pin);
        PlanResponseDTO planResponseDTO = getDTO(plan);
        entityModel = new EntityModel<>(planResponseDTO);
        return entityModel;
    }

    @GetMapping(value = {"{pin}/history"})
    public List<HistoryDTO> getPlanHistory(@PathVariable String pin)
    {
        Plan plan = getNotNullPlanByPin(pin);

        return historyMapper.toDtos(planService.getPlanHistory(plan));
    }

    private Plan getNotNullPlanByPin(String pin)
    {
        Plan plan = planService.getNotNullPlanByPin(pin);
        return plan;
    }

    @PutMapping(value = {"/{pin}"})
    public HistoryDTO updatePlan(@PathVariable String pin, @RequestBody PlanDTO planDTO)
    {
        Plan plan = getPlanToUpdate(pin, planDTO);
        History history = planService.updatePlan(pin, plan);
        HistoryDTO historyDTO = new HistoryDTO();
        if (history != null)
        {
            historyDTO = historyMapper.toDto(history);
        }
        return historyDTO;
    }

    private Plan getPlanToUpdate(String pin, PlanDTO planDTO)
    {
        Plan currentPlan = getNotNullPlanByPin(pin);
        Plan newPlan = new Plan(currentPlan);
        return updatePlanRequestProcessor.getPlanToUpdate(newPlan, planDTO);
    }

    private ChangeResponse buildChangeResponse(Plan plan)
    {
        List<HistoryDTO> historyList = historyMapper.toDtos(planService.getPlanHistory(plan));
        return buildChangeResponse(plan, historyList);
    }
    private ChangeResponse buildChangeResponse(Plan pl, List<HistoryDTO> historyList)
    {
        ChangeResponse changeResponse = new ChangeResponse();
        changeResponse.setHistoryDTOs(historyList);

        changeResponse.add(linkTo(PlanController.class).slash(pl.getPin()).withSelfRel());
        return changeResponse;
    }
}
