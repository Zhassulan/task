package com.home.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RequestId {
    @JsonProperty("RequestId")
    private UUID id;
}
