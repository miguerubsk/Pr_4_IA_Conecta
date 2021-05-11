/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conecta;

/**
 *
 * @author José María Serrano
 * @author Cristóbal J. Carmona
 * @version 1.4 Departamento de Informática. Universidad de Jáen
 *
 * Inteligencia Artificial. 2º Curso. Grado en Ingeniería Informática
 *
 * Curso 2020-21: Se introducen obstáculos aleatorios
 *
 * Código original: * Lenin Palafox * http://www.leninpalafox.com
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Conecta extends JFrame implements ActionListener {

    // Número de turnos/movimientos
    private int movimiento = 0;
    // Turno (empieza jugador 1)
    private boolean turnoJ1 = true;
    // Jugador 2, CPU por defecto
    private boolean jugadorcpu = true;
    // Jugador 2, CPU aleatorio por defecto
    private boolean alfabeta = false;
    // Marca si el jugador pulsa sobre el tablero
    private boolean pulsado;

    // Jugadores
    public static final int JUGADOR1 = 1;
    public static final int JUGADOR2 = -1;
    public static final int JUGADOR0 = 2;
    public static final int VACIO = 0;

    // Parámetros
    // Número de filas
    private static final int filas = 6;
    // Número de columnas
    private static final int columnas = 7;
    // Número de fichas que han de alinearse para ganar
    private static final int conecta = 4;

    // Tablero de juego
    private Grid juego;
    //jugadores
    private Player player2;

    //menus y elementos de la GUI
    private final JMenuBar barra = new JMenuBar();
    private final JMenu archivo = new JMenu("Archivo");
    private final JMenu opciones = new JMenu("Opciones");
    private final JMenuItem Reiniciar = new JMenuItem("Reset");
    private final JMenuItem salir = new JMenuItem("Salir");
    private final JRadioButton p1h = new JRadioButton("Humano", true);
    private final JRadioButton p2h = new JRadioButton("Humano", false);
    private final JRadioButton p2c = new JRadioButton("CPU (Aleatorio)", true);
    private final JRadioButton p2c2 = new JRadioButton("CPU (IAPlayer)", false);
    // Leyendas y cabeceras
    private String cabecera = "Pr\u00e1cticas de Inteligencia Artificial (Curso 2020-21)";
    private final JLabel nombre = new JLabel(cabecera, JLabel.CENTER);
    private final String title = "Conecta4";

    /**
     * Constructor
     *
     * @param filas Número de filas
     * @param columnas Número de columnas
     * @param conecta Número de fichas consecutivas para ganar
     */
    /*
    public Conecta(int filas, int columnas, int conecta) {
        this.filas = filas;
        this.columnas = columnas;
        this.conecta = conecta;
    }
    */
    public Conecta( ) {

    }
    

    /**
     * Gestión de eventos y del transcurso de la partida
     *
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        // Eventos del menú Opciones
        if (ae.getSource() == p2h) {
            jugadorcpu = false; // Humano
            reset();
        }
        if(ae.getSource() == Reiniciar){
            reset();
        }
        if (ae.getSource() == p2c) {
            jugadorcpu = true; // CPU Random
            alfabeta = false;
            reset();
        }
        if (ae.getSource() == p2c2) {
            jugadorcpu = true; // CPU Alfa Beta;
            alfabeta = true;
            reset();
        }
        if (ae.getSource() == salir) {
            dispose();
            System.exit(0);
        }

        // Control del juego por el usuario
        int x;
        // Siempre empieza el jugador 1
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (ae.getSource() == juego.getJButton(i, j)) {
                    if (turnoJ1) {
                        x = juego.setButton(j, JUGADOR1);
                    } else {
                        x = juego.setButton(j, JUGADOR2);
                    }
                    // Comprobar si la jugada es válida
                    if (!(x < 0)) {
                        if (jugadorcpu) //Si es modo un jugador o dos
                        {
                            pulsado = true;
                        }
                        turnoJ1 = !turnoJ1;
                        movimiento++;
                        // Comprobar si acabó el juego
                        finJuego(juego.checkWin(x, j, conecta));

                        // Añadiendo ruido, cada 5 jugadas
//                        if ((movimiento > 0) && (movimiento % 5 == 0)) {
//                            // Cara o cruz
//                            if (Math.random() < 0.5) {
//                                if (juego.setButton(player2.getRandomColumn(juego), Conecta.JUGADOR0) >= 0) {
//                                    //JOptionPane.showMessageDialog(this, "¡Oh, oh! ¡Ha aparecido una ficha obst\u00e1culo!", "Conecta4", JOptionPane.INFORMATION_MESSAGE);
//                                    movimiento++;
//                                }
//                            }
//                        }
                    } // En otro caso, la columna ya está completa
                } // if
            } // for 2
        } // for 1       

        // Pasa el turno al jugador 2
        if (pulsado) {
            if (jugadorcpu) {
                pulsado = false;
                turnoJ1 = !turnoJ1;
                movimiento++;
                // Comprobar si acabó el juego
                finJuego(player2.jugada(juego, conecta));
            }
        }
        // Mostrar tablero tras cada movimiento
        juego.print();
        cabecera = "Movimientos: " + movimiento + " - Turno: ";
        if (turnoJ1) {
            cabecera += "Jugador 1";
        } else {
            cabecera += "Jugador 2";
        }
        nombre.setText(cabecera);

    } // actionPerformed         

//    /**
//     * Mostrar un mensaje del uso correcto del programa
//     *
//     * Indica qué parámetros debemos pasarle obligatoriamente al programa y cómo
//     */
//    private static void ayuda() {
//        System.out.println();
//        System.out.println();
//        System.out.println("Error en los par\u00e1metros de llamada");
//        System.out.println();
//        System.out.println("Uso: java [classpath] conectan.Conecta <filas> <columnas> <fichas>");
//        System.out.println();
//        System.out.println("[classpath]: Si es necesario, incluir el classpath, p.e.: -cp classes");
//        System.out.println("<filas>     : N\u00faúmero de filas del tablero. Entero. OBLIGATORIO. M\u00edn: 6");
//        System.out.println("<columnas>    : N\u00famero de columnas del tablero. Entero. OBLIGATORIO. M\u00edn: 7");
//        System.out.println("<fichas>   : N\u00famero de fichas seguidas para ganar la partida. Entero. OBLIGATORIO. M\u00edn: 4");
//        System.out.println();
//        // Salir
//        System.exit(0);
//    }

    public void finJuego(int ganador) {
        // Comprobamos si llegamos al final del juego
        // Empate!!!
        if (movimiento >= filas * columnas) {
            JOptionPane.showMessageDialog(this, "¡Empate!", "Conecta4", JOptionPane.INFORMATION_MESSAGE);
            reset();
        }
        switch (ganador) {
            case JUGADOR1:
                JOptionPane.showMessageDialog(this, "¡Ganador, Jugador 1\nen " + movimiento + " movimientos!", "Conecta4", JOptionPane.INFORMATION_MESSAGE, juego.getFicha1());
                System.out.println("Ganador: Jugador 1, en " + movimiento + " movimientos.");
                reset();
                break;
            case JUGADOR2:
                JOptionPane.showMessageDialog(this, "¡Ganador, Jugador 2\nen " + movimiento + " movimientos!", "Conecta4", JOptionPane.INFORMATION_MESSAGE, juego.getFicha2());
                System.out.println("Ganador: Jugador 2, en " + movimiento + " movimientos.");
                reset();
                break;
        }

    } // finJuego

    /**
     * Reinicia una partida
     */
    private void reset() {
        // Volver el programa al estado inicial	
        juego.reset();
        turnoJ1 = true;
        movimiento = 0;
        pulsado = false;

        System.out.println();
        System.out.println("Nueva partida:");
        System.out.println("Jugador 1: Humano");
        System.out.print("Jugador 2: ");
        if (jugadorcpu) {
            if (alfabeta) {
                player2 = new IAPlayer();
                System.out.println("CPU (IAPlayer)");
            } else {
                player2 = new IAPlayer();
                System.out.println("CPU (Modo aleatorio)");
            }
        } else {
            player2 = null;
            System.out.println("Humano");
        }
    } // reset

    /**
     * Configuración inicial
     *
     * Creación de la interfaz gráfica del juego
     */
    private void run() {

        juego = new Grid(filas, columnas, "assets/player1.png", "assets/player2.png", "assets/player0.png");
        int altoVentana = (filas + 1) * juego.getFicha1().getIconWidth();
        int anchoVentana = columnas * juego.getFicha1().getIconWidth();

        if (alfabeta) {
            player2 = new IAPlayer();
        } else {
            player2 = new IAPlayer();
        }

        //menu GUI
        salir.addActionListener(this);
        archivo.add(salir);
        // Player 1
        opciones.add(new JLabel("Jugador 1:"));
        ButtonGroup m1Jugador = new ButtonGroup();
        m1Jugador.add(p1h);
        // Player 2
        opciones.add(p1h);
        opciones.add(new JLabel("Jugador 2:"));
        p2h.addActionListener(this);
        p2c.addActionListener(this);
        p2c2.addActionListener(this);
        Reiniciar.addActionListener(this);
        ButtonGroup m2Jugador = new ButtonGroup();
        m2Jugador.add(p2h);
        m2Jugador.add(p2c);
        m2Jugador.add(p2c2);
        opciones.add(p2h);
        opciones.add(p2c);
        opciones.add(p2c2);

        barra.add(archivo);
        barra.add(opciones);
        barra.add(Reiniciar);
        setJMenuBar(barra);

        //Panel Principal 
        JPanel principal = new JPanel();
        principal.setLayout(new GridLayout(filas, columnas));

        //Colocar Botones
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                juego.initialize(i, j, this, Color.BLACK);
                principal.add(juego.getJButton(i, j));
            }
        }
        nombre.setForeground(Color.BLUE);
        add(nombre, "North");
        add(principal, "Center");

        //Para cerrar la Ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dispose();
                System.exit(0);
            }
        });

        //tamaño frame
        setLocation(170, 25);
        setSize(anchoVentana, altoVentana);
        setResizable(false);
        setTitle(title);
        setVisible(true);
    } // run

    /**
     * Método principal
     *
     * Lectura de parámetros desde línea de comandos e inicio del programa
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Conecta.java - 4 en Raya");
        System.out.println("-----------------------------------------");
        System.out.println("Inteligencia Artificial - Curso 2020-21");
        System.out.println("Dpto. Inform\u00e1tica - Universidad de Ja\u00e9n");
        try {
            //int filas = Integer.parseInt(args[0]);
            //int columnas = Integer.parseInt(args[1]);
            //int conecta = Integer.parseInt(args[2]);

            // Los parámetros mínimos del juego son: 
            // Mínimo conecta: 4
            // Mínimo alto: 6
            // Mínimo ancho: 7
            //if (filas < 6 || columnas < 7 || conecta < 4 || conecta > filas || conecta > columnas) {
                // Error en los parámetros
            //    ayuda();
            //}

            System.out.println();
            System.out.println("Nueva partida.");
            System.out.println();
            System.out.println("Estado inicial:");
            System.out.println("Jugador 1: Humano");
            System.out.println("Jugador 2: CPU (Modo aleatorio)");

            Conecta juego = new Conecta( );
            juego.run();

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            System.out.println(ex.getMessage());
            //ayuda();
        }
    } // main

} // Conecta
