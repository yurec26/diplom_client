package org.example;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    static boolean isSessionIsFinished = false;
    static Integer PORT;
    static Scanner scanner;
    static FileWriter writer;

    public static void main(String[] args) throws IOException {
        File fileSettings = new File("C:/Users/Юрий/IdeaProjects/diplom_2/server/src/main/resources/settings.txt");
        File filelog = new File("client/src/main/resources/log.txt");

        if (choosePort(fileSettings)) {
            connect(filelog);
        }

    }

    public static void connect(File log) {
        scanner = new Scanner(System.in);
        try (Socket socketClient = new Socket("netology.homework", PORT)) {
            PrintWriter printWriter = new PrintWriter(socketClient.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            // поток вывода всех накоппившихся сообщений
            new Thread(() -> {
                while (true) {
                    String massage;
                    try {
                        massage = bufferedReader.readLine();
                    } catch (IOException e) {
                        System.out.println("you leaved the chat or the server connection had been interrupted (enter anything to exit)");
                        isSessionIsFinished = true;
                        Thread.currentThread().interrupt();
                        break;
                    }
                    if (massage != null) {
                        System.out.println(massage);
                        try {
                            logFile(log, massage);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
            // цикл ввода сообщений для отправки в чат
           displayMassageLoop(printWriter,isSessionIsFinished);
        } catch (IOException e) {
            System.out.println("server is unavailable");
        }
    }
    public static void displayMassageLoop(PrintWriter printWriter, boolean isSessionIsFinished){
        while (true) {
            if (isSessionIsFinished) {
                break;
            }
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                printWriter.println(input);
                break;
            }
            printWriter.println(input);
        }
    }

    public static boolean choosePort(File file) throws IOException {
        if (!file.exists()) {
            System.out.println("no any settings file found");
            return false;
        } else {
            scanner = new Scanner(file);
            if (!scanner.hasNextLine()) {
                System.out.println("the file is empty, ask admin to update");
                return false;
            } else {
                    String line = scanner.nextLine();
                    System.out.println("Current port is: " + line);
                    PORT = Integer.parseInt(line);
                    return true;
            }
        }
    }

    public static void logFile(File file, String massage) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);
        writeFile(file, true, "'" + massage + "' " + formattedTime + "\n");
    }

    public static void writeFile(File file, boolean append, String msg) throws IOException {
        writer = new FileWriter(file, append);
        writer.write(msg);
        writer.close();
    }
}
