package textpad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Textpad extends Application {

    private Stage primaryStage;
    private TextArea text;
    private long textSize = 0;
    File mainFile ; 
    
    

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        BorderPane root = new BorderPane();

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem openMenuItem = new MenuItem("Open..");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem saveAsMenuItem = new MenuItem("Save As..");
        MenuItem exitMenuItem = new MenuItem("Exit");
        
        

        Menu editMenu = new Menu("Edit");
        MenuItem undoMenuItem = new MenuItem("Undo");
        MenuItem cutMenuItem = new MenuItem("Cut");
        MenuItem copyMenuItem = new MenuItem("Copy");
        MenuItem pasteMenuItem = new MenuItem("Paste");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem selectAllMenuItem = new MenuItem("Select All");

        Menu helpMenu = new Menu("Help");
        MenuItem aboutMenuItem = new MenuItem("About Textpad");

        fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem,
                saveAsMenuItem,new SeparatorMenuItem(), exitMenuItem);

        editMenu.getItems().addAll(undoMenuItem, new SeparatorMenuItem(),
                cutMenuItem, copyMenuItem, pasteMenuItem, deleteMenuItem,
                new SeparatorMenuItem(), selectAllMenuItem);
        helpMenu.getItems().addAll(aboutMenuItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        text = new TextArea();
        text.setPrefRowCount(10);
        text.setPrefColumnCount(100);
        text.setWrapText(true);
        text.setPrefWidth(150);
        
      

        undoMenuItem.setOnAction(event -> {
            text.undo();
        });

        copyMenuItem.setOnAction(event -> {
            text.copy();
        });

        cutMenuItem.setOnAction(event -> {
            text.cut();
        });

        pasteMenuItem.setOnAction(event -> {
            text.paste();
        });

        selectAllMenuItem.setOnAction(event -> {
            text.selectAll();
        });

        deleteMenuItem.setOnAction(event -> {
            IndexRange selection = text.getSelection();
            text.deleteText(selection);
        });

        aboutMenuItem.setOnAction(event -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("About textPad One");
            alert.setHeaderText("TextPad One");
            alert.setContentText("TextPad One 2017 V1 \nBuild by JavaFX \nBy Motyim");

            alert.showAndWait();
        });

        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        newMenuItem.setOnAction((event) -> {
           
            if(!checkTextStatus())
                return ;

            text.setText("");
            primaryStage.setTitle("TextPad One");
            textSize = 0;
            mainFile = null ; 
        });

        //save option
        saveMenuItem.setOnAction((event) -> {

            save();

        });
        
         saveAsMenuItem.setOnAction((event) -> {

            saveAs();

        });

        openMenuItem.setOnAction((event) -> {
            
            if(!checkTextStatus())
                return ;
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt")
            );
            
            File file = fileChooser.showOpenDialog(primaryStage);
            
            if (file == null ) return ;
            
            mainFile = file ;
            
            primaryStage.setTitle("TextPad One | " + file.getName());
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                text.setText(everything);
                textSize = text.getText().length();
                
                //set text cursor in last line
                text.positionCaret((int) textSize);
                
            } catch (FileNotFoundException ex) {
               ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        root.setCenter(text);
        
        //add ShortCut
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));      
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        cutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        pasteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        selectAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        
        Scene scene = new Scene(root, 800, 550);
        
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            //ask before closing window 
            if(!checkTextStatus()) // if cancle will stay on textPad
                we.consume();
        });
        primaryStage.setTitle("TextPad One");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * save last update on latest file opened
     */
    private void save(){
        //file exsits 
        if(mainFile == null)
            saveAs();
        else{
            saveFile(mainFile);
        }
    }

    /**
     * save new updates on new File 
     */
    private void saveAs() {
        
        FileChooser fileChooser = new FileChooser();
        
        //handle file Extensio save as txt
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        fileChooser.setTitle("Save File");
        

        File file = fileChooser.showSaveDialog(primaryStage);
        
        //not choosen File
        if(file == null) return ; 
        mainFile = file; 
        
        saveFile(file);
    }
    
    /**
     * save last update on file 
     * @param file 
     */
    private void saveFile(File file){
        primaryStage.setTitle("TextPad One | " + file.getName());

        textSize = text.getText().length();

        //TODO handle .txt .txt
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8"))) {

            String[] split = text.getText().split("\n");
            for (String string : split) {
                writer.append(string);
                writer.append(System.lineSeparator());
            }

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * check if there is a modify in text status 
     * and make operation according this status
     * @return -true if user choose yes [save file ] or no <br>
     *          -false if user choose cancel
     */
    private boolean checkTextStatus() {
         //check size of last save and current text length 
            if (textSize != text.getText().length()) {
                Alert alert = new Alert(AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

                alert.setTitle("TextPad One");
                alert.setHeaderText("Do You Want to Save Changes ?");

                //get user result
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    //save file 
                    save();
      
                } else if (result.get() == ButtonType.CANCEL) {
                    //cancle save file and return 
                    return false;
                }

            }
            return true;
    }

}
