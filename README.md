# Sudoku
Sudoku solver

Usage :
give file where unsolved sudoku is
java Sudoku filename

or sudoku string in command line after dash
java Sudoku -123ABC789...
java Sudoku -"120 456 789 456 .."

Spaces are ignored and chars not 1-9 are empty squares

SudokuN.java supports all square sudokus fmom size 1 to n.
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
0	0	2 	0 
4 	0	0	0 
0	0	0	3
0	1 	0	0 

or like this
2 	0	7 	0	0	18 	3 	0	0	0	0	22 	0	0	14 	19 	0	1 	9 	12 	11 	13 	0	17 	10
25 	0	3 	0	0	15 	24 	0	10 	0	21 	0	0	1 	18 	0	23 	0	17 	0	0	0	0	0	0 
etc..

