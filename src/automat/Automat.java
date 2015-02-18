/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Иван
 */
public class Automat {

    /**
     * Обозначение пустого символа
     */
    public static final String EMPTY = "empty";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        List<Condition> sourceList = readFromFile("automat.txt");

        System.out.println("Исходный автомат:");
        printConditionList(sourceList);
        List<Condition> determ = getDetermAutomat(sourceList);
        System.out.println("Детерминированный автомат:");
        printConditionList(determ);
        new AutomatFrame(sourceList);
        new AutomatFrame(determ);
    }

    /**
     * Получаем детерминированный автомат.
     */
    private static List<Condition> getDetermAutomat(List<Condition> sourceList) {
        Condition inputCondition = createInputCondition(sourceList);
        inputCondition.convert();       
        List<Condition> oldList;
        List<Condition> newList = new ArrayList<>();
        newList.add(inputCondition);
        // Вызываем convert для входного, сохраняем полученные состояния.
        // Далее вызываем convert до тех пор, пока не перестанут создаваться
        // новые состояния.
        do {
            oldList = newList;
            newList = convertConditionList(oldList);
        } while (oldList.size() != newList.size());
        
        return newList;
    }
    
    /**
     * Создание входного состояния. 
     * @param sourceList автомат
     * @return входное состояние (объединённое)
     */
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

    /**
     * Получить все состояния, в которые можно попасть по пустому символу из
     * состояний, находящихся в списке.
     * @param usedConditions список состояний
     * @return 
     */
    public static List<Condition> getEmptyRoutes(List<Condition> usedConditions) {
        return getEmptyRoutes(usedConditions, usedConditions);
    }

    /**
     * Получить все состояния, в которые можно попасть по пустому символу из
     * состояний, находящихся в списке. Проверяются только состояния из списка
     * toCheck, usedConditions передаётся, чтобы не добавлять одно и то же
     * состояние по несколько раз, если оно уже есть в списке.
     * @param usedConditions
     * @param toCheck
     * @return 
     */
    public static List<Condition> getEmptyRoutes(List<Condition> usedConditions, List<Condition> toCheck) {
        List<Condition> newToCheck = new ArrayList<>();
        for (Condition c : toCheck) { // Проходим по всем состояниям,
                                      // которые нужно проверить.
            List<Condition> emptySymbolConditions = c.getRoutes().get(EMPTY);
            if (emptySymbolConditions == null) {
                continue;
            }
            for (Condition emptyC : emptySymbolConditions) {
                // Если у проверяемого состояния есть пути по пустому символу,
                // то добавляем состояния, к которым ведут эти пути,
                // в список на следующую проверку. Добавляется состояние
                // только тогда, когда его ещё не добавляли в список usedConditions.
                if (!usedConditions.contains(emptyC)) {
                    newToCheck.add(emptyC);
                }
            }
        }
        List<Condition> newList = new ArrayList<>(usedConditions);
        // Добавляем все состояния, которые собираемся проверять
        addAllConditions(newList,newToCheck);
        if (!newToCheck.isEmpty()) {
            // И добавляем все состояния, которые найдём в ходе проверки
            addAllConditions(newList,getEmptyRoutes(newList, newToCheck));
        }
        return newList;
    }
    
    public static void printConditionList(List<Condition> list) {
        for(Condition c : list) {
            System.out.println(c);
        }
    }
    
    /**
     * Добавляем состояние, если его ещё нет в списке.
     * @param list
     * @param listToAdd 
     */
    public static void addAllConditions(List<Condition> list, List<Condition> listToAdd) {
        for (Condition c : listToAdd) {
            if (!list.contains(c)) {
                list.add(c);
            }
        }
    }
    
    /**
     * Вызываем convert для всех состояний из list. Возвращаем список, в который
     * входят все состояния из list + состояния, в которые из них попадаем.
     * @param list
     * @return 
     */
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
   
    /**
     * Читать автомат с файла.
     * Файл заполнять следующим образом:
     * В каждой строке одно состояние в таком виде:
     * имя_состояния:input=входное,output=выходное.символ1-состояние1,состояние2,.... символ2-состояние1,состояние2,... ...
     * (символы через пробел). "входное" и "выходное" - true или false.
     * @param fileName
     * @return
     * @throws IOException 
     */
   private static List<Condition> readFromFile(String fileName) throws IOException {
       BufferedReader reader = new BufferedReader(new FileReader(fileName));
       String line;
       List<String> lines = new ArrayList<>();
       List<Condition> conditions = new ArrayList<>();
       while ((line = reader.readLine()) != null) {
           lines.add(line);
           String name = line.replaceAll("(.*):.*", "$1");
           boolean isInput = Boolean.parseBoolean(line.replaceAll(".*input=(.*),output.*", "$1"));
           boolean isOutput = Boolean.parseBoolean(line.replaceAll(".*output=(.*)\\..*", "$1"));
           conditions.add(new Condition(isInput, isOutput, name));
       }
       int index = 0;
       for (Condition c : conditions) {
           String routes = lines.get(index).replaceAll(".*\\.(.*)", "$1");
           StringTokenizer st = new StringTokenizer(routes, " ");
           while(st.hasMoreTokens()) {
               String symbolRoute = st.nextToken();
               String symbol = symbolRoute.replaceAll("(.*)\\-.*", "$1");
               String symbolConditions = symbolRoute.replaceAll(".*\\-(.*)", "$1");
               StringTokenizer symbolSt = new StringTokenizer(symbolConditions, ",");
               while(symbolSt.hasMoreTokens()) {
                   String conditionName = symbolSt.nextToken();
                   Condition temp = new Condition(false, false, conditionName);
                   c.addSimbol(symbol, conditions.get(conditions.indexOf(temp)));
               }
           }
           index++;
       }
       return conditions;
   }
}
