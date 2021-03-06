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
package org.gentar.biology.strain;

import org.gentar.Mapper;
import org.springframework.stereotype.Component;

@Component
public class StrainMapper implements Mapper<Strain, String>
{
    private final StrainService strainService;

    public StrainMapper(StrainService strainService)
    {
        this.strainService = strainService;
    }

    @Override
    public String toDto(Strain entity)
    {
        String name = null;
        if (entity != null)
        {
            name = entity.getName();
        }
        return name;
    }

    @Override
    public Strain toEntity(String strainName)
    {
        Strain strain = null;
        if (strainName != null)
        {
            strain = strainService.getStrainByNameFailWhenNotFound(strainName);
        }
        return strain;
    }
}
