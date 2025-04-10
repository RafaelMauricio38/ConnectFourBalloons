/* #REGION - IMPORTS */
import java.io.File
import java.io.FileWriter

/* #ENDREGION */

/* #REGION - INITIALIZE GLOBAL CONSTANT VARIABLES */
const val menuOptionsDesign = "1. Novo Jogo\n2. Gravar Jogo\n3. Ler Jogo\n0. Sair\n"

/* colors */
const val red = "\u001B[31m"
const val blue = "\u001B[34m"
const val resetColor = "\u001B[0m"

/* characters */
const val balloon = "\u03D9"
const val blueBalloon = "$blue$balloon$resetColor"
const val redBalloon = "$red$balloon$resetColor"

/* actions */
const val explodeAction = "explode"
/* #ENDREGION */


/* #REGION - REQUIRED FUNCTIONS */
fun criaTabuleiroVazio(numLinhas: Int, numColunas: Int): Array<Array<String?>> {
    /* returns a bi-dimensional array with the indicated number of rows and columns, where the positions are all
     filled with null */
    return Array(numLinhas) { Array(numColunas) { null } }
}

fun validaTabuleiro(numLinhas: Int, numColunas: Int): Boolean {
    /* validate that the number of rows and columns are within the correct ranges */
    return numLinhas in 5..7 && numColunas == numLinhas + 1
}

fun nomeValido(nome: String): Boolean {
    /* validate if username have the correct length */
    if (nome.length !in 3..12) {
        return false
    }

    /* initialize/declare/assign value to auxiliary variable */
    var idx = 0

    /* validate if username have space blanks */
    while (idx < nome.length) {
        if (nome[idx] == ' ') {
            return false
        }

        /* increment auxiliary variable */
        idx++
    }

    return true
}

fun criaTopoTabuleiro(numColunas: Int): String {
    /* validate if number of columns are within the correct range */
    if (numColunas !in 1..26) {
        return ""
    }

    /* initialize/declare/assign value to variables */
    var topOfBoard = "\u2554" /* to the first corner of the start of the board (with ╔) */
    var count = 0

    /* scroll through the columns to build the center of the top of the board (with ═) */
    while (count < (numColunas * 4) - 1) {
        topOfBoard += "\u2550"

        /* increment auxiliary variable */
        count++
    }

    /* finish the top of the board with the corner (with ╗) */
    topOfBoard += "\u2557"

    return topOfBoard
}

fun criaLegendaHorizontal(numColunas: Int): String {
    /* validate if number of columns are within the correct range */
    if (numColunas !in 1..26) {
        return ""
    }

    /* initialize/declare/assign value to variables */
    var horizontalCaption = " " /* initialize with a space blank to respect the required syntax */
    var count = 0

    /* scroll through the columns to build the horizontal caption, placing the correct letters */
    while (count < numColunas) {
//        val letter = 'A' + count
        horizontalCaption += " A "

        /* add separator "|" only between letters */
        if (count < numColunas - 1) {
            horizontalCaption += "|"
        }

        /* increment auxiliary variable */
        count++
    }

    /* add a space blank to respect the required syntax */
    horizontalCaption += " "

    return horizontalCaption
}

fun criaTabuleiro(board: Array<Array<String?>>, mostraLegenda: Boolean = true): String {
    /* initialize/declare/assign value to variables */
    var cntRows = 0
    val numRows = board.size /* get number of rows */
    val numColumns = if (board.isNotEmpty()) board[0].size else 0 /* get number of columns */
    var boardComplete = criaTopoTabuleiro(numColumns) + "\n" /* get the board top design */

    /* move through each row on the board */
    while (cntRows < numRows) {
        /* initialize/declare/assign value to variable */
        var cntColumn = 0

        /* move through each column on the board */
        while (cntColumn < (numColumns * 4)) {
            /* if it's the first column put ║ */
            if (cntColumn == 0) {
                boardComplete += "\u2551"
            }

            /* if it's the last column put ║ */
            if (cntColumn == (numColumns * 4) - 1) {
                boardComplete += "\u2551"
            } else if ((cntColumn + 1) % 4 == 0) { /* if it's divisible by 4 put | */
                boardComplete += "|"
            } else {
                /*  */
                val boardColumn = cntColumn / 4

                /* check if the element in the array isn't null (it's available) and if the cntColumn when divided by 4
                is 1 (to control the spacing/alignment of the board) */
                if (board[cntRows][boardColumn] != null && cntColumn % 4 == 1) {
                    /* get the balloon corresponding to the position and put it on the board visually */
                    val move = board[cntRows][boardColumn]
                    boardComplete += "$move"
                } else {
                    /* if nothing is checked, put a blank space */
                    boardComplete += " "
                }
            }

            /* increment auxiliary variable to move to another column */
            cntColumn++
        }

        /* checks if the current line isn't the last line (to avoid adding a new line after the last line) */
        if ((cntRows + 1) != numRows) {
            boardComplete += "\n"
        }

        /* increment auxiliary variable to move to another row */
        cntRows++
    }

    /* check if it is to show the board caption */
    if (mostraLegenda) {
        /* show the board caption */
        boardComplete += "\n" + criaLegendaHorizontal(numColumns)
    }

    return boardComplete
}

