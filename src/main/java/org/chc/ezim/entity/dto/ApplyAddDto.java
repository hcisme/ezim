package org.chc.ezim.entity.dto;

import jakarta.validation.constraints.NotEmpty;

public class ApplyAddDto {
    @NotEmpty
    public String contactId;

    public String applyInfo;
}

