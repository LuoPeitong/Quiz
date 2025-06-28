package org.example.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Users {
    private Integer id;
    private String phone;
    private String nickname;
    private String company;
    private String gender;
    private LocalDateTime createAt;

}
