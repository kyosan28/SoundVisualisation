package com.company;

/*
*  TODO: Печать спектрограмм в изображения
*  TODO: Печать спектрограммы в файл, разработка типа спектрограмм
*  TODO: Вывод всего файла в изображение
*  TODO: Преобразование в float массив
*  TODO: Придумать ещё TODO:)
* */


public class Main {

    public static void main(String[] args) {

	    if(args.length > 0 && args[0].equalsIgnoreCase("-w"))
            new Window();
        else {
            if (args.length > 0 && args[0].equalsIgnoreCase("--play")) {
                if(args.length > 1) {
                    JPlayer jp = new JPlayer(args[1]);
                }
                else {
                    JPlayer jp = new JPlayer("file.wav");
                }
            }
        }
    }
}