fun processaColuna(numColunas: Int, coluna: String?): Int? {
    /* initialize/declare/assign value to variables */
    var currentLetter = 'A'
    var idx = 0
    val lastLetter = currentLetter + (numColunas - 1)
    if (coluna == null) {
        return null
    }

    /* scroll through the letters (from A to the last letter of the table caption) and validate
     whether the letter entered corresponds to the letter of any column in the table */
    while (currentLetter <= lastLetter) {
        if (coluna == currentLetter.toString()) {
            return idx
        }

        /* increment auxiliary variables */
        currentLetter++
        idx++
    }

    return null
}

fun contaBaloesLinha(tabuleiro: Array<Array<String?>>, linha: Int): Int {
    /* returns the number of columns found in that row of the array that's different from null (is not available) */
    return tabuleiro[linha].count { it != null }
}

fun contaBaloesColuna(tabuleiro: Array<Array<String?>>, coluna: Int): Int {
    /* returns the number of rows found in that column of the array that's different from null (is not available) */
    return tabuleiro.count { it[coluna] != null }
}

fun colocaBalao(tabuleiro: Array<Array<String?>>, coluna: Int, humano: Boolean): Boolean {
    /* go through all the rows on the board, using the index of each row */

    for (row in tabuleiro.indices) {
        /* validate if there's still space in the row */
        if (contaBaloesLinha(tabuleiro, row) <= tabuleiro.size) {
            /* validate if the chosen position is free */
            if (tabuleiro[row][coluna] == null) {
                /* validate if it was a human or computer move */
                if (humano) {
                    /* save the balloon in the array at the chosen position */
                    tabuleiro[row][coluna] = redBalloon
                } else {
                    /* save the balloon in the array at the chosen position */
                    tabuleiro[row][coluna] = blueBalloon
                }
                /* move made successfully */
                return true
            }
        }
    }
    /* move not made successfully */
    return false
}

fun jogadaNormalComputador(tabuleiro: Array<Array<String?>>): Int {
    /* scroll through the index of each row on the board */
    for (row in tabuleiro.indices) {
        /* validate if there's still space in the row */
        if (contaBaloesLinha(tabuleiro, row) <= tabuleiro.size) {
            /* scroll through the index of each column on the board */
            for (column in tabuleiro[row].indices) {
                /* validate if the chosen position is free */
                if (tabuleiro[row][column] == null) {
                    /* return available column */
                    return column
                }
            }
            return -1
        }
    }
    return -1
}

fun eVitoriaHorizontal(tabuleiro: Array<Array<String?>>): Boolean {
    /* move through each row on the board */
    for (row in tabuleiro) {
        /* initialize/declare/assign value to variables */
        var balloonCounter = 1
        var lastBalloon: String? = null

        /* move through each column on the board */
        for (column in row) {
            /* validate that the column isn't null and that it's equal to the last balloon read */
            if (column != null && column == lastBalloon) {
                /* increment auxiliary variable - balloon counter in a row */
                balloonCounter++
                /* validate if 4 identical balloons were found */
                if (balloonCounter == 4) {
                    /* it's victory */
                    return true
                }
            } else {
                /* reset the balloon counter in a row and update the last balloon read */
                balloonCounter = 1
                lastBalloon = column
            }
        }
    }

    /* it's not victory */
    return false
}

fun eVitoriaVertical(tabuleiro: Array<Array<String?>>): Boolean {
    /* initialize/declare/assign value to variable */
    val numColumns = if (tabuleiro.isNotEmpty()) tabuleiro[0].size else 0 /* get number of columns */

    /* move through each column on the board */
    for (column in 0..<numColumns) {
        /* initialize/declare/assign value to variables */
        var balloonCounter = 1
        var lastBalloon: String? = null

        /* scroll through the index of each row on the board */
        for (row in tabuleiro.indices) {
            /* validate that the column isn't null and that it's equal to the last balloon read */
            if (tabuleiro[row][column] != null && tabuleiro[row][column] == lastBalloon) {
                /* increment auxiliary variable - balloon counter in a column */
                balloonCounter++
                /* validate if 4 identical balloons were found */
                if (balloonCounter == 4) {
                    /* it's victory */
                    return true
                }
            } else {
                /* reset the balloon counter in a column and update the last balloon read */
                balloonCounter = 1
                lastBalloon = tabuleiro[row][column]
            }
        }
    }

    /* it's not victory */
    return false
}

