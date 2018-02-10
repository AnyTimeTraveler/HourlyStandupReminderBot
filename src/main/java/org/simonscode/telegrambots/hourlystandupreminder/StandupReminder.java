package org.simonscode.telegrambots.hourlystandupreminder;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class StandupReminder extends TelegramLongPollingBot {

    private Timer timer;
    private boolean running;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new StandupReminder());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                if (running) return;
                running = true;
                timer = new Timer();
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.HOUR, c.get(Calendar.HOUR) + 1);
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            execute(new SendMessage(update.getMessage().getChatId(), "Standup Meeting!"));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }, c.getTime(), 3_600_000);
                System.out.println("Created!");
            } else if (update.getMessage().getText().equals("/stop")) {
                timer.cancel();
                timer.purge();
                timer = null;
                running = false;
                System.out.println("Destroyed!");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv().get("USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv().get("API_KEY");
    }
}
