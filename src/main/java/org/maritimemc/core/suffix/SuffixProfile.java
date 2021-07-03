package org.maritimemc.core.suffix;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.maritimemc.data.player.Suffix;

import java.util.Set;

@Getter
@AllArgsConstructor
public class SuffixProfile {

    private final Set<Suffix> suffixSet;

    @Setter
    private Suffix activeSuffix;

}
