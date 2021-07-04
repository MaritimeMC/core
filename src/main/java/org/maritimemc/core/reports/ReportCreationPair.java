package org.maritimemc.core.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.maritimemc.core.reports.model.Report;

@Getter
@AllArgsConstructor
public class ReportCreationPair {

    private final ReportController.ReportCreationResponse reportCreationResponse;
    private final Report report;

}
