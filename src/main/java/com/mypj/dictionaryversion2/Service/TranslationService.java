package com.mypj.dictionaryversion2.Service;

import com.mypj.dictionaryversion2.Model.Dictionary;
import com.mypj.dictionaryversion2.Model.Word;
import javafx.collections.ObservableList;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TranslationService {
    public Dictionary dictionary = new Dictionary();
    public TranslationService() {
        insertFromFile();
    }
    public void insertFromFile() {
        File file = new File(dictionary.FILE_INPUT);
        try (Scanner input = new Scanner(file)) {
            String term;
            String definition;
            while (input.hasNext()) {
                String wordRead = input.nextLine();
                term = wordRead.substring(0, wordRead.indexOf("|"));
                definition = wordRead.substring(wordRead.indexOf("|") + 1);
                dictionary.wordList.add(new Word(term, definition));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public String Search(String findWord) {
        for (Word word :dictionary.wordList) {
            if (word.GetTerm().equals(findWord)) {
                return word.GetDefinition();
            }
        }
        return "not found";
    }
    public String SearchSql(String findWord) {
        String sql= "SELECT definition_word FROM word WHERE term = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, findWord);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String definition = rs.getString("definition_word");
                    return definition;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<String> filterDictionary(String filter) {
        List<String> filteredList = new ArrayList<>();
        String lowerCaseFilter = filter.toLowerCase();
        for (Word word : dictionary.wordList) {
            if (word.GetTerm().toLowerCase().startsWith(lowerCaseFilter)) {
                filteredList.add(word.GetTerm());
            }
        }
        return filteredList;
    }
    public List<String> filterDictionarySql(String filter) {
        List<String> filteredList = new ArrayList<>();
        String lowerCaseFilter = filter.toLowerCase();
        String sql= "SELECT term FROM word WHERE term LIKE ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lowerCaseFilter + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String term = rs.getString("term");
                    filteredList.add(term);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filteredList;
    }
    public void sortList() {
        dictionary.wordList.sort(new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return o1.GetTerm().compareTo(o2.GetTerm());
            }
        });
    }
    public String add(String term, String definition) {
        boolean check = true;
        for (Word word: dictionary.wordList) {
            if ((word.GetTerm().equals(term)) && (word.GetDefinition().equals(definition))) {
                check = false;
            }
        }
        if (check) {
            try {
                Word wordRead = new Word();
                wordRead.SetTerm(term);
                wordRead.SetDefinition(definition);
                dictionary.wordList.add(wordRead);
                sortList();
                FileWriter fileWriter = new FileWriter(dictionary.FILE_INPUT);
                for (Word word1 :dictionary.wordList) {
                    fileWriter.write(word1.GetTerm() + "|" + word1.GetDefinition() + "\n");
                }
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Added successfully";
    }
    public String addWordSql(String term, String definition) {
        String checkSql = "SELECT term FROM word WHERE term = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, term);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "error add - word already exists";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "error add - SQL exception occurred";
        }

        String insertSql = "INSERT INTO word(term, definition_word) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, term);
            stmt.setString(2, definition);
            stmt.executeUpdate();
            return "Add successful";
        } catch (SQLException e) {
            e.printStackTrace();
            return "error add - SQL exception occurred";
        }
    }
    public String removeWord(String term) {
        dictionary.wordList.removeIf(word -> word.GetTerm().equalsIgnoreCase(term));
        updateDataFile();
        return "Delete Word Successful";
    }
    public String removeWordSql(String term, String definition) {
        String sql = "SELECT term, definition_word FROM word Where term = ? AND definition_word = ?";
        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, term);
            stmt.setString(2, definition);
            try(ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return "No record found to delete";
                }
            }
            } catch (SQLException e) {
            e.printStackTrace();
            return "Error checking record - SQL exception occurred";
        }
            String removeSql = "DELETE FROM word WHERE term = ? AND definition_word = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(removeSql)) {
                stmt.setString(1, term);
                stmt.setString(2, definition);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    return "Delete successful";
                } else {
                    return "No record found to delete";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error delete - SQL exception occurred";
            }
    }
    public void updateDataFile() {
        try (FileWriter fw = new FileWriter(dictionary.FILE_INPUT, false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (Word word : dictionary.wordList) {
                bw.write(word.GetTerm() + "|" + word.GetDefinition());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
