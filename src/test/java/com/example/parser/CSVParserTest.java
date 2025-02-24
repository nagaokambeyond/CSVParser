package com.example.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CSVParserTest {
    @Test
    @DisplayName("通常")
    void normal() throws ErrorOccurredWhileParsingException {
        final String doc = "a,b,c";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(3);
        assertThat(res.poll()).isEqualTo("a");
        assertThat(res.poll()).isEqualTo("b");
        assertThat(res.poll()).isEqualTo("c");
    }

    @Test
    @DisplayName("ダブルクォートあり")
    void value_double_quote() throws ErrorOccurredWhileParsingException {
        final String doc = "\"a\",b,c";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(3);
        assertThat(res.poll()).isEqualTo("\"a\"");
        assertThat(res.poll()).isEqualTo("b");
        assertThat(res.poll()).isEqualTo("c");
    }

    @Test
    @DisplayName("ダブルクォートありでカンマあり")
    void value_double_quote_comma() throws ErrorOccurredWhileParsingException {
        final String doc = "\"a,d\",b,c";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(3);
        assertThat(res.poll()).isEqualTo("\"a,d\"");
        assertThat(res.poll()).isEqualTo("b");
        assertThat(res.poll()).isEqualTo("c");
    }

    @Test
    @DisplayName("空")
    void empty() throws ErrorOccurredWhileParsingException {
        final String doc = "";
        final var list = prepareData(doc);
        assertThat(list).hasSize(0);
    }

    @Test
    @DisplayName("値なし")
    void no_value() throws ErrorOccurredWhileParsingException {
        final String doc = ",";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(2);
        assertThat(res.poll()).isEqualTo("");
        assertThat(res.poll()).isEqualTo("");
    }

    @Test
    @DisplayName("値が漢字")
    void value_kanji() throws ErrorOccurredWhileParsingException {
        final String doc = "あ,い";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(2);
        assertThat(res.poll()).isEqualTo("あ");
        assertThat(res.poll()).isEqualTo("い");
    }

    @Test
    @DisplayName("値が絵文字")
    void value_emoji() throws ErrorOccurredWhileParsingException {
        final String doc = "🍎,🗻";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(2);
        assertThat(res.poll()).isEqualTo("🍎");
        assertThat(res.poll()).isEqualTo("🗻");
    }

    @Test
    @DisplayName("全角カンマ")
    void comma1() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
        final String doc = "、";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(1);
        assertThat(res.poll()).isEqualTo("、");
    }

    @Test
    @DisplayName("全角カンマ２")
    void comma2() throws ErrorOccurredWhileParsingException {
        final String doc = "，";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(1);
        assertThat(res.poll()).isEqualTo("，");
    }

    @Test
    @DisplayName("ErrorOccurredWhileParsingException")
    void exception_test1() {
        assertThrows(ErrorOccurredWhileParsingException.class, () -> {
                final String doc = "\"abc\nde\",\"f";
                final var list = prepareData(doc);
            }
        );
    }


    private List<String[]> prepareData(final String doc) throws ErrorOccurredWhileParsingException {
        var result = new ArrayList<String[]>();

        try (final var reader = new BufferedReader(new StringReader(doc))) {
            String[] res;
            final var parser = new CSVParser(reader);
            while ((res = parser.splitLine()) != null) {
                result.add(res);
            }
        } catch (IOException | UnexpectedTokenFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