fun eVitoriaDiagonal(tabuleiro: Array<Array<String?>>): Boolean {
    /* initialize/declare/assign value to variables */
    val numRows = tabuleiro.size /* get number of rows */
    val numColumns = if (tabuleiro.isNotEmpty()) tabuleiro[0].size else 0 /* get number of columns */

    /* check diagonals from left to right */
    /* scroll through the rows until at least 3 more rows below the current position so as not to exceed the limits
     of the array */
    for (row in 0..<numRows - 3) {
        /* scroll through the columns until at least 3 more columns below the current position so as not to exceed the
        limits of the array */
        for (column in 0..<numColumns - 3) {
            /* get first value of diagonal */
            val firstValue = tabuleiro[row][column]
            /* validate that the value isn't null and that the 3 following elements on the diagonal are equal to the
             first */
            if (firstValue != null && firstValue == tabuleiro[row + 1][column + 1] &&
                firstValue == tabuleiro[row + 2][column + 2] && firstValue == tabuleiro[row + 3][column + 3]
            ) {
                /* it's victory */
                return true
            }
        }
    }

    /* check diagonals from right to left */
    /* scroll through the rows until at least 3 more rows below the current position so as not to exceed the limits
     of the array */
    for (row in 0..<numRows - 3) {
        /* scroll through all columns, from column 3 up to (not including) the total number of columns ensuring that
         there are at least 3 more columns to the left of the current position */
        for (column in 3..<numColumns) {
            /* get first value of diagonal */
            val firstValue = tabuleiro[row][column]
            /* validate that the value isn't null and that the next 3 elements (going down a row and moving a column
            to the left) are equal to the first value */
            if (firstValue != null && firstValue == tabuleiro[row + 1][column - 1] &&
                firstValue == tabuleiro[row + 2][column - 2] && firstValue == tabuleiro[row + 3][column - 3]
            ) {
                /* it's victory */
                return true
            }
        }
    }

    /* it's not victory */
    return false
}

fun ganhouJogo(tabuleiro: Array<Array<String?>>): Boolean {
    /* validate if there was a victory in any of the directions and return this information (won or not) */
    return eVitoriaHorizontal(tabuleiro) || eVitoriaVertical(tabuleiro) || eVitoriaDiagonal(tabuleiro)
}

fun eEmpate(tabuleiro: Array<Array<String?>>): Boolean {
    for (row in tabuleiro) {
        for (column in row) {
            if (column == null) {
                return false
            }
        }
    }
    return true
}

fun gravaJogo(nomeFicheiro: String, tabuleiro: Array<Array<String?>>, nomeJogador: String) {
    /* initialize/declare/assign value to variables */
    val file = File(nomeFicheiro) /* create file with the indicated name */
    val writer = FileWriter(file) /* enable editing of the file */

    /* save the username in file */
    writer.write(nomeJogador + "\n")

    /* go through all the rows on the board, using the index of each row */
    for (row in tabuleiro.indices) {
        var columnCount = 0
        /* go through each column of that row of the board */
        for (column in tabuleiro[row]) {
            /* check what is in the column */

            when (column) {

                redBalloon -> {
                    if (columnCount != tabuleiro[row].size - 1) {
                        writer.write("H,")
                        columnCount++
                    } else {
                        writer.write("H")
                        columnCount++
                    }
                } /* if it's a red balloon, write H in the file */

                blueBalloon -> {
                    if (columnCount != tabuleiro[row].size - 1) {
                        writer.write("C,")
                        columnCount++
                    } else {
                        writer.write("C")
                        columnCount++
                    }
                }/* if it's a blue balloon, write C in the file */

                else -> {
                    if (columnCount != tabuleiro[row].size - 1) {
                        writer.write(",")
                        columnCount++
                    } else {
                        writer.write("")
                        columnCount++
                    }
                }/* if the column is empty, write a blank space in the file */

            }
        }
        /* give a row break after reading each row on the board */
        writer.write("\n")
    }
    /* disable file editing */
    writer.close()
    /* success information message */
    println("Tabuleiro ${tabuleiro.size}x${tabuleiro[0].size} gravado com sucesso")
}

