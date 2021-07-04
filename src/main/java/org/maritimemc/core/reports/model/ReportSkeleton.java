package org.maritimemc.core.reports.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class ReportSkeleton {

    private final String name;
    private final String reason;

    @Setter
    private ReportCategory category;

}
