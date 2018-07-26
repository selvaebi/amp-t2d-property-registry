/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.ampt2d.registry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.ac.ebi.ampt2d.registry.repositories.PhenotypeRepository;
import uk.ac.ebi.ampt2d.registry.repositories.PropertyRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PropertyRegistryServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhenotypeRepository phenotypeRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        phenotypeRepository.deleteAll();
        propertyRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.properties").exists());
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.phenotypes").exists());
    }

    private String postTestEntity(String uri, String content) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(uri).content(content))
                .andExpect(status().isCreated())
                .andReturn();

        return mvcResult.getResponse().getHeader("Location");
    }

    private String postTestPhenotype() throws Exception {
        String content = "{\"id\":\"BMI\"," + "\"phenotypeGroup\":\"ANTHROPOMETRIC\"}";

        return postTestEntity("/phenotypes", content);
    }

    @Test
    public void shouldCreatePhenotype() throws Exception {
        String location = postTestPhenotype();

        assert location.contains("/phenotypes/");
    }

    @Test
    public void shouldRetrievePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phenotypeGroup").value("ANTHROPOMETRIC"));
    }

    @Test
    public void shouldQueryPhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(get("/phenotypes/search/findByPhenotypeGroup?phenotypeGroup=ANTHROPOMETRIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..phenotypes").isArray())
                .andExpect(jsonPath("$..phenotypes.length()").value(1))
                .andExpect(jsonPath("$..phenotypes[0]..phenotype.href").value(location));
    }

    @Test
    public void shouldUpdatePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(put(location)
                .content("{\"id\":\"BMI\"," + "\"phenotypeGroup\":\"RENAL\"}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phenotypeGroup").value("RENAL"));
    }

    @Test
    public void shouldPartiallyUpdatePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(patch(location)
                .content("{\"phenotypeGroup\":\"RENAL\"}"))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phenotypeGroup").value("RENAL"));
    }

    @Test
    public void shouldDeletePhenotype() throws Exception {
        String location = postTestPhenotype();

        mockMvc.perform(delete(location))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound());
    }

    private String postTestProperty() throws Exception {
        String content = "{\"id\":\"CALL_RATE\"," +
                "\"type\":\"FLOAT\"," +
                "\"meaning\":\"CALL_RATE\"," +
                "\"description\":\"calling rate\"}";

        return postTestEntity("/properties", content);
    }

    @Test
    public void shouldCreateProperty() throws Exception {
        String location = postTestProperty();

        assert location.contains("/properties/");
    }

    @Test
    public void shouldRetrieveProperty() throws Exception {
        String location = postTestProperty();

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.type").value("FLOAT")).andExpect(
                jsonPath("$.meaning").value("CALL_RATE")).andExpect(
                jsonPath("$.description").value("calling rate"));
    }

    @Test
    public void shouldQueryProperties() throws Exception {
        postTestProperty();

        mockMvc.perform(
                get("/properties/search/findByType?type={type}", "FLOAT")).andExpect(
                status().isOk()).andExpect(
                jsonPath("$._embedded.properties[0].type").value("FLOAT")).andExpect(
                jsonPath("$._embedded.properties[0].meaning").value("CALL_RATE")).andExpect(
                jsonPath("$._embedded.properties[0].description").value("calling rate"));
    }

    @Test
    public void shouldUpdateProperty() throws Exception {
        String location = postTestProperty();

        mockMvc.perform(put(location).content(
                "{\"id\":\"CALL_RATE\"," +
                        "\"type\":\"DOUBLE\"," +
                        "\"meaning\":\"CALL_RATE\"," +
                        "\"description\":\"call rate\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.type").value("DOUBLE")).andExpect(
                jsonPath("$.meaning").value("CALL_RATE")).andExpect(
                jsonPath("$.description").value("call rate"));
    }

    @Test
    public void shouldPartiallyUpdateProperty() throws Exception {
        String location = postTestProperty();

        mockMvc.perform(
                patch(location).content("{\"type\": \"DOUBLE\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.type").value("DOUBLE")).andExpect(
                jsonPath("$.meaning").value("CALL_RATE")).andExpect(
                jsonPath("$.description").value("calling rate"));
    }

    @Test
    public void shouldDeleteProperty() throws Exception {
        String location = postTestProperty();

        mockMvc.perform(delete(location)).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }

}