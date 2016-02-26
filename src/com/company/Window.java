package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Window extends JFrame {

    private Component inWindowComponent = null;
    private Canvas drawing_place;
    private Recorder rec;
    private JPlayer reader;


    public Window() {
        setSize(1500,700);

        setDefaultCloseOperation(3);

        JMenuBar menu = new JMenuBar();

        JMenu menu_file = new JMenu("Файл");
        JMenuItem open = new JMenuItem("Открыть");
        JMenuItem save = new JMenuItem("Сохранить");
        JMenuItem start_record = new JMenuItem("Начать запись");
        JMenuItem suspend_record = new JMenuItem("Приостановить запись");
        JMenuItem stop_record = new JMenuItem("Остановка записи");
        JMenuItem close_window = new JMenuItem("Закрыть");

        JMenu menu_analyze = new JMenu("Анализ");
        JMenuItem draw_graphic = new JMenuItem("Показать файл");
        JMenuItem spectre1 = new JMenuItem("Показать спектр 1");
        JMenuItem spectre2 = new JMenuItem("Показать спектр 2");

        menu.add(menu_file);
        menu.add(menu_analyze);

        menu_file.add(open);
        menu_file.add(save);
        menu_file.add(start_record);
        menu_file.add(suspend_record);
        menu_file.add(stop_record);
        menu_file.add(close_window);

        menu_analyze.add(draw_graphic);
        menu_analyze.add(spectre1);
        menu_analyze.add(spectre2);

        setJMenuBar(menu);

        start_record.addActionListener(e -> {
            if (rec == null)
                rec = new Recorder();
            else rec.init();
            if (inWindowComponent != null)
                this.remove(inWindowComponent);
            inWindowComponent = recordButtons();
            add(inWindowComponent);
            this.pack();

            rec.start();
        });

        stop_record.addActionListener(e -> rec.pause());

        suspend_record.addActionListener(e -> rec.pause());

        open.addActionListener(e -> {
            JFrame file_open = new JFrame("Открыть");
            file_open.setDefaultCloseOperation(HIDE_ON_CLOSE);

            JFileChooser jf = new JFileChooser(new File("."));
            jf.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return -1 != file.toString().toLowerCase().lastIndexOf(".wav") || file.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "Звуковые";
                }
            });
            jf.addActionListener(e1 -> {
                if (e1.paramString().substring(e1.paramString().lastIndexOf("cmd=") + 4, e1.paramString().lastIndexOf(",when=")).equals("ApproveSelection")) {
                    String fname = e1.toString().substring(e1.toString().lastIndexOf("selectedFile=") + 13, e1.toString().lastIndexOf(",useFileHiding"));
                    try {
                        if (fname.length() < 2)
                            throw new Exception("No file name exception");

                        if (!new File(fname).exists()) throw new Exception("File not exists");
                        if (inWindowComponent != null)
                            this.remove(inWindowComponent);
                        inWindowComponent = renderPanel();
                        add(inWindowComponent);

                        if(reader!=null)reader=null;
                        if(-1 != fname.toLowerCase().lastIndexOf(".wav"))
                            reader=new JPlayer(fname);
                        drawing_place.setData(reader.read());

                        file_open.hide();
                        putDataInCanvas();
                        setSize(1500,701);

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                //Deprecated
                else file_open.hide();

                resize(getSize());
            });

            file_open.setVisible(true);
            file_open.add(jf);
            file_open.pack();

        });
        //ends open action listener

        save.addActionListener(e -> {
            JFrame file_save = new JFrame("Сохранить как");
            file_save.setDefaultCloseOperation(HIDE_ON_CLOSE);

            JFileChooser jf = new JFileChooser();
            jf.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return -1 != file.toString().toLowerCase().lastIndexOf(".wav") || file.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "Звуковые";
                }
            });
            jf.setApproveButtonText("Save");
            jf.addActionListener(e1 -> {
                if (e1.paramString().substring(e1.paramString().lastIndexOf("cmd=") + 4, e1.paramString().lastIndexOf(",when=")).equals("ApproveSelection")) {
                    String fname = e1.toString().substring(e1.toString().lastIndexOf("selectedFile=") + 13, e1.toString().lastIndexOf(",useFileHiding"));
                    try {
                        if (fname.length() < 2)
                            throw new Exception("No file name exception");

                        if (new File(fname).exists()) {
                            int res = JOptionPane.showConfirmDialog(file_save, "Вы действительно хотите перезаписать файл " + fname);
                            switch (res) {
                                case JOptionPane.YES_OPTION:
                                    if (rec == null)
                                        throw new Exception("Nothing to save");
                                    else rec.writeToFile(fname);
                                    break;

                                default:
                                    file_save.hide();
                            }
                        } else {
                            if (rec == null)
                                throw new Exception("Nothing to save");
                            else rec.writeToFile(fname);
                        }

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                //Deprecated
                else file_save.hide();
            });

            file_save.setVisible(true);
            file_save.add(jf);
            file_save.pack();
        });
        //ends save action listener

        inWindowComponent=renderPanel();
        add(inWindowComponent);
        setVisible(true);

    }

    private void putDataInCanvas(){
        if(reader==null || drawing_place==null)throw new NullPointerException();
        drawing_place.setType(Canvas.Type.sound);
    }
    Timer canvUpdater=null;
    public JPanel renderPanel() {
        JPanel render_panel = new JPanel(new BorderLayout());
        JSlider progress_bar = new JSlider();
        JButton play = new JButton(" ");
        progress_bar.setValue(0);
        if (drawing_place == null) drawing_place = new Canvas();

        JPanel south=new JPanel(new BorderLayout());

        south.add(progress_bar,BorderLayout.CENTER);
        south.add(play,BorderLayout.WEST);
        play.setPreferredSize(new Dimension(32, 32));
        try {
            play.setIcon(new ImageIcon(ImageIO.read(new File("resources/icons/start16х16.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }


        render_panel.add(south,BorderLayout.SOUTH);
        render_panel.add(drawing_place,BorderLayout.CENTER);

        if(canvUpdater!=null)canvUpdater.stop();
        canvUpdater= new Timer(200, e -> {
            if (drawing_place != null)
                progress_bar.setMaximum(drawing_place.setSliderData(progress_bar.getValue())+1);
        });
        canvUpdater.start();
        return render_panel;
    }


    private JPanel recordButtons() {
        JPanel record_panel = new JPanel(new GridLayout(2, 1)),
                record_panel_top = new JPanel(new GridLayout(1, 1)),
                record_panel_bottom = new JPanel(new GridLayout(1, 3));

        JTextField recorded = new JTextField("");


        JButton start_record_button = new JButton(" ");
        JButton pause_record_button = new JButton(" ");
        JButton stop_record_button = new JButton(" ");
        try {
            start_record_button.setIcon(new ImageIcon(ImageIO.read(new File("resources/icons/record.png"))));
            pause_record_button.setIcon(new ImageIcon(ImageIO.read(new File("resources/icons/pause.png"))));
            stop_record_button.setIcon(new ImageIcon(ImageIO.read(new File("resources/icons/stop.png"))));

        } catch (IOException e) {
            e.printStackTrace();
        }


        record_panel.add(record_panel_top);
        record_panel.add(record_panel_bottom);

        record_panel_top.add(recorded);
        record_panel_bottom.add(start_record_button);
        record_panel_bottom.add(pause_record_button);
        record_panel_bottom.add(stop_record_button);

        Timer timeInTextFieldUpdater = new Timer(1000 / 60, e -> recorded.setText(rec.getElapsedTime().toString(TimeElapsed.Format.WITHMS)));
        timeInTextFieldUpdater.start();

        pause_record_button.addActionListener(e -> rec.pause());

        stop_record_button.addActionListener(e -> rec.pause());

        return record_panel;
    }

}