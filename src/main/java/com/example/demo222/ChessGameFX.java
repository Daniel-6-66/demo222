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
                square.setOnMouseClicked(e -> isWhiteTurn = handleSquareClick(x, y) ? !isWhiteTurn : isWhiteTurn);

                squares[i][j] = square;
                chessBoard.add(square, j, i);
            }
        }

        Scene scene = new Scene(chessBoard, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess Game");
        primaryStage.show();
    }

    private boolean handleSquareClick(int x, int y) {
        if (selectedX == -1 && selectedY == -1) {
            ChessPiece selectedPiece = board.getPiece(x, y);
            if (selectedPiece != null && selectedPiece.GetColor() == (isWhiteTurn ? Color.WHITE : Color.BLACK)) {
                if (board.isCheck(selectedPiece.GetColor())) {

                    // Проверка, может ли выбранная фигура устранить шах
                    if (!board.canPieceDefendFromCheck(selectedPiece, x, y , isWhiteTurn ? Color.WHITE : Color.BLACK)) {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Stop");
                        alert2.setHeaderText(null);
                        alert2.setContentText("This move doesn't defend you!");
                        alert2.showAndWait();// Если фигура не может защитить от шаха, ход запрещен
                    }
                }
                selectedX = x;
                selectedY = y;
            }
        } else {
            boolean isValidMove = board.movePiece(selectedX, selectedY, x, y);

            if (isValidMove) {
                if (board.isCheck(!isWhiteTurn ? Color.WHITE : Color.BLACK)) {
                    // Если ход не устраняет шах, он недействителен
                    board.movePiece(x, y, selectedX, selectedY);
                    return false;
                }
                updateBoard();
                isWhiteTurn = !isWhiteTurn;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Move");
                alert.setHeaderText(null);
                alert.setContentText("This is an invalid move!");
                alert.showAndWait();
            }
            selectedX = -1;
            selectedY = -1;
        }
        return true;
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