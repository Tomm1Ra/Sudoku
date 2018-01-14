# Sudoku

https://github.com/Tomm1Ra/Sudoku

SudokuSolver class
SudokuSolver solves almost allkind of Sudokus.
Boxes inside board have to be rectangular.
Prefered usage in command line
java SudokuSolver -SudokuBoardNumbers@charcet/boxsize
example 6x6 sudoku board and 2x3 boxes.
java SudokuSolver -400002020030000400006000050060300005@123456/2,3
Or read sudoku file from disc.
java SudokuSolver FileName.txt

Special sudoku solvers, see more info in AreaBlockInfo.txt.
Usage same as in SudokuSolver class.
AntiKingSudokuSolver
AntiKnightSudokuSolver
EvenOddSudokuSolver
ExtraAreaSudokuSolver
HyperSudokuSolver
JigsawSudokuSolver
NonConsecutiveSudokuSolver

Overlapping sudoku solvers:
CrossSudokuSolver
FlowerSudokuSolver
Overlap4SudokuSolver
OverlapSudokuSolver
Samurai4SudokuSolver
SamuraiSudokuSolver
WindmillSudokuSolver

Sudoku class, only 9x9 sudoku, but more solving methods than in SudokuSolver.
Usage :
give file where unsolved sudoku is
java Sudoku filename

or sudoku string in command line after dash
java Sudoku -123ABC789...
java Sudoku -"120 456 789 456 .."

Spaces are ignored and chars not 1-9 are empty squares


SudokuN class
SudokuN.java supports all square sudokus from size 1 to n.
Usage same as in Sudoku.java plus You can define charset.
In file define charset using @+charset or in commandline after
sudoku string add @+charset
java SudokuN -ABCD00000000DCBA@ABCD
java SudokuN --@X

file example
@BXZY
--Y-
B---
---Z
-X--

or use in file space separated integer values from 1 to n
0 0 2 0 
4 0 0 0 
0 0 0 3
0 1 0 0 

or like this
 2  0  7  0  0 18  3  0  0  0  0 22  0  0 14 19  0  1  9 12 11 13  0 17 10
25  0  3  0  0 15 24  0 10  0 21  0  0  1 18  0 23  0 17  0  0  0  0  0  0 
etc..

SurvoSolver class
Solves (small) survo puzzles. recomended max size 4x5 numbers.
cli parameter are board numbers + sums from up lef to right bottom by rows.
Values after @ char tells board size without sums.
example solve survo puzzle

   A  B  C  D
1  *  *  *  * 38
2  *  *  *  * 29
3  *  *  *  * 46
4  *  *  *  * 23
  55 42 12 27

Command is (note space before @):
java SurvoSolver -"0 0 0 0 38 0 0 0 0 29 0 0 0 0 46 0 0 0 0 23 55 42 12 27 @4,4"
Output:
    .   .   .   .    38
    .   .   .   .    29
    .   .   .   .    46
    .   .   .   .    23

   55  42  12  27

   15  12   3   8    38
   13   9   2   5    29
   16  14   6  10    46
   11   7   1   4    23

   55  42  12  27
