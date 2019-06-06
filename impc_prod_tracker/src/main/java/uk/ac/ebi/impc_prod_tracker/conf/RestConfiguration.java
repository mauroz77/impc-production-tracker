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
package uk.ac.ebi.impc_prod_tracker.conf;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.RepresentationModelProcessorInvoker;
import org.springframework.web.client.RestTemplate;
import java.util.List;

/**
 * Configure needed beans for handling REST calls
 *
 * @author Mauricio Martinez
 */
@Configuration
public class RestConfiguration
{
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }

    @Bean
    RepresentationModelProcessorInvoker representationModelProcessorInvoker(
        List<RepresentationModelProcessor<?>> processors) {
        return new RepresentationModelProcessorInvoker(processors);
    }
}
