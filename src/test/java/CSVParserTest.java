import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CSVParserTest {
    @Test
    void 通常(){
        final String doc = "a,b,c";
        final var list = main(doc);
        assertThat(list.size()).isEqualTo(1);
        
        final String[] res = list.get(0);
        assertThat(res.length).isEqualTo(3);
        assertThat(res[0]).isEqualTo("a");
        assertThat(res[1]).isEqualTo("b");
        assertThat(res[2]).isEqualTo("c");
    }

    @Test
    void 空(){
        final String doc = "";
        final var list = main(doc);
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    void 値なし(){
        final String doc = ",";
        final var list = main(doc);
        assertThat(list.size()).isEqualTo(1);
        final String[] res = list.get(0);
        assertThat(res.length).isEqualTo(2);
        assertThat(res[0]).isEqualTo("");
        assertThat(res[1]).isEqualTo("");
    }

    @Test
    void 値が漢字(){
        final String doc = "あ,い";
        final var list = main(doc);
        assertThat(list.size()).isEqualTo(1);
        final String[] res = list.get(0);
        assertThat(res.length).isEqualTo(2);
        assertThat(res[0]).isEqualTo("あ");
        assertThat(res[1]).isEqualTo("い");
    }

    @Test
    void 値が絵文字(){
        final String doc = "🍎,🗻";
        final var list = main(doc);
        assertThat(list.size()).isEqualTo(1);
        final String[] res = list.get(0);
        assertThat(res.length).isEqualTo(2);
        assertThat(res[0]).isEqualTo("🍎");
        assertThat(res[1]).isEqualTo("🗻");
    }

    @Test
    void 全角カンマ(){
        final String doc = "、";
        final var list = main(doc);
        assertThat(list.size()).isEqualTo(1);
        final String[] res = list.get(0);
        assertThat(res.length).isEqualTo(1);
        assertThat(res[0]).isEqualTo("、");
    }

    @Test
    void 全角カンマ２(){
        final String doc = "，";
        final var list = main(doc);
        assertThat(list.size()).isEqualTo(1);
        final String[] res = list.get(0);
        assertThat(res.length).isEqualTo(1);
        assertThat(res[0]).isEqualTo("，");
    }

    
    private List<String[]> main(final String doc) {
        final var reader = new BufferedReader(new StringReader(doc));
        final var parser = new example.parser.CSVParser(reader);
        String[] res;
        var result = new ArrayList<String[]>();

        try{
            while ((res = parser.splitLine()) != null) {
                result.add(res);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;        
    }
}
