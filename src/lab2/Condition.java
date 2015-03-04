/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lab2;

import automat.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author Иван
 */
public class Condition implements Comparable<Condition> {

    private final Map<Arc, Condition> routes = new HashMap<>();
    private final boolean isInput;
    private final boolean isOutput;
    private final String name;
    private boolean converted = false;

    /**
     * Конструктор для создания автомата
     *
     * @param isInput если входное, то true, иначе false
     * @param isOutput если выходное, то true, иначе false
     * @param name имя состояния
     */
    public Condition(boolean isInput, boolean isOutput, String name) {
        this.isInput = isInput;
        this.isOutput = isOutput;
        this.name = name;
    }

    public Arc getArc(String symbol) {
        for (Arc arc : routes.keySet()) {
            if (arc.getSymbol().equals(symbol)) {
                return arc;
            }
        }
        return null;
    }

    /**
     * Добавление перехода по символу в состояние
     *
     * @param simbol символ
     * @param cond состояние
     */
    public void addSimbol(String simbol, double probability, Condition cond) {
        Arc newArc = new Arc(probability, simbol);
        routes.put(newArc, cond);

    }

    /**
     * @return the routes
     */
    public Map<Arc, Condition> getRoutes() {
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
    public int compareTo(Condition o) {
        return name.compareToIgnoreCase(o.name);
    }
}