fun leJogo(nomeFicheiro: String): Pair<String, Array<Array<String?>>> {
    /* initialize/declare/assign value to variable */
    val file = File(nomeFicheiro) /* open the indicated file in memory (so that it can be read) */

    /* check if the file exists */
    if (!file.exists()) {
        /* error information message */
        println("Ficheiro '$nomeFicheiro' nao encontrado.")
        return Pair("", emptyArray())
    }

    /* initialize/declare/assign value to variable */
    val allLines = file.readLines() /* read and save information from each line of the file */

    /* check if the file is empty */
    if (allLines.isEmpty()) {
        /* error information message */
        println("Ficheiro esta vazio.")
        return Pair("", emptyArray())
    }

    /* initialize/declare/assign value to variable */
    val username = allLines.first() /* get username */
    val lines = allLines.drop(1) /* exclude the first line of the file because it's the one with the username */
    /* go through each line of the file, transforming the information, separated by a comma, into elements,
    excluding empty elements */
    /* scroll through each element and convert to the respective balloons */
    /* save everything in a two-dimensional array (game board) */
    val board: Array<Array<String?>> = lines.map { line ->
        line.split(",").map { value ->
            when (value.trim()) {
                "H" -> redBalloon
                "C" -> blueBalloon
                "" -> null
                else -> null
            }
        }.toTypedArray()
    }.toTypedArray()

    /* return the username and associated board */
    return Pair(username, board)
}


fun arrebenta(tabuleiro: Array<Array<String?>>, coordenadas: Pair<Int, Int>): Pair<Int, Int> {
    val column = coordenadas.second
    val row = coordenadas.first

    tabuleiro[row][column] = null

    for (r in row + 1 until tabuleiro.size) {
        tabuleiro[r - 1][column] = tabuleiro[r][column] // Move para a posição acima
        tabuleiro[r][column] = null
    }

    return coordenadas
}

fun explodeBalao(tabuleiro: Array<Array<String?>>, coordenadas: Pair<Int, Int>): Boolean {
    if (tabuleiro.all { row -> row.all { it == null } }) {
        println("Funcionalidade Explodir nao esta disponivel")
        return false
    }

    if (tabuleiro[coordenadas.first][coordenadas.second] == null) {
        println("Coluna vazia")
        return false
    }

    arrebenta(tabuleiro, coordenadas)

    return true
}

fun jogadaExplodirComputador(tabuleiro: Array<Array<String?>>): Pair<Int, Int> {
    val balloonsRedByColumn = Array(tabuleiro[0].size) { 0 }

    for (row in tabuleiro.indices) {
        var countRed = 0
        for (column in tabuleiro[row].indices) {
            if (tabuleiro[row][column] == redBalloon) {
                countRed++
                if (countRed == 3) {
                    val targetCol = maxOf(0, column - 2)
                    return Pair(row, targetCol)
                }
            } else {
                countRed = 0
            }
        }
    }

    for (column in tabuleiro[0].indices) {
        var countRed = 0
        for (row in tabuleiro.indices) {
            if (tabuleiro[row][column] == redBalloon) {
                countRed++
                if (countRed == 3) {
                    val targetRow = maxOf(0, row - 2)
                    return Pair(targetRow, column)
                }
            } else {
                countRed = 0
            }
        }
    }

    for (col in tabuleiro[0].indices) {
        balloonsRedByColumn[col] = tabuleiro.count { row -> row[col] == redBalloon }
    }

    val nonZeroColumns = balloonsRedByColumn.filter { it != 0 }
    val allEqualColumns = nonZeroColumns.isNotEmpty() && nonZeroColumns.all { it == nonZeroColumns[0] }

    return if (allEqualColumns) {
        val minBalloons = balloonsRedByColumn.filter { it != 0 }.minOrNull()
        val targetCol = balloonsRedByColumn.indexOfLast { it == minBalloons }
        for (row in tabuleiro.indices.reversed()) {
            if (tabuleiro[row][targetCol] == redBalloon) {
                return Pair(row, targetCol)
            }
        }
        Pair(-1, -1)
    } else {
        val maxBalloons = balloonsRedByColumn.maxOrNull()
        val targetCol = balloonsRedByColumn.indexOfLast { it == maxBalloons }
        for (row in tabuleiro.indices) {
            if (tabuleiro[row][targetCol] == redBalloon) {
                return Pair(row, targetCol)
            }
        }
        Pair(-1, -1)
    }
}

fun eQuaseVitoriaVertical(tabuleiro: Array<Array<String?>>, linhaInicial: Int, coluna: Int): Boolean {
    /* initialize/declare/assign value to variables */
    var balloonCounter = 1
    var lastBalloon: String? = null

    if (linhaInicial + 3 > tabuleiro.size - 1 || coluna > tabuleiro[0].size - 1) {
        return false
    }

    /* scroll through the index of each row on the board */
    for (row in linhaInicial..linhaInicial + 3) {
        /* validate that the column isn't null and that it's equal to the last balloon read */
        if (tabuleiro[row][coluna] != null && tabuleiro[row][coluna] == lastBalloon) {
            /* increment auxiliary variable - balloon counter in a column */
            balloonCounter++
            /* validate if 3 identical balloons were found */
            if (balloonCounter == 3) {
                if (row < linhaInicial + 3 && tabuleiro[row + 1][coluna] == null) {
                    /* it's almost victory */
                    return true
                }
            }
        } else {
            /* reset the balloon counter in a column and update the last balloon read */
            balloonCounter = 1
            lastBalloon = tabuleiro[row][coluna]
        }
    }

    return false
}

