package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuizStatisticsVO {

    private Summary summary;

    private List<CompanyStat> companyStats;

    private List<UserStat>    userStats;

    @Data
    public static class Summary {
        private int total;
    }

    @Data
    public static class CompanyStat {
        private String company;
        private int    count;
    }

    @Data
    public static class UserStat {
        private String company;
        private String nickname;
        private int    score;
        @JsonProperty("time_used")
        private int    timeUsed;
        @JsonProperty("submit_time")
        private String submitTime;
    }
}
