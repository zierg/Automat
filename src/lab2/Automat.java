/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lab2;

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
     * @param args the c ommand line arguments
     */
    public static void main(String[] args) throws IOException {
        List<Condition> sourceList = readFromFile("automat.txt");
        double x = summ(sourceList, "aaa");
        System.out.println("summ = " + x);
    }

    public static void printConditionList(List<Condition> list) {
        for (Condition c : list) {
            System.out.println(c);
        }
    }

    /**
     * Добавляем состояние, если его ещё нет в списке.
     *
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

    public static double probability(Condition cond, String word) {

        if (word.isEmpty()) {
            if (!cond.isIsOutput()) {
                return 0;
            } else {
                return 1;
            }

        }
        double summ=0;
        String substring = word.substring(1);
        for (Arc arc : cond.getArc(Character.toString(word.charAt(0)))){
            double p = arc.getProbability();
            Condition c = cond.getRoutes().get(arc);
            summ += p * probability(c, substring);
        }
  
      
        return summ;
    }

    public static double summ(List<Condition> cond, String word) {
        double summ = 1;
        Condition current = null;
        for (Condition c : cond) {
            if (c.isIsInput()) {
                current = c;
                break;
            }

        }
        if (current == null) {
            return 0;
        }
        
        return probability(current, word);
    }

    /**
     * Читать автомат с файла. Файл заполнять следующим образом: В каждой строке
     * одно состояние в таком виде:
     * имя_состояния:input=входное,output=выходное.символ1-состояние1,состояние2,....
     * символ2-состояние1,состояние2,... ... (символы через пробел). "входное" и
     * "выходное" - true или false.
     *
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
            boolean isOutput = Boolean.parseBoolean(line.replaceAll(".*output=(.*),.*", "$1"));
            conditions.add(new Condition(isInput, isOutput, name));
        }
        int index = 0;
        for (Condition c : conditions) {
            String routes = lines.get(index).replaceAll(".*\\,(.*)", "$1");

            StringTokenizer st = new StringTokenizer(routes, " ");
            //System.out.println("routes = " + routes);
            while (st.hasMoreTokens()) {
                String symbolRoute = st.nextToken();
                //System.out.println("symbolRoute = " + symbolRoute);
                String symbol = symbolRoute.replaceAll("(.*)\\-.*", "$1");
                String symbolConditions = symbolRoute.replaceAll(".*\\-(.*)\\(.*\\)", "$1");
                //System.out.println(symbolRoute.replaceAll(".*\\((.*)\\)", "$1"));
                double probability = Double.parseDouble(symbolRoute.replaceAll(".*\\((.*)\\)", "$1"));
                //System.out.println("p = " + probability);
                StringTokenizer symbolSt = new StringTokenizer(symbolConditions, ",");
                while (symbolSt.hasMoreTokens()) {
                    String conditionName = symbolSt.nextToken();
                    //System.out.println("conditionName = " + conditionName);
                    Condition temp = new Condition(false, false, conditionName);
                    c.addSimbol(symbol, probability, conditions.get(conditions.indexOf(temp)));
                }
            }
            index++;
        }
        return conditions;
    }
}
