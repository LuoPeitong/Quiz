package org.example.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;

@Data
public class Questions {
    private Integer id;
    private String question;
    private String answer;
    private String type;
    private List<String> options;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void setOptions(String optionsStr) {
        try {
            this.options = objectMapper.readValue(optionsStr, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            this.options = null;
        }
    }
}
