package com.highestpeak.dimlight.model.params;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class TagParams {
    @NotEmpty
    private String name;

    private String desc;
}