fun eQuaseVitoriaHorizontal(tabuleiro: Array<Array<String?>>, linha: Int, colunaInicial: Int): Int? {
    /* initialize/declare/assign value to variables */
    var balloonCounter = 1
    var lastBalloon: String? = null

    if(linha > tabuleiro.size-1){
        return -1
    }

    if (colunaInicial + 2 > tabuleiro[0].size - 1) {
        return null
    }

    /* move through each column on the board */
    for (column in colunaInicial..colunaInicial + 2) {
        /* validate that the column isn't null and that it's equal to the last balloon read */
        if (tabuleiro[linha][column] != null && tabuleiro[linha][column] == lastBalloon) {
            /* increment auxiliary variable - balloon counter in a row */
            balloonCounter++
            /* validate if 3 identical balloons were found */
            if (balloonCounter == 2) {
                if (column < colunaInicial + 2 && tabuleiro[linha][column + 1] == null) {
                    /* it's almost victory */
                    return column + 1
                }
            }
        } else {
            /* reset the balloon counter in a row and update the last balloon read */
            balloonCounter = 1
            lastBalloon = tabuleiro[linha][column]
        }
    }

    /* it's not almost victory */
    return null
}

fun calculaEstatisticas(tabuleiro: Array<Array<String?>>): Array<Int> {
    val statistics = Array(4) { 0 }

    for (row in tabuleiro.indices) {
        for (column in tabuleiro[row].indices) {
            when (tabuleiro[row][column]) {
                redBalloon -> {
                    statistics[2]++
                }

                blueBalloon -> {
                    statistics[1]++
                }
            }
        }
    }

    for (row in tabuleiro.indices) {
        for (column in tabuleiro.indices) {
            if (eQuaseVitoriaHorizontal(tabuleiro, row, column) != null) {
                statistics[3]++
            }
        }
    }

    for (column in tabuleiro[0].indices) {
        if (eQuaseVitoriaVertical(tabuleiro, 0, column)) {
            statistics[3]++
        }
    }

    statistics[0] = statistics[1] + statistics[2]

    for(r in tabuleiro.indices){
        if(tabuleiro[r][0] != null){
            statistics[0]++
        }
    }

    return statistics
}

fun sugestaoJogadaNormalHumano(tabuleiro: Array<Array<String?>>): Int? {
    for (row in tabuleiro.indices) {
        for (column in tabuleiro[row].indices) {
            // humano
            if (eQuaseVitoriaVertical(tabuleiro, row, column)) {
                return column
            }
        }
    }

    for (row in tabuleiro.indices) {
        //pc
        val idx = eQuaseVitoriaHorizontal(tabuleiro, row, 0)
        if (idx != null) {
            return idx
        }
    }
    return null
}

/* #ENDREGION */

/* #REGION - OUR FUNCTIONS */
/* function to get menu in console */
fun getMenu(optionNumber: Int? = null) {
    /* initialize/declare/assign value to variable */
    var gameStarted = false
    var board = criaTabuleiroVazio(0, 0)
    var userName = ""
    //var menuRunning = true

    /* validate if an option was passed - if it's, don't show the menu again */
    if (optionNumber == null) {
        /* draw menu */
        println(menuOptionsDesign)
    }

    do {
        /* get option */
        val option = optionNumber ?: readln().toIntOrNull()
        /* set functionality of each option */
        when (option) {
            1 -> {
                gameStarted = true
                /* get option 1 code */
                val result = getOption1(null)
                if (result.first != null) {
                    if (result.second == "sair") {
                        println()
                        println(menuOptionsDesign)
                        //menuRunning = false
                    }
                    /* get board */
                    board = result.first?.first!!
                    /* get username */
                    userName = result.first?.second!!
                    if (result.second == "save") {
                        getOption2(board, userName)
                        /* indicate that a game has already been launched - it's allowed to make the game save functionality
                                available */
                        println()
                        println(menuOptionsDesign)
                    }
                    if (result.second == "win" || result.second == "tie") {
                        println(menuOptionsDesign)
                    }
                }
            }

            2 -> {
                if (optionNumber != null) {
                    gameStarted = true
                }
                /* validate if a game has already started - to be able to save something */
                if (!gameStarted) {
                    /* error information message */
                    println("Funcionalidade Gravar nao esta disponivel")
                } else {
                    /* get option 2 code */
                    getOption2(board, userName)
                }
            }

            3 -> {
                /* get option 3 code */
                val result = getOption3()
                if (result.first != null) {
                    if (result.second == "sair") {
                        println()
                        println(menuOptionsDesign)
                        //menuRunning = false
                    }
                    /* get board */
                    board = result.first?.first!!
                    /* get username */
                    userName = result.first?.second!!
                    if (result.second == "save") {
                        getOption2(board, userName)
                        /* indicate that a game has already been launched - it's allowed to make the game save functionality
                                available */
                    }
                }
            }

            0 -> {
                /* information message */
                println("A sair...")
                //menuRunning = false
            }

            else -> {
                /* error information message */
                println("Opcao invalida. Por favor, tente novamente.")
            }
        }
    } while (option != 0) /* keep asking for an option until the option to exit is introduced (0) */
}

