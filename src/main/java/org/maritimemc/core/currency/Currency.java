package org.maritimemc.core.currency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.maritimemc.data.versioning.MaritimeMaterial;

@RequiredArgsConstructor
@Getter
public class Currency {

    private final String id;
    private final String singular;
    private final String plural;
    private final boolean global;
    private final MaritimeMaterial icon;

}
