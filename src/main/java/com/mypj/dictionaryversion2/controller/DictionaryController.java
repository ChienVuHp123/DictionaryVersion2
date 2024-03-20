package com.mypj.dictionaryversion2.controller;

import com.mypj.dictionaryversion2.Service.TranslationService;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.List;

public class DictionaryController {

    private final TranslationService translationService = new TranslationService();

    private ObservableList<String> filteredTerms;

    @FXML
    private TextField txtWord;

    @FXML
    private TextArea txtDefinition;

    @FXML
    private Button btnLookUp;
    @FXML
    private ListView<String> listViewWords;

    @FXML
    public void initialize() {
        filteredTerms = FXCollections.observableArrayList();
        listViewWords.setItems(filteredTerms);
        listViewWords.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
//                txtDefinition.setText(translationService.Search(newValue));
                txtDefinition.setText(translationService.SearchSql(newValue));
            }
        });
    }
    @FXML
    protected void handleLookUpAction() {
//        txtDefinition.setText(translationService.Search(txtWord.getText()));
        txtDefinition.setText(translationService.SearchSql(txtWord.getText()));
    }

    @FXML
    protected void searchFilterList() {
        PauseTransition pause = new PauseTransition(Duration.millis(300));
        pause.setOnFinished(event -> {
            String filter = txtWord.getText();
//            List<String> filteredList = translationService.filterDictionary(filter);
            List<String> filteredList = translationService.filterDictionarySql(filter);
            filteredTerms.setAll(filteredList);
        });
        txtWord.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.playFromStart();
        });
    }

    @FXML
    protected void handleAdd() {
//        txtDefinition.setText(translationService.add(txtWord.getText(), txtDefinition.getText()));
        txtDefinition.setText(translationService.addWordSql(txtWord.getText(), txtDefinition.getText()));
    }

    @FXML
    protected void handleDel() {
//        txtDefinition.setText(translationService.removeWord(txtWord.getText()));
        txtDefinition.setText(translationService.removeWordSql(txtWord.getText(), txtDefinition.getText()));
    }
}