/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Иван
 */
public class Automat {

    public static final String EMPTY = "empty";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<Condition> sourceList = new ArrayList<>();

        String a = "a";
        String b = "b";
        String c = "c";

        Condition q1 = new Condition(true, false, "q1");
        Condition q2 = new Condition(false, true, "q2");
        Condition q3 = new Condition(false, true, "q3");
        Condition q4 = new Condition(false, true, "q4");

        q1.addSimbol(a, q2);
        q1.addSimbol(a, q3);
        q1.addSimbol(b, q2);
        q1.addSimbol(EMPTY, q3);

        q2.addSimbol(c, q4);

        q3.addSimbol(b, q1);
        q3.addSimbol(b, q2);
        q3.addSimbol(c, q3);

        q4.addSimbol(b, q3);

        sourceList.add(q1);
        sourceList.add(q2);
        sourceList.add(q3);
        sourceList.add(q4);
        
        
        System.out.println("Исходный автомат:");
        printConditionList(sourceList);
        List<Condition> determ = getDetermAutomat(sourceList);
        System.out.println("Детерминированный автомат:");
        printConditionList(determ);
        
    }

    private static List<Condition> getDetermAutomat(List<Condition> sourceList) {
        Condition inputCondition = createInputCondition(sourceList);
        inputCondition.convert();       
        List<Condition> oldList;
        List<Condition> newList = new ArrayList<>();
        newList.add(inputCondition);
        do {
            oldList = newList;
            newList = convertConditionList(oldList);
        } while (oldList.size() != newList.size());
        
        return newList;
    }
    
    private static Condition createInputCondition(List<Condition> sourceList) {
        List<Condition> inputConditions = new ArrayList<>();
        for (Condition c : sourceList) {
            if (c.isIsInput()) {
                inputConditions.add(c);
            }
        }
        List<Condition> finalList = getEmptyRoutes(inputConditions);
        return new Condition(true, finalList);
    }

    public static List<Condition> getEmptyRoutes(List<Condition> usedConditions) {
        return getEmptyRoutes(usedConditions, usedConditions);
    }

    public static List<Condition> getEmptyRoutes(List<Condition> usedConditions, List<Condition> toCheck) {
        List<Condition> newToCheck = new ArrayList<>();
        for (Condition c : toCheck) {
            List<Condition> emptySymbolConditions = c.getRoutes().get(EMPTY);
            if (emptySymbolConditions == null) {
                continue;
            }
            for (Condition emptyC : emptySymbolConditions) {
                if (!usedConditions.contains(emptyC)) {
                    newToCheck.add(emptyC);
                }
            }
        }
        List<Condition> newList = new ArrayList<>(usedConditions);
        addAllConditions(newList,newToCheck);
        if (!newToCheck.isEmpty()) {
            addAllConditions(newList,getEmptyRoutes(newList, newToCheck));
        }
        return newList;
    }
    
    public static void printConditionList(List<Condition> list) {
        for(Condition c : list) {
            System.out.println(c);
        }
    }
    
    public static void addAllConditions(List<Condition> list, List<Condition> listToAdd) {
        for (Condition c : listToAdd) {
            if (!list.contains(c)) {
                list.add(c);
            }
        }
    }
    
   private static List<Condition> convertConditionList(List<Condition> list) {
       List<Condition> convertedList = new ArrayList<>(list);
       for (Condition c : list) {
           c.convert();
           for (List<Condition> symbolList : c.getRoutes().values()) {
               addAllConditions(convertedList, symbolList);
           }
       }
       return convertedList;
   }
}