/* function to validate number of rows insert by user */
fun validateRows(numRows: Int): Boolean {
    /* validate if user put a valid number of rows */
    return numRows > 0
}

/* function to validate number of columns insert by user */
fun validateColumns(numColumns: Int): Boolean {
    /* validate if user put a valid number of columns */
    return numColumns > 0
}

/* function to get the last column letter of caption */
fun getLastColumnLetter(caption: String): Char {
    return caption[caption.length - 3].uppercaseChar() /* get the last column letter of caption in upper case */
}

/* function to get valid number of rows */
fun getRows(): Int {
    /* initialize/declare variable */
    var numRows: Int

    do {
        /* get number of rows */
        println("Numero de linhas:")
        numRows = readln().toIntOrNull() ?: -1
        /* validate row */
        val isValidRow = validateRows(numRows)
        if (!isValidRow) {
            println("Numero invalido")
        }
    } while (!isValidRow) /* keep asking for a number of lines until a number is valid */

    /* return number of rows */
    return numRows
}

/* function to get valid number of columns */
fun getColumns(): Int {
    /* initialize/declare variable */
    var numColumns: Int

    do {
        /* get number of columns */
        println("Numero de colunas:")
        numColumns = readln().toIntOrNull() ?: -1
        /* validate column */
        val isValidColumn = validateColumns(numColumns)
        if (!isValidColumn) {
            println("Numero invalido")
        }
    } while (!isValidColumn) /* keep asking for a number of columns until a number is valid */

    /* return number of columns */
    return numColumns
}

/* function to validate a combination of rows and columns that result in a valid board */
fun validateBoard(): Pair<Int, Int> {
    /* initialize/declare variables */
    var numRows: Int
    var numColumns: Int

    do {
        /* get rows */
        numRows = getRows()

        /* get columns */
        numColumns = getColumns()

        /* validate board */
        val isValidBoard = validaTabuleiro(numRows, numColumns)
        if (!isValidBoard) {
            println("Tamanho do tabuleiro invalido")
        }
    } while (!isValidBoard) /* keep asking for a set of valid rows and columns until there is a combination
                            corresponding to a valid board */

    /* return number of rows and columns */
    return Pair(numRows, numColumns)
}

/* function to get valid username */
fun getUsername(): String {
    /* initialize/declare variable */
    var userName: String

    do {
        /* get username of player */
        println("Nome do jogador 1:")
        userName = readln()
        /* validate username */
        val isValidUserName = nomeValido(userName)
        if (!isValidUserName) {
            println("Nome de jogador invalido")
        }
    } while (!isValidUserName) /* keep asking for a username until gets a valid username */

    /* return username */
    return userName
}

/* function to get the chosen column letter index */
fun getLetterIdx(chosenColumnLetter: String): Int {
    /* initialize/declare variable */
    var idx = 0

    /* scroll through all the letters until you reach the chosen one */
    for (letter in 'A'..chosenColumnLetter[0].uppercaseChar()) {
        /* validate if we are facing the chosen letter */
        if (letter != chosenColumnLetter[0].uppercaseChar()) {
            /* increase the auxiliary variable */
            idx++
        }
    }

    /* return the index */
    return idx
}

fun getLetterByIdx(idx: Int): String {
    /* initialize/declare variable */
    var idxAux = 0
    var letterByIdx = ""

    /* scroll through all the letters until you reach the chosen one */
    for (letter in 'A'..'Z') {
        if (idxAux == idx) {
            letterByIdx = letter.toString()
        }
        idxAux++
    }

    return letterByIdx
}

