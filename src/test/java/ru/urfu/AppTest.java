package ru.urfu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AppTest {
    private final User testUser = new User(12L);
    private FakeBot fakeBot;
    private Core core;

    @BeforeEach
    public void setUp() {
        fakeBot = new FakeBot();
        core = new Core(fakeBot);
    }

    private void executeCommands(List<String> commands) {
        for (String command : commands) {
            core.processCommand(testUser, command);
        }
    }

    private String getLastMessageFromBot() {
        return fakeBot.getMessages().get(fakeBot.getMessages().size());
    }

    /**
     * Тестирование удаления вопроса из списка ошибочных, когда пользователь дал правильный ответ
     */

    @Test
    public void testDeleteWrongQuestionFromListIfUserGetCorrectAnswer() {
        String wrongAnswer = "Неправильный ответ";
        String correctAnswer = "Правильный ответ";
        List<String> commands = List.of("/test", wrongAnswer, "/stop", "/repeat", correctAnswer);
        executeCommands(commands);
        String questionWithWrongAnswer = "Вычислите степень: 10^2";

        Assertions.assertNotEquals(questionWithWrongAnswer,
                fakeBot.getMessages().get(4));
    }

    /**
     * Тестирование, если пользователь ответил на все вопросы правильно, то список вопросов для отработки должен быть пуст
     */
    @Test
    public void testIfUserGetAllCorrectAnswer() {
        List<String> commands = List.of("/test", "Правильный отве", "Правильный ответ", "/stop", "/repeat");
        executeCommands(commands);

        String expected = "Список вопросов для отработки пуст. Пройдите тест!";

        Assertions.assertEquals(expected, getLastMessageFromBot());
    }

    /**
     * Тестирование возвращения учебных предметов в виде кнопок
     */

    @Test
    public void testGivenSubjectsShouldBeButtons() {
        List<String> commands = List.of("/start", "SUBJECT");
        executeCommands(commands);

        Assertions.assertFalse(fakeBot.getButtonsMessages().isEmpty());
    }

    /**
     * Тестирование возвращения вопроса из теста, соответствующего выбранному предмету
     */
    @Test
    public void testChooseSubject() {
        List<String> commands = List.of("/start", "SUBJECT", "MATHS", "/test");
        executeCommands(commands);

        String mathQuestion = "Вычислите степень: 10^2";

        Assertions.assertEquals(mathQuestion, getLastMessageFromBot());
    }

    /**
     * Тестирование возможности смены предмета
     */
    @Test
    public void testChangeSubject() {
        List<String> commands = List.of("/start", "SUBJECT", "MATHS", "/back", "RUSSIAN");
        executeCommands(commands);
        String math = "Вы выбрали предмет математика, хотите начать тест (/test)?";
        String russian = "Вы выбрали предмет русский, хотите начать тест (/test)?";

        Assertions.assertEquals(math, fakeBot.getMessages().get(3));

        Assertions.assertEquals(russian, fakeBot.getMessages().get(5));
    }

    /**
     * Тестирование работоспособности напоминания
     */
    @Test
    public void testReminder() {
        List<String> commands = List.of("/start", "/test", "/stop");
        String reminderMessage = "Вас давно не было видно. Хотите пройти тест?";
        executeCommands(commands);

        TimerBehavior.setStandardDispatchTime(1);

        Assertions.assertEquals(reminderMessage, getLastMessageFromBot());
    }

    /**
     * Тестирование отключения напоминания
     */
    @Test
    public void testReminderShutdown() {
        List<String> commands = List.of("/start", "/test", "/stop", "timerOff");
        String reminderMessage = "Вас давно не было видно. Хотите пройти тест?";
        executeCommands(commands);

        TimerBehavior.setStandardDispatchTime(1);

        Assertions.assertNotEquals(reminderMessage, getLastMessageFromBot());
    }


}
