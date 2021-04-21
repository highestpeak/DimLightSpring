package com.highestpeak.dimlight.model.dto;

import com.highestpeak.dimlight.model.enums.TaskEnum;
import com.highestpeak.dimlight.model.params.BaseTaskParams;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDto {
    private BaseTaskParams baseTaskParams;
    private TaskEnum taskType;
    private int taskOperator;
}
