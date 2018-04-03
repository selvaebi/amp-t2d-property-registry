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
import uk.ac.ebi.ampt2d.registry.repositories.PropertyRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistryServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        propertyRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.properties").exists());
    }

    @Test
    public void shouldCreateEntity() throws Exception {
        mockMvc.perform(post("/properties").content(
                "{\"id\":\"CALL_RATE\"," +
                        "\"type\":\"FLOAT\"," +
                        "\"meaning\":\"CALL_RATE\"," +
                        "\"description\":\"calling rate\"}")).andExpect(
                status().isCreated()).andExpect(
                header().string("Location", containsString("properties/")));
    }

    @Test
    public void shouldRetrieveEntity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/properties").content(
                "{\"id\":\"CALL_RATE\"," +
                        "\"type\":\"FLOAT\"," +
                        "\"meaning\":\"CALL_RATE\"," +
                        "\"description\":\"calling rate\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.type").value("FLOAT")).andExpect(
                jsonPath("$.meaning").value("CALL_RATE")).andExpect(
                jsonPath("$.description").value("calling rate"));
    }

    @Test
    public void shouldQueryEntity() throws Exception {
        mockMvc.perform(post("/properties").content(
                "{\"id\":\"CALL_RATE\"," +
                        "\"type\":\"FLOAT\"," +
                        "\"meaning\":\"CALL_RATE\"," +
                        "\"description\":\"calling rate\"}")).andExpect(
                status().isCreated());

        mockMvc.perform(
                get("/properties/search/findByType?type={type}", "FLOAT")).andExpect(
                status().isOk()).andExpect(
                jsonPath("$._embedded.properties[0].type").value("FLOAT")).andExpect(
                jsonPath("$._embedded.properties[0].meaning").value("CALL_RATE")).andExpect(
                jsonPath("$._embedded.properties[0].description").value("calling rate"));
    }

    @Test
    public void shouldUpdateEntity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/properties").content(
                "{\"id\":\"CALL_RATE\"," +
                        "\"type\":\"FLOAT\"," +
                        "\"meaning\":\"CALL_RATE\"," +
                        "\"description\":\"calling rate\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

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
    public void shouldPartiallyUpdateEntity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/properties").content(
                "{\"id\":\"CALL_RATE\"," +
                        "\"type\":\"FLOAT\"," +
                        "\"meaning\":\"CALL_RATE\"," +
                        "\"description\":\"calling rate\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(
                patch(location).content("{\"type\": \"DOUBLE\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.type").value("DOUBLE")).andExpect(
                jsonPath("$.meaning").value("CALL_RATE")).andExpect(
                jsonPath("$.description").value("calling rate"));
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/properties").content(
                "{\"id\":\"CALL_RATE\"," +
                        "\"type\":\"FLOAT\"," +
                        "\"meaning\":\"CALL_RATE\"," +
                        "\"description\":\"calling rate\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(delete(location)).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }
}