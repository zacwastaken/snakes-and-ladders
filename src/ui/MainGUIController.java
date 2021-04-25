package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.*;

import java.net.URL;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable, CSSIDs {

    /****************************************FIELDS****************************************/

    /*Splash Screen*/
    @FXML
    private BorderPane preloaderPane;

    @FXML
    private Label progress;

    public static Label label;

    /*Main Pane*/

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button newGameBTN;

    @FXML
    private Button scoreBoardBTN;

    @FXML
    private Button eraseAllDataBTN;

    @FXML
    private Label mainTitleLBL;

    //New Game

    @FXML
    private ListView<String> playersLV;

    @FXML
    private TextField columnsTF;

    @FXML
    private TextField rowsTF;

    @FXML
    private TextField snakesTF;

    @FXML
    private TextField laddersTF;

    /*Game Pane*/

    @FXML
    private BorderPane boardPane = new BorderPane();

    @FXML
    private ImageView diceIMV;

    @FXML
    private TableView<Player> localLeaderboardTBV;

    @FXML
    private TableColumn<Player, String> nameColBoard;

    @FXML
    private TableColumn<Player, Integer> scoreColBoard;

    @FXML
    private Label timerLBL;

    //Board

    @FXML
    private GridPane boardGP = new GridPane();

    //Tiles

    @FXML
    private Label tileLBL = new Label();

    @FXML
    private AnchorPane tileAP = new AnchorPane();

    @FXML
    private ImageView specialTileLBL;

    /*Fields*/

    Board game = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label = progress;
        initializeMainMenu();
    }

    /***************************************METHODS***************************************/

    /*General*/

    private void initIDs() {
        mainTitleLBL.setId(titleLBLId);
        mainPane.setId(mainPaneID);
        newGameBTN.setId(mainPaneButtonsID);
        scoreBoardBTN.setId(mainPaneButtonsID);
        eraseAllDataBTN.setId(mainPaneButtonsID);
    }

    private void launchWindow(String fxml, String title, Modality modality) {
        try {
            Parent loadedPane = loadFxml(fxml);
            Stage stage = new Stage();
            stage.setScene(new Scene(loadedPane));
            Image icon = new Image(String.valueOf(getClass().getResource("resources/snl-logo.png")));
            stage.getIcons().add(icon);
            stage.getScene().getStylesheets().addAll(String.valueOf(getClass().getResource("css/main.css")));
            stage.setTitle(title);
            stage.initModality(modality);
            stage.setResizable(false);
            stage.show();
        } catch (NullPointerException npe) {
            System.out.println("Can't load requested window right now.\nRequested window: \"" + fxml + "\"");
            System.err.println(npe.getMessage());
        }
    }

    private Parent loadFxml(String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            fxmlLoader.setController(this);
            return fxmlLoader.load();
        } catch (Exception e) {
            System.out.println("Can't load requested document right now.\nRequested document: \"" + fxml + "\"");
            throw new NullPointerException("Document is null");
        }
    }

    /*Main Pane*/

    private void initializeMainMenu() {
        initIDs();
    }

    @FXML
    void eraseAllData(ActionEvent event) {

    }

    @FXML
    void newGame(ActionEvent event) {
        launchWindow("fxml/board/create-board.fxml", "Create new game", Modality.APPLICATION_MODAL);
    }

    //Pre Game

    @FXML
    void addPlayer(ActionEvent event) {

    }

    @FXML
    void startGame(ActionEvent event) {
        ((Stage) playersLV.getScene().getWindow()).close();
        ((Stage) mainPane.getScene().getWindow()).close();
        game = new Board(5, 6, 3, 3, 2);
        GridPane board = boardGP;
        board = gridProperties(board);
        board = initializeBoard(0, board, 0, 0);
        launchWindow("fxml/board/board-pane.fxml", "Now playing!", Modality.NONE);
        boardPane.setCenter(board);
    }

    GridPane gridProperties(GridPane grid) {
        grid.setAlignment(Pos.CENTER);
        tileAP.setMinSize(750.0/game.getColumns(),750.0/game.getRows());
        tileAP.setMaxSize(750.0/game.getColumns(),750.0/game.getRows());
        tileAP.setPrefSize(750.0/game.getColumns(),750.0/game.getRows());
        grid.setPrefSize(750, 750);
        grid.setMinSize(750, 750);
        grid.setMaxSize(750, 750);
        grid.setHgap(5);
        grid.setVgap(5);
        return grid;
    }

    GridPane initializeBoard(int i, GridPane board, int y, int x) {
        Label number = new Label(String.valueOf(game.getABox(i).getPosition()));
        number.setId("tile-numbers");
        tileAP.getChildren().add(number);
        if (i < game.getSize()) {
            Parent tile;
            if (x == game.getColumns()) {
                x = 0;
                y++;
            }
            System.out.println("-Column: " + (x + 1) + ", Row: " + (y + 1));
            //System.out.println(i+1);

            System.out.println(i+1);
            tile = loadFxml("fxml/board/tile.fxml");
            tile.setId((i % 2 != 0) ? "odd-tile" : "even-tile");
            board.add(tile,x,y);
            return initializeBoard(i + 1, board, y, x + 1);

        }

        return board;
    }

    @FXML
    void scoreBoard(ActionEvent event) {

    }

    /*Game Pane*/

    @FXML
    void restartGame(ActionEvent event) {

    }

    @FXML
    void rollDice(MouseEvent event) {

    }

    @FXML
    void endGame(ActionEvent event) {

    }

    @FXML
    void skipTurn(ActionEvent event) {

    }
}