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
package uk.ac.ebi.ampt2d.registry.config.security;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.ac.ebi.ampt2d.registry.entities.User;
import uk.ac.ebi.ampt2d.registry.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomAuthoritiesExtractor implements AuthoritiesExtractor {

    private UserRepository userRepository;

    public CustomAuthoritiesExtractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        String email = (String) map.get("email");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User(email, User.Roles.ROLE_USER);
            userRepository.save(user);
            return Arrays.asList(new SimpleGrantedAuthority(User.Roles.ROLE_USER.name()));
        }
        return Arrays.asList(new SimpleGrantedAuthority(user.getRole().toString()));
    }
}