/* function to get the letter of the column where the balloon was placed */
fun getColumnLetter(board: Array<Array<String?>>, isHuman: Boolean): String? {
    /* initialize/declare/assign value to variables */
    val numRows = board.size /* get number of rows */
    val numColumns = if (board.isNotEmpty()) board[0].size else 0 /* get number of columns */
    val lastColumnLetter =
        getLastColumnLetter(criaLegendaHorizontal(numColumns)) /* get last column letter from caption */
    var columnIdx: Int? = null
    var balloonPlaced: Boolean
    val invalidCol = "Coluna invalida"

    do {
        /* validate if it's a human or computer move */
        if (isHuman) {
            /* get column letter */
            println("Coluna? (A..$lastColumnLetter):")
            var chosenColumnLetter = readln()

            /* validate that the column parameter isn't null or empty */
            if (chosenColumnLetter.isNotEmpty()) {
                /* validate if the "SAIR" command was entered */
                if (chosenColumnLetter.uppercase() == "SAIR") {
                    /* return to menu */
                    return "sair"
                }

                if (chosenColumnLetter[0] == '?') {
                    val idxSuggestion = sugestaoJogadaNormalHumano(board)
                    if (idxSuggestion != null) {
                        println("Sugestao de jogada na coluna: ${getLetterByIdx(idxSuggestion)}")
                    } else {
                        println("Nao existe uma sugestao de jogada")
                    }
                }

                if (chosenColumnLetter.uppercase().contains("EXPLODIR")) {
                    val result = handleExplodirActionInternal(chosenColumnLetter, board, invalidCol, lastColumnLetter)
                    if (result != null) {
                        return result
                    }
                }

                if (chosenColumnLetter.uppercase() == "GRAVAR") {
                    return "save"
                }
                if (chosenColumnLetter[0].uppercaseChar() !in 'A'..lastColumnLetter && chosenColumnLetter[0] != '?') {
                    println(invalidCol)
                    chosenColumnLetter = ""
                    return null
                }
                if (contaBaloesColuna(board, getLetterIdx(chosenColumnLetter)) >= numRows) {
                    chosenColumnLetter = ""
                }

                columnIdx = processaColuna(numColumns, chosenColumnLetter)
            }
            else{
                println(invalidCol)
            }
        } else {
            /* get the index of the selected column, if this is greater than or equal to 0, if it doesn't get a null
             value */
            columnIdx = jogadaNormalComputador(board).takeIf { it >= 0 }
        }

        /* validate that the column index isn't null (it's valid) */
        if (columnIdx == null) {
            /* error information message */
            return null
        }

        /* get if the balloon was placed on the board */
        balloonPlaced = colocaBalao(board, columnIdx, isHuman)
        /* validate that the balloon was placed on the board */
        if (!balloonPlaced) {
            /* error information message */
            println("Erro ao armazenar balao. Tente novamente.")
        }
    } while (!balloonPlaced) /* keep asking for a column until the balloon is placed in a valid column */

    /* return the letter of the column where the balloon was placed */
    return ('A' + columnIdx!!).toString().uppercase() /* !! - indicates to the program that despite being a nullable
                                                      variable, when that part of the code arrives, the variable will
                                                      have a value for sure */
}

fun handleExplodirActionInternal(chosenColumnLetter: String, board: Array<Array<String?>>, invalidCol: String, lastColumnLetter: Char): String? {
    /* return to menu */
    if (chosenColumnLetter.length != 10) {
        println(invalidCol)
        return null
    }
    if (chosenColumnLetter[9].uppercaseChar() !in 'A'..lastColumnLetter) {
        println(invalidCol)
        return null
    }

    if (board.count { true } >= 2) {
        if (explodeBalao(board, Pair(0, getLetterIdx(chosenColumnLetter[9].toString())))) {
            println("Balao ${chosenColumnLetter[9].uppercaseChar()} explodido!")
            /* get board */
            println(criaTabuleiro(board))
            println()
            println("Prima enter para continuar. O computador ira agora explodir um dos seus baloes")
            val keyPress = readln()
            if (keyPress.isEmpty()) {
                val coordenadas = jogadaExplodirComputador(board)
                if (explodeBalao(board, Pair(coordenadas.first, coordenadas.second))) {
                    val letterExplode = getLetterByIdx(coordenadas.second)
                    println("Balao ${letterExplode},${coordenadas.first + 1} explodido pelo Computador!")
                    println(criaTabuleiro(board))
                    println()
                    return explodeAction
                } else {
                    return null
                }
            }
            return explodeAction
        } else {
            return null
        }
    } else {
        println("Funcionalidade Explodir nao esta disponivel")
    }

    return null
}

