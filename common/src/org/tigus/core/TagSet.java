package org.tigus.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author Mircea Bardac
 * 
 */
public class TagSet implements Map<String, List<String>> {
    private Map<String, List<String>> container;

    public TagSet() {
        this.container = new HashMap<String, List<String>>();
    }

    public TagSet(TagSet t) {
        this.container = new HashMap<String, List<String>>();
        for (String tag : t.container.keySet()) {
            List<String> values = new Vector<String>();
            for (String s : t.container.get(tag)) {
                values.add(s);
            }
            this.container.put(tag, values);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        TagSet test = (TagSet) obj;
        return ((test.container.equals(this.container)));
    }

    public void clear() {
        container.clear();
    }

    public boolean containsKey(Object key) {
        return container.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return container.containsValue(value);
    }

    public Set<Entry<String, List<String>>> entrySet() {
        return container.entrySet();
    }

    public List<String> get(Object key) {
        return container.get(key);
    }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    public Set<String> keySet() {
        return container.keySet();
    }

    public List<String> remove(Object key) {
        return container.remove(key);
    }

    public int size() {
        return container.size();
    }

    public Collection<List<String>> values() {
        return container.values();
    }

    public List<String> put(String key, List<String> value) {
        return container.put(key, value);
    }

    public void putAll(Map< ? extends String, ? extends List<String>> t) {
        container.putAll(t);
    }
}
