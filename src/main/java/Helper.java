import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helper {
   public static List<Integer> generatePeriodsList(int first, int last) {
        if (Integer.toString(first).length() != Integer.toString(last).length())
            return Collections.emptyList();
        else if (Integer.toString(first).length() == 4)
            return IntStream.rangeClosed(first, last).boxed().collect(Collectors.toList());
        else if (Integer.toString(first).length() == 6) {
            int startYear = first / 100;
            int lastYear = last / 100;
            int startMonth = first % 100;
            int lastMonth = last % 100;
            List<Integer> list = new ArrayList<>();
            for (int i = startYear; i <= lastYear; i++) {
                for (int j = 1; j <= 12; j++) {
                    if (j < startMonth && i == startYear) continue;
                    if (j > lastMonth && i == lastYear) continue;
                    int x = i * 100 + j;
                    list.add(x);
                }
            }
            return list;
        } else return Collections.emptyList();
    }
}
