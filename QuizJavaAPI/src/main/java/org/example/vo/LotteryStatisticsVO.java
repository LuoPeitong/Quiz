package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LotteryStatisticsVO {

    private Summary summary;

    private List<CompanyWinner> companyWinners;

    private List<UserWinner>    userWinners;

    @Data
    public static class Summary {
        private int totalWinners;
    }

    @Data
    public static class CompanyWinner {
        private String company;
        private int    count;
    }

    @Data
    public static class UserWinner {
        private String company;
        private String nickname;
        @JsonProperty("prize_name")
        private String prizeName;
        @JsonProperty("prize_amt")
        private int    prizeAmt;
        @JsonProperty("draw_time")
        private String drawTime;
    }
}
