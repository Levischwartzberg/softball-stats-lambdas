package org.morts.dto;

import lombok.Builder;
import lombok.Value;
import org.morts.domain.Result;

@Builder
@Value
public class ResultStatline {

    Result result;
    Statline statline;
}
