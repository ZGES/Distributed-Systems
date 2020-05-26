package pl.agh.rest.processing;

import java.util.Comparator;

public class IndexComparator implements Comparator<String> {

    private int quality(String index){
        switch (index){
            case "Bardzo dobry":
                return 0;
            case "Dobry":
                return 1;
            case "Umiarkowany":
                return 2;
            case "Dostateczny":
                return 3;
            case "Zły":
                return 4;
            case "Bardzo zły":
                return 5;
            default:
                return 6;
        }
    }

    @Override
    public int compare(String s, String t) {
        return quality(s) - quality(t);
    }

}
