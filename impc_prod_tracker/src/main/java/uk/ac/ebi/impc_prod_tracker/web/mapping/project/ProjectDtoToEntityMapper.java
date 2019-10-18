/*******************************************************************************
 * Copyright 2019 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.impc_prod_tracker.web.mapping.project;

import org.springframework.stereotype.Component;
import uk.ac.ebi.impc_prod_tracker.data.biology.privacy.Privacy;
import uk.ac.ebi.impc_prod_tracker.data.biology.project.Project;
import uk.ac.ebi.impc_prod_tracker.data.organization.consortium.Consortium;
import uk.ac.ebi.impc_prod_tracker.web.dto.project.ProjectDTO;
import uk.ac.ebi.impc_prod_tracker.web.mapping.EntityMapper;
import uk.ac.ebi.impc_prod_tracker.web.mapping.consortium.ConsortiumMapper;
import uk.ac.ebi.impc_prod_tracker.web.mapping.privacy.PrivacyMapper;
import java.util.Set;

/**
 * Class in charge of converting a {@link ProjectDTO} into a {@link Project}  entity.
 */
@Component
public class ProjectDtoToEntityMapper
{
    private EntityMapper entityMapper;
    private PrivacyMapper privacyMapper;
    private ConsortiumMapper consortiumMapper;

    public ProjectDtoToEntityMapper(
        EntityMapper entityMapper, PrivacyMapper privacyMapper, ConsortiumMapper consortiumMapper)
    {
        this.entityMapper = entityMapper;
        this.privacyMapper = privacyMapper;
        this.consortiumMapper = consortiumMapper;
    }

    public Project toEntity(ProjectDTO projectDTO)
    {
        Project project = entityMapper.toTarget(projectDTO, Project.class);
        setPrivacy(project, projectDTO);
        setConsortia(project, projectDTO);
        return project;
    }

    private void setPrivacy(Project project, ProjectDTO projectDTO)
    {
        Privacy privacy =  privacyMapper.toEntity(projectDTO.getPrivacyName());
        project.setPrivacy(privacy);
    }

    private void setConsortia(Project project, ProjectDTO projectDTO)
    {
        Set<Consortium> consortia = consortiumMapper.toEntities(projectDTO.getConsortiaNames());
        if (!consortia.isEmpty())
        {
            project.setConsortia(consortia);
        }
    }
}