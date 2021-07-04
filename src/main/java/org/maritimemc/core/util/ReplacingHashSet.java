package org.maritimemc.core.util;

import java.util.*;

public class ReplacingHashSet<T> extends HashSet<T>  {

    @Override
    public boolean add(T t) {
        super.remove(t);

        return super.add(t);
    }
}