/* function to perform the human move (user's) */
fun move(board: Array<Array<String?>>, userName: String?): Pair<Boolean?, String> {
    /* initialize/declare/assign value to variables */
    val numRows = board.size /* get number of rows */
    val numColumns = if (board.isNotEmpty()) board[0].size else 0 /* get number of columns */
    var columnLetter: String?

    /* validate if it's a human or computer move */
    /* !the variable reset color is necessary to return the console to writing in the original color! */
    var playerLabel =
        if (userName != null) "$userName: $redBalloon" else "Computador: $blueBalloon"

    /* write player identifier */
    println(playerLabel)
    /* write board settings */
    println("Tabuleiro $numRows" + "X$numColumns")

    do {
        /* get column letter */
        columnLetter = getColumnLetter(board, userName != null)
        if (columnLetter == "sair") {
            return Pair(null, columnLetter)
        }
        if (columnLetter == "save") {
            return Pair(null, columnLetter)
        }
        if(columnLetter == explodeAction){
            columnLetter = null
            /* validate if it's a human or computer move */
            /* !the variable reset color is necessary to return the console to writing in the original color! */
            playerLabel =
                if (userName != null) "$userName: $redBalloon" else "Computador: $blueBalloon"

            /* write player identifier */
            println(playerLabel)
            /* write board settings */
            println("Tabuleiro $numRows" + "X$numColumns")
        }
        else if (columnLetter != null) {
            /* write chosen column letter */
            println("Coluna escolhida: $columnLetter")
            /* get board */
            println(criaTabuleiro(board))
            println()
        }
    } while (columnLetter == null) /* keep asking for the column until you have a non-null column letter (valid) */

    /* validate if the user won the game */
    if (ganhouJogo(board)) {
        if (userName != null) {
            /* success information message */
            println("Parabens $userName! Ganhou!\n")
        } else {
            println("Perdeu! Ganhou o Computador.\n")
        }
        /* return to menu */
        return Pair(true, "win")
    }

    /* validate if there was a tie */
    if (eEmpate(board)) {
        /* information message */
        println("Empate!\n")
        /* return to menu */
        return Pair(true, "tie")
    }

    return Pair(false, columnLetter)
}

/* function to get the functionality of option 1 */
fun getOption1(gameSaved: Pair<Array<Array<String?>>, String>?): Pair<Pair<Array<Array<String?>>, String>?, String> {
    /* initialize/declare/assign value to variables */
    var gameSituation: Pair<Boolean?, String> = Pair(false, "")
    val board: Array<Array<String?>>
    var userName = ""

    if (gameSaved == null) {
        /* !(numRows, numColumns) - it's called destructuring, as the function returns a Pair, each variable is assigned
    the respective returned value! */
        val (numRows, numColumns) = validateBoard() /* validate board dimensions */
        userName = getUsername() /* get the username */
        board = criaTabuleiroVazio(numRows, numColumns) /* create an empty board */
    } else {
        board = gameSaved.first
        userName = gameSaved.second
    }

    /* create the board visually in the console */
    println(criaTabuleiro(board))
    println()

    /* game loop - continues until victory or draw is found */
    while (gameSituation.first == false) {
        /* user turn (human) */
        gameSituation = move(board, userName)
        if (gameSituation.first == null) {
            return Pair(Pair(board, userName), gameSituation.second)
        }

        /* check if the human managed to win */
        if (gameSituation.first == false && gameSituation.second != explodeAction) {
            /* computer turn */
            gameSituation = move(board, null)
        }
    }

    /* Return the board and the username */
    return Pair(Pair(board, userName), gameSituation.second)
}

/* function to get the functionality of option 2 */
fun getOption2(board: Array<Array<String?>>, userName: String) {
    /* get file name to save the game */
    println("Introduza o nome do ficheiro (ex: jogo.txt)")
    val fileName = readln().ifEmpty { "jogo.txt" } /* if the file name is null, the file is saved with the default
                                                        name */
    /* save the game to file */
    gravaJogo(fileName, board, userName)
}

/* function to validate if the game is valid */
fun isValidGame(userName: String, board: Array<Array<String?>>): Boolean {
    /* return if both information taken from the file (username and board) contain information */
    return !(userName.isEmpty() || board.isEmpty())
}

/* function to get the functionality of option 3 */
fun getOption3(): Pair<Pair<Array<Array<String?>>, String>?, String> {
    /* get file name */
    println("Introduza o nome do ficheiro (ex: jogo.txt)")
    val fileName = readln()
    /* !(userName, board) - it's called destructuring, as the function returns a Pair, each variable is assigned
    the respective returned value! */
    val (userName, board) = leJogo(fileName) /* read the game from the indicated file */

    /* validate if the information obtained from the file is a valid match */
    if (!isValidGame(userName, board)) {
        return Pair(Pair(emptyArray(), ""), "")
    }

    /* initialize/declare/assign value to variables */
    val numRows = board.size
    val numColumns = board[0].size

    /* success information message */
    println("Tabuleiro ${numRows}x${numColumns} lido com sucesso!")
    val gameSaved = Pair(board, userName)
    /* return the board and username */
    return getOption1(gameSaved)
}
/* #ENDREGION */

fun main() {
    println("\nBem-vindo ao jogo \"4 Baloes em Linha\"!\n")
    getMenu()
}