package com.example.demo222;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.ChessBoard;
import org.example.ChessPiece;
import org.example.Color;


public class ChessGameFX extends Application {
    private ChessBoard board;
    private boolean isWhiteTurn = true;
    private int selectedX = -1;
    private int selectedY = -1;
    private StackPane[][] squares;
    private Alert checkmateAlert;

    @Override
    public void start(Stage primaryStage) {
        board = new ChessBoard();

        GridPane chessBoard = new GridPane();
        chessBoard.setAlignment(Pos.CENTER);
        squares = new StackPane[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = new StackPane();
                square.setPrefSize(75, 75);

                if ((i + j) % 2 == 0) {
                    square.setStyle("-fx-background-color: white;");
                } else {
                    square.setStyle("-fx-background-color: gray;");
                }

                ChessPiece piece = board.getPiece(i, j);
                if (piece != null) {
                    ImageView imageView = new ImageView(new Image(piece.getImage(piece.GetColor())));
                    square.getChildren().add(imageView);
                }

                int x = i;
                int y = j;
                square.setOnMouseClicked(e -> isWhiteTurn = handleSquareClick(x, y , chessBoard) ? !isWhiteTurn : isWhiteTurn);

                squares[i][j] = square;
                chessBoard.add(square, j, i);
            }
        }

        Scene scene = new Scene(chessBoard, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess Game");
        primaryStage.show();
    }

    private boolean handleSquareClick(int x, int y, GridPane chessBoard) {
        // Проверка на шах и мат
        if (board.isCheckMate(isWhiteTurn ? Color.WHITE : Color.BLACK)) {
            showAlert("Game Over", "Checkmate! " + (isWhiteTurn ? "Black" : "White") + " wins!");
            closeGame(chessBoard);
            return false;
        }

        // Проверка на шах
        if (board.isCheck(isWhiteTurn ? Color.WHITE : Color.BLACK)) {
            showAlert("Check", "You are in check!");
            // Дополнительные действия для ситуации шаха, если требуются
        }

        // Если уже выбрана фигура для хода
        if (selectedX != -1 && selectedY != -1) {
            // Попытка выполнить ход
            boolean isValidMove = board.movePiece(selectedX, selectedY, x, y);

            if (isValidMove) {
                // Если ход ведет к шаху или не устраняет шах, отменяем его
                if (board.isCheck(isWhiteTurn ? Color.BLACK : Color.WHITE)) {
                    System.out.println("BE");
                    board.movePiece(x, y, selectedX, selectedY); // Откат хода
                    showAlert("Invalid Move", "This move puts you in check.");
                    resetSelection();
                    return true;
                }

                // Ход успешен, обновляем доску и сменяем хода
                updateBoard();
                isWhiteTurn = !isWhiteTurn;
                resetSelection();
                return true;
            } else {
                // Невалидный ход, выводим уведомление
                showAlert("Invalid Move", "This is an invalid move!");
                resetSelection();
                return true;
            }
        } else {
            // Выбор фигуры для хода
            ChessPiece selectedPiece = board.getPiece(x, y);
            if (selectedPiece != null && selectedPiece.GetColor() == (isWhiteTurn ? Color.WHITE : Color.BLACK)) {
                selectedX = x;
                selectedY = y;
                return true;
            }
        }

        resetSelection();
        return true;
    }

    private void resetSelection() {
        selectedX = -1;
        selectedY = -1;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeGame(GridPane chessBoard) {
        // Закрыть текущее окно
        Stage stage = (Stage) chessBoard.getScene().getWindow();
        stage.close();

        // Если требуется, можно добавить дополнительные действия перед закрытием,
        // например, показать сообщение с результатами игры или сохранить результаты игры.
    }

    private void updateBoard() {
        ChessPiece[][] updatedBoard = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = squares[i][j];
                square.getChildren().clear();

                ChessPiece piece = updatedBoard[i][j];
                if (piece != null) {
                    ImageView imageView = new ImageView(new Image(piece.getImage(piece.GetColor())));
                    square.getChildren().add(imageView);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}