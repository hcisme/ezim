package org.chc.ezim.entity.dto;

import jakarta.validation.constraints.NotNull;

public class DealApplyDto {
    @NotNull
    public Integer applyId;

    @NotNull
    public Integer status;
}
