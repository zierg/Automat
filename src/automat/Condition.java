/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author Иван
 */
public class Condition implements Comparable<Condition>{

    private final Map<String, List<Condition>> routes = new TreeMap<>();
    private final boolean isInput;
    private final boolean isOutput;
    private final String name;
    private boolean converted = false;

    public Condition(boolean isInput, boolean isOutput, String name) {
        this.isInput = isInput;
        this.isOutput = isOutput;
        this.name = name;
    }

    public Condition(boolean isInput, List<Condition> conditions) {
        
        this.isInput = isInput;
        boolean isOut = false;
        StringBuilder b = new StringBuilder();
        String sep = ",";
        for (Condition c : conditions) {
            if (c.isOutput) {
                isOut = true;
            }
            b.append(c.name).append(sep);
            for (Map.Entry<String, List<Condition>> cond : c.routes.entrySet()) {
                String simbol = cond.getKey();
                if (!simbol.equals(Automat.EMPTY)) {
                    List<Condition> list = Automat.getEmptyRoutes(cond.getValue());
                    if (routes.containsKey(simbol)) {
                        Automat.addAllConditions(routes.get(simbol), list);
                    } else {
                        routes.put(simbol, list);
                    }
                    Collections.sort(routes.get(simbol));
                }
            }
        }
        b.delete(b.length() - sep.length(), b.length());
        this.name = b.toString();
        isOutput = isOut;
    }

    public void addSimbol(String simbol, Condition cond) {
        List<Condition> list = new ArrayList<>();
        list.add(cond);
        if (getRoutes().containsKey(simbol)) {
            Automat.addAllConditions(getRoutes().get(simbol), list);
        } else {
            getRoutes().put(simbol, list);
        }
        Collections.sort(getRoutes().get(simbol));

    }

    /**
     * @return the routes
     */
    public Map<String, List<Condition>> getRoutes() {
        return routes;
    }

    /**
     * @return the isInput
     */
    public boolean isIsInput() {
        return isInput;
    }

    /**
     * @return the isOutput
     */
    public boolean isIsOutput() {
        return isOutput;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Condition) {
            Condition c = (Condition) o;
            return this.name.equals(c.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("(").append(name).append("):");
        String sep = ",";
        for (Map.Entry<String, List<Condition>> entry : routes.entrySet()) {
            String symbol = entry.getKey();
            List<Condition> list = entry.getValue();
            b.append(" [").append(symbol).append("]->");
            for (Condition c : list) {
                b.append("(").append(c.name).append(")").append(sep);
            }
        }
        b.replace(b.length() - sep.length(), b.length(), ";");
        return b.toString();
    }

    @Override
    public int compareTo(Condition o) {
        return name.compareToIgnoreCase(o.name);
    }
    
    public void convert() {
        if (converted) {
            return;
        }
        for (Map.Entry<String, List<Condition>> entry : routes.entrySet()) {
            String symbol = entry.getKey();
            List<Condition> list = entry.getValue();
            Condition newCondition = new Condition(isInput, list);
            List<Condition> newList = new ArrayList<>();
            newList.add(newCondition);
            routes.put(symbol, newList);
        }
        converted = true;
    }
}
