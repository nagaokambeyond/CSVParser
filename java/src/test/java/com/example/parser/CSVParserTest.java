package com.example.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

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
    void normal() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
        final var doc = "a,b,c";
        final var list = prepareData(doc);
        assertThat(list).hasSize(1);

        final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
        assertThat(res).hasSize(3);
        assertThat(res.poll()).isEqualTo("a");
        assertThat(res.poll()).isEqualTo("b");
        assertThat(res.poll()).isEqualTo("c");
    }

    @Test
    @DisplayName("コメント")
    void comment() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
        final var doc = "#,";
        final var list = prepareData(doc);
        assertThat(list).hasSize(0);
    }

    @Execution(ExecutionMode.CONCURRENT)
    @Nested
    @DisplayName("値テスト")
    class ValueTest {
        @Test
        @DisplayName("スペースあり")
        void value_in_space() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "a a a,b bb, c cc ";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(3);
            assertThat(res.poll()).isEqualTo("a a a");
            assertThat(res.poll()).isEqualTo("b bb");
            assertThat(res.poll()).isEqualTo(" c cc ");
        }

        @Test
        @DisplayName("値にタブあり")
        void value_in_tab() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "\"a\t\",b,\tc";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(3);
            assertThat(res.poll()).isEqualTo("a\t");
            assertThat(res.poll()).isEqualTo("b");
            assertThat(res.poll()).isEqualTo("\tc");
        }

        @Test
        @DisplayName("値なし")
        void empty_value1() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "";
            final var list = prepareData(doc);
            assertThat(list).hasSize(0);
        }

        @Test
        @DisplayName("値なし")
        void value_value2() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = ",";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(2);
            assertThat(res.poll()).isEqualTo("");
            assertThat(res.poll()).isEqualTo("");
        }

        @Test
        @DisplayName("値が漢字")
        void value_kanji() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "あ,い";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(2);
            assertThat(res.poll()).isEqualTo("あ");
            assertThat(res.poll()).isEqualTo("い");
        }

        @Test
        @DisplayName("値が絵文字")
        void value_emoji() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "🍎,🗻";
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
            final var doc = "、";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(1);
            assertThat(res.poll()).isEqualTo("、");
        }

        @Test
        @DisplayName("全角カンマ２")
        void comma2() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "a，bb";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(1);
            assertThat(res.poll()).isEqualTo("a，bb");
        }

        @Test
        @DisplayName("全角カンマ３")
        void comma3() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "a，bb,cc";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(2);
            assertThat(res.poll()).isEqualTo("a，bb");
            assertThat(res.poll()).isEqualTo("cc");
        }
    }

    @Execution(ExecutionMode.CONCURRENT)
    @DisplayName("ダブルクォートテスト")
    @Nested
    class DoubleQuoteTest {
        @Test
        @DisplayName("ダブルクォートあり")
        void value_double_quote() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "\"a\",b,c";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(3);
            assertThat(res.poll()).isEqualTo("a");
            assertThat(res.poll()).isEqualTo("b");
            assertThat(res.poll()).isEqualTo("c");
        }

        @Test
        @DisplayName("ダブルクォートありでカンマあり")
        void value_double_quote_in_comma() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "\"a,d\",b,c";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(3);
            assertThat(res.poll()).isEqualTo("a,d");
            assertThat(res.poll()).isEqualTo("b");
            assertThat(res.poll()).isEqualTo("c");
        }

        @Test
        @DisplayName("最後の要素がダブルクォートつき")
        void value_double_quote_end() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "\"a,d\",\"1\"";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(2);
            assertThat(res.poll()).isEqualTo("a,d");
            assertThat(res.poll()).isEqualTo("1");
        }

        @Test
        @DisplayName("ダブルクォート内にダブルクォート")
        void value_double_quote_in_double_quote() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "\"a\"\"b\"";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(1);
            assertThat(res.poll()).isEqualTo("a\"b");
        }

        @Test
        @DisplayName("改行つき1")
        void value_new_line1() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "\"a\nd\",\",1\"";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(2);
            assertThat(res.poll()).isEqualTo("a\nd");
            assertThat(res.poll()).isEqualTo(",1");
        }

        @Test
        @DisplayName("改行つき2")
        void value_new_line2() throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
            final var doc = "\"a\n\n,\n\nd\",\",1\"";
            final var list = prepareData(doc);
            assertThat(list).hasSize(1);

            final Queue<String> res = new ArrayDeque<>(List.of(list.getFirst()));
            assertThat(res).hasSize(2);
            assertThat(res.poll()).isEqualTo("a\n\n,\n\nd");
            assertThat(res.poll()).isEqualTo(",1");
        }
    }

    @Execution(ExecutionMode.CONCURRENT)
    @DisplayName("ExceptionTest")
    @Nested
    class ExceptionTest {
        @Test
        @DisplayName("ErrorOccurredWhileParsingException_ダブルクォート閉じられてない1")
        void exception_ErrorOccurredWhileParsingException1() {
            assertThrows(ErrorOccurredWhileParsingException.class, () -> {
                    final var doc = "\"a,b,c";
                    prepareData(doc);
                }
            );
        }

        @Test
        @DisplayName("ErrorOccurredWhileParsingException_ダブルクォート閉じられてない2")
        void exception_ErrorOccurredWhileParsingException2() {
            assertThrows(ErrorOccurredWhileParsingException.class, () -> {
                    final var doc = "\"a,b,\nc";
                    prepareData(doc);
                }
            );
        }

        @Test
        @DisplayName("ErrorOccurredWhileParsingException_ダブルクォート閉じられてない3")
        void exception_ErrorOccurredWhileParsingException3() {
            assertThrows(ErrorOccurredWhileParsingException.class, () -> {
                    final var doc = "\"abc\nde\",\"f";
                    prepareData(doc);
                }
            );
        }

        @Test
        @DisplayName("IllegalArgumentException")
        void exception_IllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new CSVParser(null));
        }

        @Test
        @DisplayName("UnexpectedTokenFoundException")
        void exception_UnexpectedTokenFoundException() {
            assertThrows(UnexpectedTokenFoundException.class, () -> {
                final String doc = "\"a,d\",\"1\" ";
                prepareData(doc);
            });
        }

        @Test
        @DisplayName("IOException")
        void exception_IOException() {
            assertThrows(IOException.class, () -> {
                final var reader = new BufferedReader(new StringReader(""));
                reader.close();
                final var parser = new CSVParser(reader);
                parser.splitLine();
            });
        }
    }

    private List<String[]> prepareData(final String doc) throws ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
        var result = new ArrayList<String[]>();

        try (final var reader = new BufferedReader(new StringReader(doc))) {
            String[] res;
            final var parser = new CSVParser(reader);
            while ((res = parser.splitLine()) != null) {
                result.add(res);
            }
        } catch (IOException e) {}
        return result;
    }
}
